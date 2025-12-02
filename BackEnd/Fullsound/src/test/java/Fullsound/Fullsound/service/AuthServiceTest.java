package Fullsound.Fullsound.service;
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
import Fullsound.Fullsound.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider tokenProvider;
    @InjectMocks
    private AuthServiceImpl authService;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private Usuario usuario;
    private Rol rol;
    @BeforeEach
    void setUp() {
        rol = Rol.builder()
                .id(1)
                .tipo("cliente")
                .descripcion("Usuario cliente")
                .build();
        usuario = Usuario.builder()
                .id(1)
                .nombreUsuario("testuser")
                .correo("test@example.com")
                .contraseña("encodedPassword")
                .nombre("Test")
                .apellido("User")
                .activo(true)
                .rol(rol)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        registerRequest = RegisterRequest.builder()
                .nombreUsuario("testuser")
                .correo("test@example.com")
                .contraseña("password123")
                .nombre("Test")
                .apellido("User")
                .rol("cliente")
                .build();
        loginRequest = LoginRequest.builder()
                .nombreUsuario("testuser")
                .contraseña("password123")
                .build();
    }
    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {
        @Test
        @DisplayName("Should register user successfully")
        void shouldRegisterUserSuccessfully() {
            when(usuarioRepository.existsByNombreUsuario(anyString())).thenReturn(false);
            when(usuarioRepository.existsByCorreo(anyString())).thenReturn(false);
            when(rolRepository.findByTipo("cliente")).thenReturn(Optional.of(rol));
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
            MessageResponse response = authService.register(registerRequest);
            assertNotNull(response);
            assertTrue(response.getSuccess());
            assertEquals("Usuario registrado exitosamente", response.getMessage());
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }
        @Test
        @DisplayName("Should throw exception when username exists")
        void shouldThrowExceptionWhenUsernameExists() {
            when(usuarioRepository.existsByNombreUsuario("testuser")).thenReturn(true);
            assertThrows(BadRequestException.class, () -> authService.register(registerRequest));
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }
        @Test
        @DisplayName("Should throw exception when email exists")
        void shouldThrowExceptionWhenEmailExists() {
            when(usuarioRepository.existsByNombreUsuario(anyString())).thenReturn(false);
            when(usuarioRepository.existsByCorreo("test@example.com")).thenReturn(true);
            assertThrows(BadRequestException.class, () -> authService.register(registerRequest));
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }
        @Test
        @DisplayName("Should register with default role when no role specified")
        void shouldRegisterWithDefaultRole() {
            RegisterRequest requestWithoutRole = RegisterRequest.builder()
                    .nombreUsuario("testuser")
                    .correo("test@example.com")
                    .contraseña("password123")
                    .nombre("Test")
                    .apellido("User")
                    .build();
            when(usuarioRepository.existsByNombreUsuario(anyString())).thenReturn(false);
            when(usuarioRepository.existsByCorreo(anyString())).thenReturn(false);
            when(rolRepository.findByTipo("cliente")).thenReturn(Optional.of(rol));
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
            MessageResponse response = authService.register(requestWithoutRole);
            assertNotNull(response);
            assertTrue(response.getSuccess());
            verify(rolRepository, times(1)).findByTipo("cliente");
        }
        @Test
        @DisplayName("Should throw exception when role not found")
        void shouldThrowExceptionWhenRoleNotFound() {
            when(usuarioRepository.existsByNombreUsuario(anyString())).thenReturn(false);
            when(usuarioRepository.existsByCorreo(anyString())).thenReturn(false);
            when(rolRepository.findByTipo("cliente")).thenReturn(Optional.empty());
            assertThrows(BadRequestException.class, () -> authService.register(registerRequest));
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }
    }
    @Nested
    @DisplayName("Login Tests")
    class LoginTests {
        @Test
        @DisplayName("Should login successfully with username")
        void shouldLoginSuccessfullyWithUsername() {
            Authentication authentication = mock(Authentication.class);
            UserDetailsImpl userDetails = new UserDetailsImpl(
                    1, "testuser", "test@example.com", "encodedPassword", true, null
            );
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(tokenProvider.generateToken(authentication)).thenReturn("jwt-token");
            when(usuarioRepository.findByNombreUsuario("testuser")).thenReturn(Optional.of(usuario));
            AuthResponse response = authService.login(loginRequest);
            assertNotNull(response);
            assertEquals("jwt-token", response.getToken());
            assertEquals(1, response.getUsuarioId());
            assertEquals("testuser", response.getNombreUsuario());
            assertEquals("test@example.com", response.getCorreo());
            assertTrue(response.getRoles().contains("cliente"));
        }
        @Test
        @DisplayName("Should login successfully with email")
        void shouldLoginSuccessfullyWithEmail() {
            LoginRequest emailLoginRequest = LoginRequest.builder()
                    .nombreUsuario("test@example.com")
                    .contraseña("password123")
                    .build();
            Authentication authentication = mock(Authentication.class);
            UserDetailsImpl userDetails = new UserDetailsImpl(
                    1, "testuser", "test@example.com", "encodedPassword", true, null
            );
            when(usuarioRepository.findByCorreo("test@example.com")).thenReturn(Optional.of(usuario));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(tokenProvider.generateToken(authentication)).thenReturn("jwt-token");
            when(usuarioRepository.findByNombreUsuario("testuser")).thenReturn(Optional.of(usuario));
            AuthResponse response = authService.login(emailLoginRequest);
            assertNotNull(response);
            assertEquals("jwt-token", response.getToken());
            verify(usuarioRepository, times(1)).findByCorreo("test@example.com");
        }
        @Test
        @DisplayName("Should throw exception when email not found")
        void shouldThrowExceptionWhenEmailNotFound() {
            LoginRequest emailLoginRequest = LoginRequest.builder()
                    .nombreUsuario("notfound@example.com")
                    .contraseña("password123")
                    .build();
            when(usuarioRepository.findByCorreo("notfound@example.com")).thenReturn(Optional.empty());
            assertThrows(BadRequestException.class, () -> authService.login(emailLoginRequest));
        }
        @Test
        @DisplayName("Should throw exception when user not found after authentication")
        void shouldThrowExceptionWhenUserNotFoundAfterAuth() {
            Authentication authentication = mock(Authentication.class);
            UserDetailsImpl userDetails = new UserDetailsImpl(
                    1, "testuser", "test@example.com", "encodedPassword", true, null
            );
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(tokenProvider.generateToken(authentication)).thenReturn("jwt-token");
            when(usuarioRepository.findByNombreUsuario("testuser")).thenReturn(Optional.empty());
            assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
        }
    }
}
