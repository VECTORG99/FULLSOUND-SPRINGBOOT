package Fullsound.Fullsound.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de Swagger/OpenAPI para la documentación de la API REST.
 * 
 * Acceso a la documentación:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - API Docs (JSON): http://localhost:8080/api-docs
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-30
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:FullSound Backend API}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Configura OpenAPI 3.0 con información de la API, servidores y seguridad JWT.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Definir esquema de seguridad JWT
        final String securitySchemeName = "Bearer Authentication";
        
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                    new Components()
                        .addSecuritySchemes(securitySchemeName,
                            new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresa el token JWT obtenido del endpoint /api/auth/login")
                        )
                );
    }

    /**
     * Información general de la API.
     */
    private Info apiInfo() {
        return new Info()
                .title("🎵 FullSound API - Marketplace de Beats Musicales")
                .description(
                    "API RESTful para el marketplace de beats musicales FullSound. " +
                    "Permite la gestión de usuarios, beats, pedidos y pagos.\n\n" +
                    "**Características principales:**\n" +
                    "- ✅ Autenticación JWT\n" +
                    "- ✅ CRUD completo de beats\n" +
                    "- ✅ Gestión de pedidos y compras\n" +
                    "- ✅ Integración con Stripe para pagos\n" +
                    "- ✅ Panel de administración\n" +
                    "- ✅ Roles de usuario (cliente, productor, administrador)\n\n" +
                    "**Base de datos:** PostgreSQL\n" +
                    "**Framework:** Spring Boot 3.5.7\n" +
                    "**Java:** 17+"
                )
                .version("2.0.0")
                .contact(apiContact())
                .license(apiLicense());
    }

    /**
     * Información de contacto.
     */
    private Contact apiContact() {
        return new Contact()
                .name("VECTORG99")
                .email("vectorg99@fullsound.com")
                .url("https://github.com/VECTORG99/FULLSOUND-SPRINGBOOT");
    }

    /**
     * Licencia de la API.
     */
    private License apiLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    /**
     * Servidores disponibles (local, desarrollo, producción).
     */
    private List<Server> apiServers() {
        return List.of(
            new Server()
                .url("http://localhost:" + serverPort)
                .description("Servidor Local (Desarrollo)"),
            
            new Server()
                .url("http://localhost:8080")
                .description("Servidor Docker Local"),
            
            new Server()
                .url("https://api.fullsound.com")
                .description("Servidor de Producción (AWS)")
        );
    }
}
