package Fullsound.Fullsound.service;

import Fullsound.Fullsound.dto.request.NotificacionRequest;
import Fullsound.Fullsound.dto.response.NotificacionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificacionService {

    NotificacionResponse create(NotificacionRequest request);

    NotificacionResponse createForUsuario(Integer usuarioId, String tipo, String mensaje);

    Page<NotificacionResponse> getByUsuario(Integer usuarioId, Pageable pageable);

    List<NotificacionResponse> getUnreadByUsuario(Integer usuarioId);

    NotificacionResponse markAsRead(Integer id);

    Long countUnread(Integer usuarioId);
}
