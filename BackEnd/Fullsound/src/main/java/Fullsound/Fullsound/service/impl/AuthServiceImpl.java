package Fullsound.Fullsound.service.impl;

import Fullsound.Fullsound.dto.request.LoginRequest;
import Fullsound.Fullsound.dto.request.RegisterRequest;
import Fullsound.Fullsound.dto.response.AuthResponse;
import Fullsound.Fullsound.dto.response.MessageResponse;
import Fullsound.Fullsound.exception.BadRequestException;
import Fullsound.Fullsound.model.Rol;
import Fullsound.Fullsound.model.Usuario;
import Fullsound.Fullsound.repository.RolRepository;
import Fullsound.Fullsound.repository.UsuarioRepository;
import Fullsound.Fullsound.security.JwtTokenProvider;
import Fullsound.Fullsound.security.UserDetailsImpl;
import Fullsound.Fullsound.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Implementación del servicio de autenticación.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    
    @Override
    @Transactional
    public MessageResponse register(RegisterRequest request) {
        // Validar que el usuario no exista
        if (usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            throw new BadRequestException("El nombre de usuario ya está en uso");
        }
        
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new BadRequestException("El correo ya está registrado");
        }
        
        // Determinar el rol a asignar (si se especifica, sino cliente por defecto)
        String tipoRol = (request.getRol() != null && !request.getRol().isEmpty()) 
                ? request.getRol() 
                : "cliente";
        
        Rol rol = rolRepository.findByTipo(tipoRol)
                .orElseThrow(() -> new BadRequestException("Rol '" + tipoRol + "' no encontrado"));
        
        // Crear nuevo usuario
        Usuario usuario = Usuario.builder()
                .nombreUsuario(request.getNombreUsuario())
                .correo(request.getCorreo())
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .contraseña(passwordEncoder.encode(request.getContraseña()))
                .activo(true)
                .rol(rol)
                .build();
        
        usuarioRepository.save(usuario);
        
        return MessageResponse.builder()
                .message("Usuario registrado exitosamente")
                .success(true)
                .build();
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        // Determinar si es correo o nombre de usuario
        String identifier = request.getNombreUsuario();
        String username = identifier;
        
        // Si parece un correo, buscar el usuario por correo para obtener el nombreUsuario
        if (identifier.contains("@")) {
            Usuario usuario = usuarioRepository.findByCorreo(identifier)
                    .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));
            username = usuario.getNombreUsuario();
        }
        
        // Autenticar usuario con el nombre de usuario real
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        request.getContraseña()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generar token JWT
        String jwt = tokenProvider.generateToken(authentication);
        
        // Obtener detalles del usuario
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Obtener el rol del usuario desde la base de datos
        Usuario usuario = usuarioRepository.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
        
        List<String> roles = usuario.getRol() != null 
                ? Collections.singletonList(usuario.getRol().getTipo())
                : Collections.emptyList();
        
        return new AuthResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getCorreo(),
                roles
        );
    }
}
