package Fullsound.Fullsound.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "usuario", indexes = {
    @Index(name = "idx_usuario_correo", columnList = "correo"),
    @Index(name = "idx_usuario_nombre_usuario", columnList = "nombre_usuario")
})
@Data
@NoArgsConstructor
@SuperBuilder
public class Usuario extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;
    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;
    
    @Column(name = "rut", length = 12)
    private String rut;
    
    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String correo;
    @Column(name = "contraseña", nullable = false, length = 255)
    private String contraseña;
    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    @Builder.Default
    private Set<Rol> roles = new HashSet<>();
    @Column(name = "nombre", length = 100)
    private String nombre;
    @Column(name = "apellido", length = 100)
    private String apellido;
    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nombreUsuario='" + nombreUsuario + "', correo='" + correo + "'}";
    }
}
