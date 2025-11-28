package Fullsound.Fullsound.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para petición de login.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String nombreUsuario;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String contraseña;
}
