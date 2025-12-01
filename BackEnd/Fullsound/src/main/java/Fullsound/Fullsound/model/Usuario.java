package Fullsound.Fullsound.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad Usuario - Mapea a la tabla 'usuario' en la base de datos.
 * Representa un usuario del sistema (cliente o administrador).
 * 
 * IMPORTANTE: Relación Many-to-One con Rol via columna 'id_rol'
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;
    
    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;
    
    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String correo;
    
    @Column(name = "contraseña", nullable = false, length = 255)
    private String contraseña;
    
    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false, updatable = true)
    private LocalDateTime updatedAt;
    
    // Relación Many-to-One con Rol
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;
    
    @Column(name = "nombre", length = 100)
    private String nombre;
    
    @Column(name = "apellido", length = 100)
    private String apellido;
    
    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nombreUsuario='" + nombreUsuario + "', correo='" + correo + "'}";
    }
}
