package Fullsound.Fullsound.service.impl;

import Fullsound.Fullsound.dto.request.ReviewRequest;
import Fullsound.Fullsound.dto.response.ReviewResponse;
import Fullsound.Fullsound.exception.BadRequestException;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.exception.UnauthorizedException;
import Fullsound.Fullsound.mapper.ReviewMapper;
import Fullsound.Fullsound.model.Beat;
import Fullsound.Fullsound.model.Review;
import Fullsound.Fullsound.model.Usuario;
import Fullsound.Fullsound.repository.BeatRepository;
import Fullsound.Fullsound.repository.ReviewRepository;
import Fullsound.Fullsound.repository.UsuarioRepository;
import Fullsound.Fullsound.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BeatRepository beatRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewResponse createReview(Integer beatId, Integer usuarioId, ReviewRequest request) {
        Beat beat = beatRepository.findById(beatId)
                .orElseThrow(() -> new ResourceNotFoundException("Beat", "id", beatId));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));

        // Upsert: si el usuario ya reseño este beat, se actualiza la valoración.
        Review review = reviewRepository.findByBeatAndUsuario(beat, usuario)
                .orElseGet(() -> Review.builder()
                        .beat(beat)
                        .usuario(usuario)
                        .build());
        review.setRating(request.getRating());
        review.setComentario(request.getComentario());

        Review saved = reviewRepository.save(review);
        log.info("Resena guardada para beat {} por usuario {} (rating={})", beatId, usuarioId, request.getRating());
        return reviewMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByBeat(Integer beatId) {
        if (!beatRepository.existsById(beatId)) {
            throw new ResourceNotFoundException("Beat", "id", beatId);
        }
        return reviewRepository.findByBeatOrderByCreatedAtDesc(
                        beatRepository.findById(beatId).get())
                .stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReview(Integer reviewId, Integer usuarioId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        if (!review.getUsuario().getId().equals(usuarioId)) {
            throw new UnauthorizedException("No puedes eliminar una reseña que no te pertenece");
        }
        reviewRepository.delete(review);
        log.info("Resena {} eliminada por usuario {}", reviewId, usuarioId);
    }
}
