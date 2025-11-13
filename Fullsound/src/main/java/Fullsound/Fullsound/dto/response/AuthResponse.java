package Fullsound.Fullsound.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta para autenticación.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String tipo;
    private Integer usuarioId;
    private String nombreUsuario;
    private String correo;
    private List<String> roles;
    
    public AuthResponse(String token, Integer usuarioId, String nombreUsuario, String correo, List<String> roles) {
        this.token = token;
        this.tipo = "Bearer";
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.roles = roles;
    }
}
