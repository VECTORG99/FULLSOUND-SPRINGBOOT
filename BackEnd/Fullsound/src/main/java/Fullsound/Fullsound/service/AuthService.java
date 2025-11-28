package Fullsound.Fullsound.service;

import Fullsound.Fullsound.dto.request.LoginRequest;
import Fullsound.Fullsound.dto.request.RegisterRequest;
import Fullsound.Fullsound.dto.response.AuthResponse;
import Fullsound.Fullsound.dto.response.MessageResponse;

/**
 * Servicio de autenticación.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
public interface AuthService {
    
    /**
     * Registra un nuevo usuario.
     */
    MessageResponse register(RegisterRequest request);
    
    /**
     * Autentica un usuario y genera un token JWT.
     */
    AuthResponse login(LoginRequest request);
}
