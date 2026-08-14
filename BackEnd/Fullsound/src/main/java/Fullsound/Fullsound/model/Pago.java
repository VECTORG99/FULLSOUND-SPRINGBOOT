package Fullsound.Fullsound.model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
@Entity
@Table(name = "pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra", nullable = false)
    private Pedido pedido;
    @Column(name = "stripe_payment_intent_id", unique = true, length = 255)
    private String stripePaymentIntentId;
    @Column(name = "stripe_charge_id", length = 255)
    private String stripeChargeId;
    @Column(name = "estado", length = 20, nullable = false)
    @Builder.Default
    private String estado = "PENDIENTE";  
    @Column(name = "monto", nullable = false)
    private Integer monto;
    @Column(name = "moneda", length = 3)
    @Builder.Default
    private String moneda = "CLP";
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    @Override
    public String toString() {
        return "Pago{id=" + id + ", stripePaymentIntentId='" + stripePaymentIntentId + "', monto=" + monto + ", estado=" + estado + "}";
    }
}
