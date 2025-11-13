package Fullsound.Fullsound.exception;

/**
 * Excepción lanzada cuando hay un error de autenticación.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
}
