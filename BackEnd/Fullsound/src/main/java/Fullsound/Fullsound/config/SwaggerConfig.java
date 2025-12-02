package Fullsound.Fullsound.config;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.ArrayList;
import java.util.List;
@Configuration
public class SwaggerConfig {
    @Value("${spring.application.name:FullSound Backend API}")
    private String applicationName;
    @Value("${server.port:8080}")
    private String serverPort;
    @Bean
    public OpenAPI customOpenAPI() {
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
    private Contact apiContact() {
        return new Contact()
                .name("VECTORG99")
                .email("vectorg99@fullsound.com")
                .url("https://github.com/VECTORG99/FULLSOUND-SPRINGBOOT");
    }
    private License apiLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }
    private List<Server> apiServers() {
        List<Server> servers = new ArrayList<>();
        
        // Servidor dinámico - usa la URL de la request actual
        servers.add(new Server()
            .url("")
            .description("Servidor Actual"));
        
        // Servidores estáticos de respaldo
        servers.add(new Server()
            .url("http://localhost:" + serverPort)
            .description("Servidor Local (Desarrollo)"));
        
        servers.add(new Server()
            .url("http://localhost:8080")
            .description("Servidor Docker Local"));
        
        return servers;
    }
}
