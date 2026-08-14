package Fullsound.Fullsound.enums;
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
