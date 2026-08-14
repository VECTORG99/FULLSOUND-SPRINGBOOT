package Fullsound.Fullsound.service;
import Fullsound.Fullsound.dto.request.LoginRequest;
import Fullsound.Fullsound.dto.request.RegisterRequest;
import Fullsound.Fullsound.dto.request.ForgotPasswordRequest;
import Fullsound.Fullsound.dto.request.ResetPasswordRequest;
import Fullsound.Fullsound.dto.response.AuthResponse;
import Fullsound.Fullsound.dto.response.MessageResponse;
public interface AuthService {
    MessageResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    MessageResponse forgotPassword(ForgotPasswordRequest request);
    MessageResponse resetPassword(ResetPasswordRequest request);
    boolean isUsernameAvailable(String username);
    boolean isEmailAvailable(String email);
}
