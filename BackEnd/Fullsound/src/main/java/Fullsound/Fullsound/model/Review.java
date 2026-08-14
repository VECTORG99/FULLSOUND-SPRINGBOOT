package Fullsound.Fullsound.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa una resena/valoracion de un beat por un usuario.
 * Tabla: review. Un usuario solo puede dejar una resena por beat.
 */
@Entity
@Table(name = "review",
    uniqueConstraints = @UniqueConstraint(name = "uk_review_beat_usuario", columnNames = {"id_beat", "id_usuario"}),
    indexes = {
        @Index(name = "idx_review_beat", columnList = "id_beat"),
        @Index(name = "idx_review_usuario", columnList = "id_usuario")
    }
)
@Data
@NoArgsConstructor
@SuperBuilder
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_review")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_beat", nullable = false)
    private Beat beat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Override
    public String toString() {
        return "Review{id=" + id + ", rating=" + rating + ", beat=" + (beat != null ? beat.getId() : null) + "}";
    }
}
