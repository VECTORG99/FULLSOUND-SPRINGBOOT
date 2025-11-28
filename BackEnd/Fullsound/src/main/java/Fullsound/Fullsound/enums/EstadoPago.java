package Fullsound.Fullsound.enums;

/**
 * Enumeración de estados posibles de un pago.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
public enum EstadoPago {
    PENDIENTE("Pendiente de procesamiento"),
    PROCESANDO("Procesando con pasarela de pago"),
    EXITOSO("Pago completado exitosamente"),
    FALLIDO("Pago fallido"),
    REEMBOLSADO("Pago reembolsado");
    
    private final String descripcion;
    
    EstadoPago(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
