package Fullsound.Fullsound.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    @Column(name = "numero_pedido", unique = true, length = 50)
    private String numeroPedido;
    @CreationTimestamp
    @Column(name = "fecha_compra", nullable = false, updatable = false)
    private LocalDateTime fechaCompra;
    @Column(name = "total", nullable = false)
    private Integer total;
    @Column(name = "estado", length = 20, nullable = false)
    @Builder.Default
    private String estado = "PENDIENTE";  
    @Column(name = "metodo_pago", length = 20)
    private String metodoPago;  
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PedidoItem> items = new ArrayList<>();
    @PrePersist
    public void generarNumeroPedido() {
        if (this.numeroPedido == null) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String numeroUnico = timestamp.substring(timestamp.length() - 6);
            this.numeroPedido = "FS-" + 
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                "-" + numeroUnico;
        }
    }
    public void addItem(PedidoItem item) {
        items.add(item);
        item.setPedido(this);
    }
    public void removeItem(PedidoItem item) {
        items.remove(item);
        item.setPedido(null);
    }
    @Override
    public String toString() {
        return "Pedido{id=" + id + ", numeroPedido='" + numeroPedido + "', total=" + total + ", estado=" + estado + "}";
    }
}
