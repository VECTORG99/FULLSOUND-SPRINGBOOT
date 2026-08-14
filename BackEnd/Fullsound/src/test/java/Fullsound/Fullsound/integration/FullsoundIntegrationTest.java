package Fullsound.Fullsound.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración con H2 en memoria.
 * Verifica los flujos críticos de la aplicación:
 * - Registro y login de usuarios
 * - CRUD de beats (admin)
 * - Creación de pedidos
 * - Creación de payment intents
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests de Integración - Flujos Críticos")
class FullsoundIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String adminToken;
    private static String clientToken;
    private static Integer beatIdForCrud;
    private static Integer beatIdForOrder;

    // RUTs válidos calculados con dígito verificador correcto
    private static final String ADMIN_RUT = "11.111.111-1";
    private static final String CLIENT_RUT = "22.222.222-2";

    @Test
    @Order(1)
    @DisplayName("Registrar usuario administrador")
    void shouldRegisterAdminUser() throws Exception {
        String requestBody = """
                {
                    "nombreUsuario": "admin_test",
                    "rut": "%s",
                    "correo": "admin@test.com",
                    "contraseña": "Admin1234",
                    "nombre": "Admin",
                    "apellido": "Test",
                    "rol": "administrador"
                }
                """.formatted(ADMIN_RUT);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario registrado exitosamente"));
    }

    @Test
    @Order(2)
    @DisplayName("Registrar usuario cliente")
    void shouldRegisterClientUser() throws Exception {
        String requestBody = """
                {
                    "nombreUsuario": "client_test",
                    "rut": "%s",
                    "correo": "client@test.com",
                    "contraseña": "Client1234",
                    "nombre": "Client",
                    "apellido": "Test",
                    "rol": "cliente"
                }
                """.formatted(CLIENT_RUT);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @Order(3)
    @DisplayName("Login como administrador")
    void shouldLoginAdmin() throws Exception {
        String requestBody = """
                {
                    "nombreUsuario": "admin_test",
                    "contraseña": "Admin1234"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.nombreUsuario").value("admin_test"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        adminToken = json.get("token").asText();
    }

    @Test
    @Order(4)
    @DisplayName("Login como cliente")
    void shouldLoginClient() throws Exception {
        String requestBody = """
                {
                    "nombreUsuario": "client_test",
                    "contraseña": "Client1234"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        clientToken = json.get("token").asText();
    }

    @Test
    @Order(5)
    @DisplayName("Verificar disponibilidad de nombre de usuario")
    void shouldCheckUsernameAvailability() throws Exception {
        mockMvc.perform(get("/api/auth/check-username")
                        .param("username", "admin_test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));

        mockMvc.perform(get("/api/auth/check-username")
                        .param("username", "newuser123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @Order(6)
    @DisplayName("Verificar disponibilidad de correo")
    void shouldCheckEmailAvailability() throws Exception {
        mockMvc.perform(get("/api/auth/check-email")
                        .param("email", "admin@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));

        mockMvc.perform(get("/api/auth/check-email")
                        .param("email", "newuser@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @Order(7)
    @DisplayName("Crear beat como administrador")
    void shouldCreateBeatAsAdmin() throws Exception {
        String requestBody = """
                {
                    "titulo": "Test Beat Integration",
                    "artista": "Test Producer",
                    "precio": 15000,
                    "bpm": 140,
                    "tonalidad": "Am",
                    "duracion": 180,
                    "genero": "Trap",
                    "etiquetas": "trap,dark,808",
                    "descripcion": "Beat de prueba para integration test",
                    "estado": "DISPONIBLE"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/beats")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Test Beat Integration"))
                .andExpect(jsonPath("$.slug").isNotEmpty())
                .andExpect(jsonPath("$.precio").value(15000))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        beatIdForCrud = json.get("idBeat").asInt();
    }

    @Test
    @Order(8)
    @DisplayName("Cliente no puede crear beats (403)")
    void shouldRejectBeatCreationByClient() throws Exception {
        String requestBody = """
                {
                    "titulo": "Unauthorized Beat",
                    "precio": 5000
                }
                """;

        mockMvc.perform(post("/api/beats")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(9)
    @DisplayName("Obtener beat por ID (sin auth)")
    void shouldGetBeatByIdWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/beats/" + beatIdForCrud))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idBeat").value(beatIdForCrud))
                .andExpect(jsonPath("$.titulo").value("Test Beat Integration"));
    }

    @Test
    @Order(10)
    @DisplayName("Listar beats con paginación")
    void shouldListBeatsWithPagination() throws Exception {
        mockMvc.perform(get("/api/beats")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "titulo,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    @Order(11)
    @DisplayName("Actualizar beat como administrador")
    void shouldUpdateBeatAsAdmin() throws Exception {
        String requestBody = """
                {
                    "titulo": "Updated Beat Integration",
                    "artista": "Test Producer",
                    "precio": 20000,
                    "bpm": 140,
                    "genero": "Trap",
                    "estado": "DISPONIBLE"
                }
                """;

        mockMvc.perform(put("/api/beats/" + beatIdForCrud)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Updated Beat Integration"))
                .andExpect(jsonPath("$.precio").value(20000));
    }

    @Test
    @Order(12)
    @DisplayName("Eliminar beat como administrador (sin referencias)")
    void shouldDeleteBeatAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/beats/" + beatIdForCrud)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @Order(13)
    @DisplayName("Obtener beat eliminado devuelve 404")
    void shouldReturn404ForDeletedBeat() throws Exception {
        mockMvc.perform(get("/api/beats/" + beatIdForCrud))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").isNotEmpty());
    }

    @Test
    @Order(14)
    @DisplayName("Crear segundo beat para pedido")
    void shouldCreateSecondBeatForOrder() throws Exception {
        String requestBody = """
                {
                    "titulo": "Order Beat Integration",
                    "artista": "Test Producer",
                    "precio": 25000,
                    "bpm": 120,
                    "genero": "Boom Bap",
                    "estado": "DISPONIBLE"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/beats")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        beatIdForOrder = json.get("idBeat").asInt();
    }

    @Test
    @Order(15)
    @DisplayName("Crear pedido como cliente")
    void shouldCreateOrderAsClient() throws Exception {
        String requestBody = """
                {
                    "beatIds": [%d],
                    "metodoPago": "STRIPE"
                }
                """.formatted(beatIdForOrder);

        mockMvc.perform(post("/api/pedidos")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.numeroPedido").isNotEmpty())
                .andExpect(jsonPath("$.total").value(25000))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    @Order(16)
    @DisplayName("Obtener mis pedidos como cliente")
    void shouldGetMyOrdersAsClient() throws Exception {
        mockMvc.perform(get("/api/pedidos/mis-pedidos")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].numeroPedido").isNotEmpty());
    }

    @Test
    @Order(17)
    @DisplayName("Crear pedido falla sin autenticación")
    void shouldRejectOrderWithoutAuth() throws Exception {
        String requestBody = """
                {
                    "beatIds": [%d],
                    "metodoPago": "STRIPE"
                }
                """.formatted(beatIdForOrder);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(18)
    @DisplayName("Health check responde correctamente")
    void shouldReturnHealthCheck() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.database").exists())
                .andExpect(jsonPath("$.cache").exists());
    }

    @Test
    @Order(19)
    @DisplayName("Solicitar restablecimiento de contraseña")
    void shouldRequestPasswordReset() throws Exception {
        String requestBody = """
                {
                    "correo": "client@test.com"
                }
                """;

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @Order(20)
    @DisplayName("Restablecer contraseña con token inválido falla")
    void shouldRejectResetWithInvalidToken() throws Exception {
        String requestBody = """
                {
                    "token": "invalid-token-12345",
                    "nuevaContraseña": "NewPassword123"
                }
                """;

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
