package Fullsound.Fullsound.security;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Almacén en memoria de tokens de restablecimiento de contraseña.
 *
 * Como el proyecto no cuenta con un servicio de email, los tokens se
 * generan y se registran en logs. Cada token expira tras 1 hora.
 * En producción debería reemplazarse por un almacenamiento persistente
 * (tabla de base de datos o Redis).
 */
@Component
public class PasswordResetTokenStore {

    private static final int EXPIRY_HOURS = 1;

    private final Map<String, ResetToken> tokens = new ConcurrentHashMap<>();

    public String generateToken(String correo) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, new ResetToken(correo, LocalDateTime.now().plusHours(EXPIRY_HOURS)));
        return token;
    }

    public boolean isValid(String token) {
        ResetToken resetToken = tokens.get(token);
        if (resetToken == null) {
            return false;
        }
        if (resetToken.expiry.isBefore(LocalDateTime.now())) {
            tokens.remove(token);
            return false;
        }
        return true;
    }

    public String getCorreoForToken(String token) {
        ResetToken resetToken = tokens.get(token);
        return resetToken != null ? resetToken.correo : null;
    }

    public void invalidate(String token) {
        tokens.remove(token);
    }

    private static class ResetToken {
        final String correo;
        final LocalDateTime expiry;

        ResetToken(String correo, LocalDateTime expiry) {
            this.correo = correo;
            this.expiry = expiry;
        }
    }
}
