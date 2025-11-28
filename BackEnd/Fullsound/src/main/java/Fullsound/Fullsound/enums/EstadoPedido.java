package Fullsound.Fullsound.enums;

/**
 * Enumeración de estados posibles de un pedido.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
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
