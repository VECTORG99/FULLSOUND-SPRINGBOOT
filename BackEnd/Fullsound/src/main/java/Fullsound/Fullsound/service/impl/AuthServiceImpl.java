package Fullsound.Fullsound.service.impl;
import Fullsound.Fullsound.dto.request.LoginRequest;
import Fullsound.Fullsound.dto.request.RegisterRequest;
import Fullsound.Fullsound.dto.request.ForgotPasswordRequest;
import Fullsound.Fullsound.dto.request.ResetPasswordRequest;
import Fullsound.Fullsound.dto.response.AuthResponse;
import Fullsound.Fullsound.dto.response.MessageResponse;
import Fullsound.Fullsound.exception.BadRequestException;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.model.Rol;
import Fullsound.Fullsound.model.Usuario;
import Fullsound.Fullsound.repository.RolRepository;
import Fullsound.Fullsound.repository.UsuarioRepository;
import Fullsound.Fullsound.security.JwtTokenProvider;
import Fullsound.Fullsound.security.PasswordResetTokenStore;
import Fullsound.Fullsound.security.UserDetailsImpl;
import Fullsound.Fullsound.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final PasswordResetTokenStore passwordResetTokenStore;
    @Override
    @Transactional
    public MessageResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            throw new BadRequestException("El nombre de usuario ya está en uso");
        }
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new BadRequestException("El correo ya está registrado");
        }
        String tipoRol = (request.getRol() != null && !request.getRol().isEmpty()) 
                ? request.getRol() 
                : "cliente";
        Rol rol = rolRepository.findByTipo(tipoRol)
                .orElseThrow(() -> new BadRequestException("Rol '" + tipoRol + "' no encontrado"));
        Set<Rol> roles = new HashSet<>();
        roles.add(rol);
        Usuario usuario = Usuario.builder()
                .nombreUsuario(request.getNombreUsuario())
                .rut(request.getRut())
                .correo(request.getCorreo())
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .contraseña(passwordEncoder.encode(request.getContraseña()))
                .activo(true)
                .roles(roles)
                .build();
        usuarioRepository.save(usuario);
        return MessageResponse.builder()
                .message("Usuario registrado exitosamente")
                .success(true)
                .build();
    }
    @Override
    public AuthResponse login(LoginRequest request) {
        String identifier = request.getNombreUsuario();
        String username = identifier;
        if (identifier.contains("@")) {
            Usuario usuario = usuarioRepository.findByCorreo(identifier)
                    .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));
            username = usuario.getNombreUsuario();
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        request.getContraseña()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
        List<String> roles = usuario.getRoles() != null && !usuario.getRoles().isEmpty()
                ? usuario.getRoles().stream().map(r -> r.getTipo()).collect(Collectors.toList())
                : Collections.emptyList();
        return new AuthResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getCorreo(),
                roles
        );
    }
    @Override
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        // Por seguridad, siempre devolvemos éxito incluso si el correo no existe
        usuarioRepository.findByCorreo(request.getCorreo()).ifPresent(usuario -> {
            String token = passwordResetTokenStore.generateToken(usuario.getCorreo());
            // Como no hay servicio de email, registramos el token en el log
            log.info("Token de restablecimiento generado para {}: {}", usuario.getCorreo(), token);
        });
        return MessageResponse.builder()
                .message("Si el correo existe, se ha enviado un enlace de restablecimiento")
                .success(true)
                .build();
    }
    @Override
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        if (!passwordResetTokenStore.isValid(request.getToken())) {
            throw new BadRequestException("Token inválido o expirado");
        }
        String correo = passwordResetTokenStore.getCorreoForToken(request.getToken());
        if (correo == null) {
            throw new BadRequestException("Token inválido o expirado");
        }
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "correo", correo));
        usuario.setContraseña(passwordEncoder.encode(request.getNuevaContraseña()));
        usuarioRepository.save(usuario);
        passwordResetTokenStore.invalidate(request.getToken());
        return MessageResponse.builder()
                .message("Contraseña restablecida exitosamente")
                .success(true)
                .build();
    }
    @Override
    public boolean isUsernameAvailable(String username) {
        return !usuarioRepository.existsByNombreUsuario(username);
    }
    @Override
    public boolean isEmailAvailable(String email) {
        return !usuarioRepository.existsByCorreo(email);
    }
}
