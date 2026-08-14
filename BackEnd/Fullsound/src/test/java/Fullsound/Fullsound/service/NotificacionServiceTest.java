package Fullsound.Fullsound.service;

import Fullsound.Fullsound.dto.request.NotificacionRequest;
import Fullsound.Fullsound.dto.response.NotificacionResponse;
import Fullsound.Fullsound.exception.BadRequestException;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.mapper.NotificacionMapper;
import Fullsound.Fullsound.model.Notificacion;
import Fullsound.Fullsound.model.Usuario;
import Fullsound.Fullsound.repository.NotificacionRepository;
import Fullsound.Fullsound.repository.UsuarioRepository;
import Fullsound.Fullsound.service.impl.NotificacionServiceImpl;
import Fullsound.Fullsound.websocket.NotificationPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacionService - Tests unitarios")
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private NotificacionMapper notificacionMapper;
    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private NotificacionServiceImpl notificacionService;

    private Usuario usuario;
    private Notificacion notificacion;
    private NotificacionResponse notificacionResponse;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder().id(10).nombreUsuario("client_test").correo("c@test.com").build();
        notificacion = Notificacion.builder()
                .id(1)
                .usuario(usuario)
                .tipo("SISTEMA")
                .mensaje("Tu pedido fue completado")
                .leido(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificacionResponse = NotificacionResponse.builder()
                .id(1)
                .usuarioId(10)
                .tipo("SISTEMA")
                .mensaje("Tu pedido fue completado")
                .leido(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Crear notificación")
    class CreateTests {
        @Test
        @DisplayName("Debe crear notificación desde request")
        void shouldCreateFromRequest() {
            NotificacionRequest request = NotificacionRequest.builder()
                    .usuarioId(10).tipo("SISTEMA").mensaje("Hola").build();
            when(usuarioRepository.findById(10)).thenReturn(Optional.of(usuario));
            when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);
            when(notificacionMapper.toResponse(notificacion)).thenReturn(notificacionResponse);

            NotificacionResponse result = notificacionService.create(request);

            assertNotNull(result);
            assertEquals("SISTEMA", result.getTipo());
            verify(notificationPublisher, times(1)).publishToUser(eq(10), any(NotificacionResponse.class));
        }

        @Test
        @DisplayName("Debe crear notificación programática (createForUsuario)")
        void shouldCreateForUsuario() {
            when(usuarioRepository.findById(10)).thenReturn(Optional.of(usuario));
            when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);
            when(notificacionMapper.toResponse(notificacion)).thenReturn(notificacionResponse);

            NotificacionResponse result = notificacionService.createForUsuario(10, "COMPRA", "Pedido ok");

            assertNotNull(result);
            assertEquals("SISTEMA", result.getTipo());
            verify(notificationPublisher, times(1)).publishToUser(eq(10), any(NotificacionResponse.class));
        }

        @Test
        @DisplayName("Debe lanzar 404 si el usuario no existe")
        void shouldThrowWhenUsuarioNotFound() {
            NotificacionRequest request = NotificacionRequest.builder()
                    .usuarioId(999).tipo("SISTEMA").mensaje("Hola").build();
            when(usuarioRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> notificacionService.create(request));
        }
    }

    @Nested
    @DisplayName("Consultar notificaciones")
    class ReadTests {
        @Test
        @DisplayName("Debe listar notificaciones paginadas del usuario")
        void shouldGetByUsuario() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Notificacion> page = new PageImpl<>(Arrays.asList(notificacion));
            when(usuarioRepository.findById(10)).thenReturn(Optional.of(usuario));
            when(notificacionRepository.findByUsuarioOrderByCreatedAtDesc(usuario, pageable)).thenReturn(page);
            when(notificacionMapper.toResponse(notificacion)).thenReturn(notificacionResponse);

            Page<NotificacionResponse> result = notificacionService.getByUsuario(10, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Debe listar notificaciones no leídas")
        void shouldGetUnreadByUsuario() {
            when(usuarioRepository.findById(10)).thenReturn(Optional.of(usuario));
            when(notificacionRepository.findByUsuarioAndLeidoFalseOrderByCreatedAtDesc(usuario))
                    .thenReturn(Arrays.asList(notificacion));
            when(notificacionMapper.toResponse(notificacion)).thenReturn(notificacionResponse);

            List<NotificacionResponse> result = notificacionService.getUnreadByUsuario(10);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertFalse(result.get(0).getLeido());
        }

        @Test
        @DisplayName("Debe contar notificaciones no leídas")
        void shouldCountUnread() {
            when(notificacionRepository.countUnreadByUsuarioId(10)).thenReturn(3L);
            Long count = notificacionService.countUnread(10);
            assertEquals(3L, count);
        }
    }

    @Nested
    @DisplayName("Marcar como leída")
    class MarkAsReadTests {
        @Test
        @DisplayName("Debe marcar notificación como leída")
        void shouldMarkAsRead() {
            Notificacion leida = Notificacion.builder()
                    .id(1).usuario(usuario).tipo("SISTEMA").mensaje("msg").leido(true).build();
            when(notificacionRepository.findById(1)).thenReturn(Optional.of(notificacion));
            when(notificacionRepository.save(any(Notificacion.class))).thenReturn(leida);
            NotificacionResponse respLeida = NotificacionResponse.builder()
                    .id(1).usuarioId(10).tipo("SISTEMA").mensaje("msg").leido(true).build();
            when(notificacionMapper.toResponse(leida)).thenReturn(respLeida);

            NotificacionResponse result = notificacionService.markAsRead(1);

            assertNotNull(result);
            assertTrue(result.getLeido());
        }

        @Test
        @DisplayName("Debe lanzar BadRequest si ya estaba leída")
        void shouldThrowWhenAlreadyRead() {
            notificacion.setLeido(true);
            when(notificacionRepository.findById(1)).thenReturn(Optional.of(notificacion));
            assertThrows(BadRequestException.class, () -> notificacionService.markAsRead(1));
        }

        @Test
        @DisplayName("Debe lanzar 404 si la notificación no existe")
        void shouldThrowWhenNotFound() {
            when(notificacionRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> notificacionService.markAsRead(999));
        }
    }
}
