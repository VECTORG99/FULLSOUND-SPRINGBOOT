package Fullsound.Fullsound.exception;

/**
 * Excepción lanzada cuando un recurso no es encontrado.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
