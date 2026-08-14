package Fullsound.Fullsound.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa un beat marcado como favorito por un usuario.
 * Tabla join: usuario_favorito.
 */
@Entity
@Table(name = "usuario_favorito",
    uniqueConstraints = @UniqueConstraint(name = "uk_usuario_favorito", columnNames = {"id_usuario", "id_beat"}),
    indexes = {
        @Index(name = "idx_usuario_favorito_usuario", columnList = "id_usuario"),
        @Index(name = "idx_usuario_favorito_beat", columnList = "id_beat")
    }
)
@Data
@NoArgsConstructor
@SuperBuilder
public class UsuarioFavorito extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_favorito")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_beat", nullable = false)
    private Beat beat;

    @Override
    public String toString() {
        return "UsuarioFavorito{id=" + id + ", usuario=" + (usuario != null ? usuario.getId() : null) +
               ", beat=" + (beat != null ? beat.getId() : null) + "}";
    }
}
