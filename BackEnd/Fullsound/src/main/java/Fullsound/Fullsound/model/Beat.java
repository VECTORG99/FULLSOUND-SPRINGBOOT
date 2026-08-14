package Fullsound.Fullsound.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
@Entity
@Table(name = "beat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_beat")
    private Integer id;
    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;
    @Column(name = "slug", unique = true, length = 255)
    private String slug;
    @Column(name = "artista", length = 100)
    private String artista;
    @Column(name = "precio", nullable = false)
    private Integer precio;
    @Column(name = "bpm")
    private Integer bpm;
    @Column(name = "tonalidad", length = 10)
    private String tonalidad;
    @Column(name = "duracion")
    private Integer duracion;  
    @Column(name = "genero", length = 50)
    private String genero;
    
    @Column(name = "emocion", length = 50)
    private String emocion;
    
    @Column(name = "etiquetas", columnDefinition = "TEXT")
    private String etiquetas;  
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;
    @Column(name = "audio_url", length = 500)
    private String audioUrl;
    @Column(name = "audio_demo_url", length = 500)
    private String audioDemoUrl;
    @Column(name = "reproducciones", nullable = false)
    @Builder.Default
    private Integer reproducciones = 0;
    @Column(name = "estado", length = 20, nullable = false)
    @Builder.Default
    private String estado = "DISPONIBLE";  
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Transient
    public String getPrecioFormateado() {
        return precio != null ? String.format("$%,d", precio) : "$0";
    }
    @Transient
    public String getEnlaceProducto() {
        return slug != null ? "/beats/" + slug : "/beats/" + id;
    }
    @Override
    public String toString() {
        return "Beat{id=" + id + ", titulo='" + titulo + "', precio=" + precio + ", genero='" + genero + "', emocion='" + emocion + "', estado=" + estado + "}";
    }
}
