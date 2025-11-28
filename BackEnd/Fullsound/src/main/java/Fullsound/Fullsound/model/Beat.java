package Fullsound.Fullsound.model;

import Fullsound.Fullsound.enums.EstadoBeat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
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
    
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Column(name = "bpm")
    private Integer bpm;
    
    @Column(name = "tonalidad", length = 10)
    private String tonalidad;
    
    @Column(name = "mood", length = 50)
    private String mood;
    
    @Column(name = "tags", length = 255)
    private String tags;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    @Builder.Default
    private EstadoBeat estado = EstadoBeat.DISPONIBLE;
    
    @Column(name = "archivo_audio", length = 255)
    private String archivoAudio;
    
    @Column(name = "imagen_portada", length = 255)
    private String imagenPortada;
    
    @Column(name = "reproducciones")
    @Builder.Default
    private Integer reproducciones = 0;
    
    @Column(name = "descargas")
    @Builder.Default
    private Integer descargas = 0;
    
    @Column(name = "likes")
    @Builder.Default
    private Integer likes = 0;
    
    @Column(name = "destacado")
    @Builder.Default
    private Boolean destacado = false;
    
    @Column(name = "activo")
    @Builder.Default
    private Boolean activo = true;
    
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
        return precio != null ? String.format("$%,.2f", precio) : "$0.00";
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
        return "Beat{id=" + id + ", titulo='" + titulo + "', precio=" + precio + ", estado=" + estado + "}";
    }
}
