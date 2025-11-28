package Fullsound.Fullsound.exception;

/**
 * Excepción lanzada cuando hay un error en la petición.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
}
