package Fullsound.Fullsound.enums;

/**
 * Enumeración de roles de usuario en el sistema.
 * 
 * IMPORTANTE: Los valores en BD son strings: "cliente" y "administrador"
 * NO usar prefijo ROLE_ en Spring Security.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
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
    
    /**
     * Convierte el valor de la base de datos al enum correspondiente.
     * 
     * @param dbValue valor almacenado en BD ("cliente" o "administrador")
     * @return RolUsuario correspondiente
     * @throws IllegalArgumentException si el valor no es válido
     */
    public static RolUsuario fromDbValue(String dbValue) {
        for (RolUsuario rol : values()) {
            if (rol.dbValue.equalsIgnoreCase(dbValue)) {
                return rol;
            }
        }
        throw new IllegalArgumentException("Rol no válido: " + dbValue);
    }
    
    /**
     * Retorna el nombre del rol para Spring Security (sin prefijo ROLE_).
     * 
     * @return nombre del rol en minúsculas
     */
    public String getAuthority() {
        return dbValue;
    }
}
