package Fullsound.Fullsound.model;
import jakarta.persistence.*;
import lombok.*;
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
    private String nombreItem;  
    @Column(name = "cantidad", nullable = false)
    @Builder.Default
    private Integer cantidad = 1;
    @Column(name = "precio_unitario", nullable = false)
    private Integer precioUnitario;  
    @Transient
    public Integer getSubtotal() {
        if (precioUnitario == null || cantidad == null) {
            return 0;
        }
        return precioUnitario * cantidad;
    }
    @Override
    public String toString() {
        return "PedidoItem{id=" + id + ", nombreItem='" + nombreItem + "', cantidad=" + cantidad + ", precioUnitario=" + precioUnitario + "}";
    }
}
