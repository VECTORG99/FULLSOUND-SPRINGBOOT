package Fullsound.Fullsound.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad Beat - Mapea a la tabla 'beat' en la base de datos.
 * Representa un beat musical disponible para compra.
 * 
 * IMPORTANTE: Campos calculados (precioFormateado, enlaceProducto) son @Transient
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
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
    private Integer duracion; // Duración en segundos
    
    @Column(name = "genero", length = 50)
    private String genero;
    
    @Column(name = "etiquetas", columnDefinition = "TEXT")
    private String etiquetas; // Tags separados por comas
    
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
    private String estado = "DISPONIBLE"; // DISPONIBLE, VENDIDO, RESERVADO, INACTIVO
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Campo calculado: precio formateado.
     * NO existe como columna en la base de datos.
     */
    @Transient
    public String getPrecioFormateado() {
        return precio != null ? String.format("$%,d", precio) : "$0";
    }
    
    /**
     * Campo calculado: enlace al producto.
     * NO existe como columna en la base de datos.
     */
    @Transient
    public String getEnlaceProducto() {
        return slug != null ? "/beats/" + slug : "/beats/" + id;
    }
    
    @Override
    public String toString() {
        return "Beat{id=" + id + ", titulo='" + titulo + "', precio=" + precio + ", genero='" + genero + "', estado=" + estado + "}";
    }
}
