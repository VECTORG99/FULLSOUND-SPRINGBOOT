package Fullsound.Fullsound.service;

import Fullsound.Fullsound.dto.request.ReviewRequest;
import Fullsound.Fullsound.dto.response.ReviewResponse;

import java.util.List;

/**
 * Servicio para la gestion de resenas/valoraciones de beats.
 */
public interface ReviewService {

    /**
     * Crea o actualiza la resena de un usuario sobre un beat.
     * Un usuario solo puede dejar una resena por beat (upsert).
     */
    ReviewResponse createReview(Integer beatId, Integer usuarioId, ReviewRequest request);

    /**
     * Lista las resenas de un beat ordenadas por fecha descendente.
     */
    List<ReviewResponse> getReviewsByBeat(Integer beatId);

    /**
     * Elimina una resena propia. Verifica que la resena pertenezca al usuario.
     */
    void deleteReview(Integer reviewId, Integer usuarioId);
}
