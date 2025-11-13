package Fullsound.Fullsound.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para petición de actualización de usuario.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUsuarioRequest {
    
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String nombreUsuario;
    
    @Email(message = "El correo debe ser válido")
    private String correo;
    
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contraseña;
    
    private Boolean activo;
}
