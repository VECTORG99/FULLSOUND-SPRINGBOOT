package Fullsound.Fullsound.service;
import Fullsound.Fullsound.dto.request.LoginRequest;
import Fullsound.Fullsound.dto.request.RegisterRequest;
import Fullsound.Fullsound.dto.response.AuthResponse;
import Fullsound.Fullsound.dto.response.MessageResponse;
public interface AuthService {
    MessageResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
