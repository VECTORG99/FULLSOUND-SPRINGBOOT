package Fullsound.Fullsound.service.impl;

import Fullsound.Fullsound.dto.request.NotificacionRequest;
import Fullsound.Fullsound.dto.response.NotificacionResponse;
import Fullsound.Fullsound.exception.BadRequestException;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.mapper.NotificacionMapper;
import Fullsound.Fullsound.model.Notificacion;
import Fullsound.Fullsound.model.Usuario;
import Fullsound.Fullsound.repository.NotificacionRepository;
import Fullsound.Fullsound.repository.UsuarioRepository;
import Fullsound.Fullsound.service.NotificacionService;
import Fullsound.Fullsound.websocket.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionMapper notificacionMapper;
    private final NotificationPublisher notificationPublisher;

    @Override
    @Transactional
    public NotificacionResponse create(NotificacionRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.getUsuarioId().toString()));
        Notificacion notificacion = Notificacion.builder()
                .usuario(usuario)
                .tipo(request.getTipo())
                .mensaje(request.getMensaje())
                .leido(false)
                .build();
        Notificacion saved = notificacionRepository.save(notificacion);
        NotificacionResponse response = notificacionMapper.toResponse(saved);
        notificationPublisher.publishToUser(usuario.getId(), response);
        return response;
    }

    @Override
    @Transactional
    public NotificacionResponse createForUsuario(Integer usuarioId, String tipo, String mensaje) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId.toString()));
        Notificacion notificacion = Notificacion.builder()
                .usuario(usuario)
                .tipo(tipo)
                .mensaje(mensaje)
                .leido(false)
                .build();
        Notificacion saved = notificacionRepository.save(notificacion);
        NotificacionResponse response = notificacionMapper.toResponse(saved);
        notificationPublisher.publishToUser(usuarioId, response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificacionResponse> getByUsuario(Integer usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId.toString()));
        return notificacionRepository.findByUsuarioOrderByCreatedAtDesc(usuario, pageable)
                .map(notificacionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponse> getUnreadByUsuario(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId.toString()));
        return notificacionRepository.findByUsuarioAndLeidoFalseOrderByCreatedAtDesc(usuario).stream()
                .map(notificacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificacionResponse markAsRead(Integer id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacion", "id", id.toString()));
        if (Boolean.TRUE.equals(notificacion.getLeido())) {
            throw new BadRequestException("La notificación ya está marcada como leída");
        }
        notificacion.setLeido(true);
        Notificacion updated = notificacionRepository.save(notificacion);
        return notificacionMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUnread(Integer usuarioId) {
        return notificacionRepository.countUnreadByUsuarioId(usuarioId);
    }
}
