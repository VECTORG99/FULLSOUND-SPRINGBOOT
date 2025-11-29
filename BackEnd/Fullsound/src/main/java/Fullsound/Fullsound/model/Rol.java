package Fullsound.Fullsound.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad Rol - Mapea a la tabla 'tipo_usuario' en la base de datos.
 * Representa los tipos de usuario en el sistema (cliente, administrador).
 * 
 * IMPORTANTE: La tabla en BD se llama 'tipo_usuario', no 'roles'
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Entity
@Table(name = "tipo_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_usuario")
    private Integer id;
    
    @Column(name = "tipo", nullable = false, unique = true, length = 50)
    private String tipo; // "cliente", "productor" o "administrador"
    
    @Column(name = "descripcion", length = 255)
    private String descripcion;
    
    @Override
    public String toString() {
        return "Rol{id=" + id + ", tipo='" + tipo + "'}";
    }
}
