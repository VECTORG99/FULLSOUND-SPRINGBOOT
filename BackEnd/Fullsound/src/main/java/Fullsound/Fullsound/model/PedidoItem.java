package Fullsound.Fullsound.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entidad PedidoItem - Mapea a la tabla 'compra_detalle' en la base de datos.
 * Representa un item (beat) dentro de un pedido.
 * 
 * IMPORTANTE: La tabla en BD se llama 'compra_detalle', no 'pedidos_items'
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Entity
@Table(name = "compra_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra", nullable = false)
    private Pedido pedido;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_beat", nullable = false)
    private Beat beat;
    
    @Column(name = "nombre_item", nullable = false, length = 255)
    private String nombreItem; // Snapshot del nombre del beat al momento de la compra
    
    @Column(name = "cantidad", nullable = false)
    @Builder.Default
    private Integer cantidad = 1;
    
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario; // Snapshot del precio al momento de la compra
    
    /**
     * Calcula el subtotal del item.
     */
    @Transient
    public BigDecimal getSubtotal() {
        if (precioUnitario == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
    
    @Override
    public String toString() {
        return "PedidoItem{id=" + id + ", nombreItem='" + nombreItem + "', cantidad=" + cantidad + ", precioUnitario=" + precioUnitario + "}";
    }
}
