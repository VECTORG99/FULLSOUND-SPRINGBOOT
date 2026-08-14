package Fullsound.Fullsound.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa una notificacion in-app dirigida a un usuario.
 * Tabla: notificacion.
 */
@Entity
@Table(name = "notificacion",
    indexes = {
        @Index(name = "idx_notificacion_usuario", columnList = "id_usuario"),
        @Index(name = "idx_notificacion_leido", columnList = "id_usuario,leido")
    }
)
@Data
@NoArgsConstructor
@SuperBuilder
public class Notificacion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "mensaje", nullable = false, length = 500)
    private String mensaje;

    @Column(name = "leido", nullable = false)
    @Builder.Default
    private Boolean leido = false;

    @Override
    public String toString() {
        return "Notificacion{id=" + id + ", tipo='" + tipo + "', leido=" + leido + "}";
    }
}
