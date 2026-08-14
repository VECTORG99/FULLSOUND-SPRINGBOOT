package Fullsound.Fullsound.config;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Logger log = LoggerFactory.getLogger(DotenvConfig.class);
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        try {
            String[] locations = {
                "../../",
                "../../../",
                "./"
            };
            Dotenv dotenv = null;
            for (String location : locations) {
                File envFile = new File(location + ".env");
                if (envFile.exists()) {
                    dotenv = Dotenv.configure()
                            .directory(location)
                            .ignoreIfMissing()
                            .load();
                    log.info("Archivo .env encontrado en: {}", envFile.getAbsolutePath());
                    break;
                }
            }
            if (dotenv == null) {
                log.warn("No se encontró el archivo .env");
                return;
            }
            Map<String, Object> dotenvMap = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                dotenvMap.put(entry.getKey(), entry.getValue());
                log.debug("Variable de entorno cargada: {}", entry.getKey());
            });
            environment.getPropertySources()
                    .addFirst(new MapPropertySource("dotenvProperties", dotenvMap));
            log.info("Variables de entorno cargadas desde .env");
        } catch (Exception e) {
            log.error("Error al cargar el archivo .env: {}", e.getMessage(), e);
        }
    }
}
