package Fullsound.Fullsound.enums;
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
