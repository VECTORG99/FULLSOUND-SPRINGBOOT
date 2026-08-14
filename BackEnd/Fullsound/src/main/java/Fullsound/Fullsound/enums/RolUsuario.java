package Fullsound.Fullsound.enums;
public enum RolUsuario {
    CLIENTE("cliente"),
    ADMINISTRADOR("administrador");
    private final String dbValue;
    RolUsuario(String dbValue) {
        this.dbValue = dbValue;
    }
    public String getDbValue() {
        return dbValue;
    }
    public static RolUsuario fromDbValue(String dbValue) {
        for (RolUsuario rol : values()) {
            if (rol.dbValue.equalsIgnoreCase(dbValue)) {
                return rol;
            }
        }
        throw new IllegalArgumentException("Rol no válido: " + dbValue);
    }
    public String getAuthority() {
        return dbValue;
    }
}
