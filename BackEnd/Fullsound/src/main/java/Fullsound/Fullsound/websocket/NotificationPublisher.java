package Fullsound.Fullsound.websocket;

import Fullsound.Fullsound.dto.response.NotificacionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Componente que publica notificaciones en tiempo real a traves de WebSocket/STOMP.
 * Envia las notificaciones al destino por usuario: /user/{userId}/queue/notifications.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Envia una notificacion al usuario indicado via WebSocket.
     * El destino se resuelve a /user/queue/notifications en la sesion STOMP
     * del usuario autenticado cuyo principal coincide con el id.
     */
    public void publishToUser(Integer usuarioId, NotificacionResponse notificacion) {
        try {
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(usuarioId),
                    "/queue/notifications",
                    notificacion
            );
            log.debug("Notificacion publicada via WebSocket para usuario {}: {}", usuarioId, notificacion.getTipo());
        } catch (Exception e) {
            log.warn("No se pudo enviar notificacion via WebSocket al usuario {}: {}", usuarioId, e.getMessage());
        }
    }
}
