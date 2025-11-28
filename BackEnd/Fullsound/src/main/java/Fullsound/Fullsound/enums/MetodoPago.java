package Fullsound.Fullsound.enums;

/**
 * Enumeración de métodos de pago disponibles.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
public enum MetodoPago {
    STRIPE("Tarjeta de crédito/débito (Stripe)"),
    PAYPAL("PayPal"),
    TRANSFERENCIA("Transferencia bancaria");
    
    private final String descripcion;
    
    MetodoPago(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
