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
 * Tests de integración para las funcionalidades avanzadas (Round 3):
 * - Búsqueda de beats (full-text con fallback LIKE en H2)
 * - Favoritos / wishlist de usuarios
 * - Reseñas / valoraciones de beats
 * - Notificaciones in-app
 *
 * Usa H2 en memoria (Flyway deshabilitado, ddl-auto=create-drop).
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests de Integración - Funcionalidades Avanzadas (Round 3)")
class AdvancedFeaturesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String adminToken;
    private static String clientToken;
    private static Integer beatId;

    // RUTs válidos calculados con dígito verificador correcto
    private static final String ADMIN_RUT = "33.333.333-3";
    private static final String CLIENT_RUT = "44.444.444-4";
    // IP distinta para aislar el rate-limit (5 intentos/min/IP) del otro test
    // de integración que comparte el mismo contexto de Spring.
    private static final String FORWARD_IP = "10.10.10.10";

    private String register(String username, String rut, String email, String role) throws Exception {
        String body = """
                {
                    "nombreUsuario": "%s",
                    "rut": "%s",
                    "correo": "%s",
                    "contraseña": "Test1234",
                    "nombre": "%s",
                    "apellido": "Test",
                    "rol": "%s"
                }
                """.formatted(username, rut, email, username, role);
        mockMvc.perform(post("/api/auth/register")
                        .header("X-Forwarded-For", FORWARD_IP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
        return body;
    }

    private String login(String username) throws Exception {
        String body = """
                {
                    "nombreUsuario": "%s",
                    "contraseña": "Test1234"
                }
                """.formatted(username);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", FORWARD_IP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    @Order(1)
    @DisplayName("Registrar y loguear usuarios para tests avanzados")
    void shouldRegisterAndLoginUsers() throws Exception {
        register("adv_admin", ADMIN_RUT, "advadmin@test.com", "administrador");
        register("adv_client", CLIENT_RUT, "advclient@test.com", "cliente");
        adminToken = login("adv_admin");
        clientToken = login("adv_client");
        assertNotNull(adminToken);
        assertNotNull(clientToken);
    }

    @Test
    @Order(2)
    @DisplayName("Crear beat para tests avanzados")
    void shouldCreateBeatForAdvancedTests() throws Exception {
        String body = """
                {
                    "titulo": "Advanced Trap Beat",
                    "artista": "Advanced Producer",
                    "precio": 18000,
                    "bpm": 140,
                    "genero": "Trap",
                    "etiquetas": "trap,dark,808",
                    "descripcion": "Beat oscuro de trap para tests avanzados",
                    "estado": "DISPONIBLE"
                }
                """;
        MvcResult result = mockMvc.perform(post("/api/beats")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.calificacionPromedio").exists())
                .andExpect(jsonPath("$.totalResenas").exists())
                .andExpect(jsonPath("$.totalFavoritos").exists())
                .andReturn();
        beatId = objectMapper.readTree(result.getResponse().getContentAsString()).get("idBeat").asInt();
    }

    // ==================== BÚSQUEDA ====================

    @Test
    @Order(3)
    @DisplayName("Buscar beats por término (fallback LIKE en H2)")
    void shouldSearchBeats() throws Exception {
        mockMvc.perform(get("/api/beats/search")
                        .param("q", "trap"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    @Order(4)
    @DisplayName("Buscar beats por artista")
    void shouldSearchBeatsByArtist() throws Exception {
        mockMvc.perform(get("/api/beats/search")
                        .param("q", "Advanced Producer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(5)
    @DisplayName("Búsqueda sin resultados devuelve página vacía")
    void shouldReturnEmptyPageForNoMatch() throws Exception {
        mockMvc.perform(get("/api/beats/search")
                        .param("q", "zzznoexistezzz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ==================== FAVORITOS ====================

    @Test
    @Order(6)
    @DisplayName("Añadir beat a favoritos como cliente")
    void shouldAddBeatToFavorites() throws Exception {
        mockMvc.perform(post("/api/usuarios/me/favorites/" + beatId)
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @Order(7)
    @DisplayName("Añadir favorito duplicado no falla (idempotente)")
    void shouldNotFailOnDuplicateFavorite() throws Exception {
        mockMvc.perform(post("/api/usuarios/me/favorites/" + beatId)
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @Order(8)
    @DisplayName("Listar favoritos del usuario")
    void shouldListFavorites() throws Exception {
        mockMvc.perform(get("/api/usuarios/me/favorites")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].idBeat").value(beatId));
    }

    @Test
    @Order(9)
    @DisplayName("Favoritos requieren autenticación")
    void shouldRejectFavoritesWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/usuarios/me/favorites"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(10)
    @DisplayName("Eliminar beat de favoritos")
    void shouldRemoveBeatFromFavorites() throws Exception {
        mockMvc.perform(delete("/api/usuarios/me/favorites/" + beatId)
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/usuarios/me/favorites")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ==================== RESEÑAS ====================

    @Test
    @Order(11)
    @DisplayName("Crear reseña de un beat como cliente")
    void shouldCreateReview() throws Exception {
        String body = """
                {
                    "rating": 5,
                    "comentario": "Beat increíble, calidad de estudio"
                }
                """;
        mockMvc.perform(post("/api/beats/" + beatId + "/reviews")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comentario").value("Beat increíble, calidad de estudio"))
                .andExpect(jsonPath("$.beatId").value(beatId))
                .andExpect(jsonPath("$.nombreUsuario").value("adv_client"));
    }

    @Test
    @Order(12)
    @DisplayName("Listar reseñas de un beat (público)")
    void shouldListReviews() throws Exception {
        mockMvc.perform(get("/api/beats/" + beatId + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rating").value(5));
    }

    @Test
    @Order(13)
    @DisplayName("El beat muestra calificación promedio y total de reseñas")
    void shouldShowAverageRatingInBeat() throws Exception {
        mockMvc.perform(get("/api/beats/" + beatId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calificacionPromedio").value(5.0))
                .andExpect(jsonPath("$.totalResenas").value(1));
    }

    @Test
    @Order(14)
    @DisplayName("Crear reseña requiere autenticación")
    void shouldRejectReviewWithoutAuth() throws Exception {
        String body = """
                {
                    "rating": 4,
                    "comentario": "Sin auth"
                }
                """;
        mockMvc.perform(post("/api/beats/" + beatId + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(15)
    @DisplayName("Reseña con rating inválido (6) es rechazada")
    void shouldRejectInvalidRating() throws Exception {
        String body = """
                {
                    "rating": 6,
                    "comentario": "Invalid"
                }
                """;
        mockMvc.perform(post("/api/beats/" + beatId + "/reviews")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(16)
    @DisplayName("Actualizar reseña existente (upsert) cambia el rating")
    void shouldUpsertReview() throws Exception {
        String body = """
                {
                    "rating": 3,
                    "comentario": "Cambié de opinión"
                }
                """;
        mockMvc.perform(post("/api/beats/" + beatId + "/reviews")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(3));

        mockMvc.perform(get("/api/beats/" + beatId + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ==================== NOTIFICACIONES ====================

    @Test
    @Order(17)
    @DisplayName("Admin crea notificación para cliente")
    void shouldAdminCreateNotification() throws Exception {
        // Obtener el id del cliente desde /me
        MvcResult meResult = mockMvc.perform(get("/api/usuarios/me")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andReturn();
        Integer clientId = objectMapper.readTree(meResult.getResponse().getContentAsString()).get("id").asInt();

        String body = """
                {
                    "usuarioId": %d,
                    "tipo": "SISTEMA",
                    "mensaje": "Bienvenido a FullSound"
                }
                """.formatted(clientId);
        mockMvc.perform(post("/api/notifications")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("SISTEMA"))
                .andExpect(jsonPath("$.leido").value(false));
    }

    @Test
    @Order(18)
    @DisplayName("Cliente lista sus notificaciones")
    void shouldClientListNotifications() throws Exception {
        mockMvc.perform(get("/api/usuarios/me/notifications")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @Order(19)
    @DisplayName("Cliente obtiene conteo de notificaciones no leídas")
    void shouldClientGetUnreadCount() throws Exception {
        mockMvc.perform(get("/api/usuarios/me/notifications/unread-count")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(1));
    }

    @Test
    @Order(20)
    @DisplayName("Marcar notificación como leída")
    void shouldMarkNotificationAsRead() throws Exception {
        MvcResult listResult = mockMvc.perform(get("/api/usuarios/me/notifications")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode content = objectMapper.readTree(listResult.getResponse().getContentAsString()).get("content");
        Integer notifId = content.get(0).get("id").asInt();

        mockMvc.perform(put("/api/notifications/" + notifId + "/read")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leido").value(true));

        mockMvc.perform(get("/api/usuarios/me/notifications/unread-count")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(0));
    }

    @Test
    @Order(21)
    @DisplayName("Notificaciones requieren autenticación")
    void shouldRejectNotificationsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/usuarios/me/notifications"))
                .andExpect(status().isUnauthorized());
    }

    // ==================== ELIMINAR RESEÑA ====================

    @Test
    @Order(22)
    @DisplayName("Eliminar reseña propia")
    void shouldDeleteOwnReview() throws Exception {
        MvcResult listResult = mockMvc.perform(get("/api/beats/" + beatId + "/reviews"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode arr = objectMapper.readTree(listResult.getResponse().getContentAsString());
        Integer reviewId = arr.get(0).get("id").asInt();

        mockMvc.perform(delete("/api/reviews/" + reviewId)
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/beats/" + beatId + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private static void assertNotNull(Object o) {
        org.junit.jupiter.api.Assertions.assertNotNull(o);
    }
}
