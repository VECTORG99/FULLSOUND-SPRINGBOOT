package Fullsound.Fullsound.service;

import Fullsound.Fullsound.dto.request.UpdateUsuarioRequest;
import Fullsound.Fullsound.dto.response.UsuarioResponse;
import Fullsound.Fullsound.exception.BadRequestException;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.mapper.UsuarioMapper;
import Fullsound.Fullsound.model.Rol;
import Fullsound.Fullsound.model.Usuario;
import Fullsound.Fullsound.repository.UsuarioRepository;
import Fullsound.Fullsound.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UsuarioServiceImpl.
 * Tests CRUD operations for users.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private Rol rol;
    private UsuarioResponse usuarioResponse;
    private UpdateUsuarioRequest updateRequest;

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
                .activo(true)
                .rol(rol)
                .nombre("Test")
                .apellido("User")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        usuarioResponse = UsuarioResponse.builder()
                .id(1)
                .nombreUsuario("testuser")
                .correo("test@example.com")
                .activo(true)
                .roles(List.of("cliente"))
                .createdAt(LocalDateTime.now())
                .build();

        updateRequest = UpdateUsuarioRequest.builder()
                .correo("newemail@example.com")
                .build();
    }

    @Nested
    @DisplayName("Read Usuario Tests")
    class ReadUsuarioTests {

        @Test
        @DisplayName("Should get usuario by ID successfully")
        void shouldGetUsuarioByIdSuccessfully() {
            when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
            when(usuarioMapper.toResponse(usuario)).thenReturn(usuarioResponse);

            UsuarioResponse result = usuarioService.getById(1);

            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals("testuser", result.getNombreUsuario());
            assertEquals("test@example.com", result.getCorreo());
        }

        @Test
        @DisplayName("Should throw exception when usuario not found by ID")
        void shouldThrowExceptionWhenUsuarioNotFoundById() {
            when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> usuarioService.getById(999));
        }

        @Test
        @DisplayName("Should get usuario by nombreUsuario successfully")
        void shouldGetUsuarioByNombreUsuarioSuccessfully() {
            when(usuarioRepository.findByNombreUsuario("testuser")).thenReturn(Optional.of(usuario));
            when(usuarioMapper.toResponse(usuario)).thenReturn(usuarioResponse);

            UsuarioResponse result = usuarioService.getByNombreUsuario("testuser");

            assertNotNull(result);
            assertEquals("testuser", result.getNombreUsuario());
        }

        @Test
        @DisplayName("Should throw exception when usuario not found by nombreUsuario")
        void shouldThrowExceptionWhenUsuarioNotFoundByNombreUsuario() {
            when(usuarioRepository.findByNombreUsuario("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> usuarioService.getByNombreUsuario("nonexistent"));
        }

        @Test
        @DisplayName("Should get all usuarios")
        void shouldGetAllUsuarios() {
            Usuario usuario2 = Usuario.builder()
                    .id(2)
                    .nombreUsuario("user2")
                    .correo("user2@example.com")
                    .activo(true)
                    .rol(rol)
                    .build();

            UsuarioResponse response2 = UsuarioResponse.builder()
                    .id(2)
                    .nombreUsuario("user2")
                    .correo("user2@example.com")
                    .activo(true)
                    .roles(List.of("cliente"))
                    .build();

            when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario, usuario2));
            when(usuarioMapper.toResponse(usuario)).thenReturn(usuarioResponse);
            when(usuarioMapper.toResponse(usuario2)).thenReturn(response2);

            List<UsuarioResponse> result = usuarioService.getAll();

            assertNotNull(result);
            assertEquals(2, result.size());
        }
    }

    @Nested
    @DisplayName("Update Usuario Tests")
    class UpdateUsuarioTests {

        @Test
        @DisplayName("Should update usuario profile with new email")
        void shouldUpdateUsuarioProfileWithNewEmail() {
            when(usuarioRepository.findByNombreUsuario("testuser")).thenReturn(Optional.of(usuario));
            when(usuarioRepository.existsByCorreo("newemail@example.com")).thenReturn(false);
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
            
            UsuarioResponse updatedResponse = UsuarioResponse.builder()
                    .id(1)
                    .nombreUsuario("testuser")
                    .correo("newemail@example.com")
                    .activo(true)
                    .roles(List.of("cliente"))
                    .build();
            when(usuarioMapper.toResponse(any(Usuario.class))).thenReturn(updatedResponse);

            UsuarioResponse result = usuarioService.updateProfile("testuser", updateRequest);

            assertNotNull(result);
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            when(usuarioRepository.findByNombreUsuario("testuser")).thenReturn(Optional.of(usuario));
            when(usuarioRepository.existsByCorreo("newemail@example.com")).thenReturn(true);

            assertThrows(BadRequestException.class, () -> 
                usuarioService.updateProfile("testuser", updateRequest));
        }

        @Test
        @DisplayName("Should update usuario password")
        void shouldUpdateUsuarioPassword() {
            UpdateUsuarioRequest passwordUpdate = UpdateUsuarioRequest.builder()
                    .contraseña("newPassword123")
                    .build();

            when(usuarioRepository.findByNombreUsuario("testuser")).thenReturn(Optional.of(usuario));
            when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
            when(usuarioMapper.toResponse(any(Usuario.class))).thenReturn(usuarioResponse);

            UsuarioResponse result = usuarioService.updateProfile("testuser", passwordUpdate);

            assertNotNull(result);
            verify(passwordEncoder, times(1)).encode("newPassword123");
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent usuario")
        void shouldThrowExceptionWhenUpdatingNonExistentUsuario() {
            when(usuarioRepository.findByNombreUsuario("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> 
                usuarioService.updateProfile("nonexistent", updateRequest));
        }
    }

    @Nested
    @DisplayName("Activate/Deactivate Usuario Tests")
    class ActivateDeactivateUsuarioTests {

        @Test
        @DisplayName("Should deactivate usuario successfully")
        void shouldDeactivateUsuarioSuccessfully() {
            when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

            assertDoesNotThrow(() -> usuarioService.deactivate(1));

            verify(usuarioRepository, times(1)).save(argThat(savedUsuario -> 
                !savedUsuario.getActivo()
            ));
        }

        @Test
        @DisplayName("Should throw exception when deactivating non-existent usuario")
        void shouldThrowExceptionWhenDeactivatingNonExistentUsuario() {
            when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> usuarioService.deactivate(999));
        }

        @Test
        @DisplayName("Should activate usuario successfully")
        void shouldActivateUsuarioSuccessfully() {
            usuario.setActivo(false);
            when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

            assertDoesNotThrow(() -> usuarioService.activate(1));

            verify(usuarioRepository, times(1)).save(argThat(savedUsuario -> 
                savedUsuario.getActivo()
            ));
        }

        @Test
        @DisplayName("Should throw exception when activating non-existent usuario")
        void shouldThrowExceptionWhenActivatingNonExistentUsuario() {
            when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> usuarioService.activate(999));
        }
    }

    @Nested
    @DisplayName("Change Password Tests")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should change password successfully")
        void shouldChangePasswordSuccessfully() {
            when(usuarioRepository.findByNombreUsuario("testuser")).thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches("currentPassword", "encodedPassword")).thenReturn(true);
            when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

            assertDoesNotThrow(() -> 
                usuarioService.cambiarPassword("testuser", "currentPassword", "newPassword"));

            verify(passwordEncoder, times(1)).encode("newPassword");
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Should throw exception when current password is incorrect")
        void shouldThrowExceptionWhenCurrentPasswordIsIncorrect() {
            when(usuarioRepository.findByNombreUsuario("testuser")).thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

            assertThrows(BadRequestException.class, () -> 
                usuarioService.cambiarPassword("testuser", "wrongPassword", "newPassword"));
        }

        @Test
        @DisplayName("Should throw exception when user not found for password change")
        void shouldThrowExceptionWhenUserNotFoundForPasswordChange() {
            when(usuarioRepository.findByNombreUsuario("nonexistent")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> 
                usuarioService.cambiarPassword("nonexistent", "currentPassword", "newPassword"));
        }
    }
}
