package Fullsound.Fullsound.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
@Entity
@Table(name = "tipo_usuario")
@Data
@NoArgsConstructor
@SuperBuilder
public class Rol extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_usuario")
    private Integer id;
    @Column(name = "tipo", nullable = false, unique = true, length = 50)
    private String tipo;  
    @Column(name = "descripcion", length = 255)
    private String descripcion;
    @Override
    public String toString() {
        return "Rol{id=" + id + ", tipo='" + tipo + "'}";
    }
}
