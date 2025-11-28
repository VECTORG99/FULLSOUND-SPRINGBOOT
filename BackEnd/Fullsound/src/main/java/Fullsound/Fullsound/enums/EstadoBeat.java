package Fullsound.Fullsound.enums;

/**
 * Enumeración de estados posibles de un beat.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
public enum EstadoBeat {
    DISPONIBLE("Disponible para compra"),
    VENDIDO("Vendido (licencia exclusiva)"),
    RESERVADO("Reservado temporalmente"),
    INACTIVO("Inactivo/Oculto");
    
    private final String descripcion;
    
    EstadoBeat(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
