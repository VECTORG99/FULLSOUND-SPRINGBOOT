package Fullsound.Fullsound.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuración para cargar variables de entorno desde el archivo .env
 * al iniciar la aplicación Spring Boot.
 */
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        try {
            // Buscar el .env en múltiples ubicaciones
            String[] locations = {
                "../../",           // Raíz del repositorio
                "../../../",        // Alternativa
                "./"               // Directorio actual
            };
            
            Dotenv dotenv = null;
            for (String location : locations) {
                File envFile = new File(location + ".env");
                if (envFile.exists()) {
                    dotenv = Dotenv.configure()
                            .directory(location)
                            .ignoreIfMissing()
                            .load();
                    System.out.println("✓ Archivo .env encontrado en: " + envFile.getAbsolutePath());
                    break;
                }
            }
            
            if (dotenv == null) {
                System.err.println("⚠ No se encontró el archivo .env");
                return;
            }
            
            // Convertir las variables del .env a un mapa
            Map<String, Object> dotenvMap = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                dotenvMap.put(entry.getKey(), entry.getValue());
                System.out.println("  → " + entry.getKey() + " cargada");
            });
            
            // Agregar las variables al entorno de Spring
            environment.getPropertySources()
                    .addFirst(new MapPropertySource("dotenvProperties", dotenvMap));
            
            System.out.println("✓ Variables de entorno cargadas desde .env");
            
        } catch (Exception e) {
            System.err.println("⚠ Error al cargar el archivo .env: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
