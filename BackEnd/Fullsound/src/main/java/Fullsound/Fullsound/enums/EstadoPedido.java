package Fullsound.Fullsound.enums;
public enum EstadoPedido {
    PENDIENTE("Pendiente de pago"),
    PROCESANDO("Procesando pago"),
    COMPLETADO("Completado exitosamente"),
    CANCELADO("Cancelado"),
    REEMBOLSADO("Reembolsado");
    private final String descripcion;
    EstadoPedido(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getDescripcion() {
        return descripcion;
    }
}
