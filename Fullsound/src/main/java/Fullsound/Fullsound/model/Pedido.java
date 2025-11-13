package Fullsound.Fullsound.model;

import Fullsound.Fullsound.enums.EstadoPedido;
import Fullsound.Fullsound.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Pedido - Mapea a la tabla 'compra' en la base de datos.
 * Representa una compra realizada por un usuario.
 * 
 * IMPORTANTE: La tabla en BD se llama 'compra', no 'pedidos'
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
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
    
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.PENDIENTE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", length = 20)
    private MetodoPago metodoPago;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PedidoItem> items = new ArrayList<>();
    
    /**
     * Método para generar el número de pedido automáticamente.
     * Formato: FS-YYYYMMDD-XXXXXX (FS = FullSound)
     */
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
    
    /**
     * Método helper para agregar items al pedido.
     */
    public void addItem(PedidoItem item) {
        items.add(item);
        item.setPedido(this);
    }
    
    /**
     * Método helper para remover items del pedido.
     */
    public void removeItem(PedidoItem item) {
        items.remove(item);
        item.setPedido(null);
    }
    
    @Override
    public String toString() {
        return "Pedido{id=" + id + ", numeroPedido='" + numeroPedido + "', total=" + total + ", estado=" + estado + "}";
    }
}
