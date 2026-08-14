package Fullsound.Fullsound.service;

import Fullsound.Fullsound.dto.request.ReviewRequest;
import Fullsound.Fullsound.dto.response.ReviewResponse;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.exception.UnauthorizedException;
import Fullsound.Fullsound.mapper.ReviewMapper;
import Fullsound.Fullsound.model.Beat;
import Fullsound.Fullsound.model.Review;
import Fullsound.Fullsound.model.Usuario;
import Fullsound.Fullsound.repository.BeatRepository;
import Fullsound.Fullsound.repository.ReviewRepository;
import Fullsound.Fullsound.repository.UsuarioRepository;
import Fullsound.Fullsound.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService - Tests unitarios")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BeatRepository beatRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Beat beat;
    private Usuario usuario;
    private Usuario otroUsuario;
    private Review review;
    private ReviewResponse reviewResponse;

    @BeforeEach
    void setUp() {
        beat = Beat.builder().id(1).titulo("Test Beat").estado("DISPONIBLE").build();
        usuario = Usuario.builder().id(10).nombreUsuario("client_test").correo("c@test.com").build();
        otroUsuario = Usuario.builder().id(20).nombreUsuario("other_user").correo("o@test.com").build();
        review = Review.builder()
                .id(100)
                .beat(beat)
                .usuario(usuario)
                .rating(5)
                .comentario("Beat increíble")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        reviewResponse = ReviewResponse.builder()
                .id(100)
                .beatId(1)
                .usuarioId(10)
                .nombreUsuario("client_test")
                .rating(5)
                .comentario("Beat increíble")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Crear reseña")
    class CreateReviewTests {
        @Test
        @DisplayName("Debe crear reseña nueva cuando no existe previa")
        void shouldCreateNewReview() {
            ReviewRequest request = ReviewRequest.builder().rating(5).comentario("Beat increíble").build();
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            when(usuarioRepository.findById(10)).thenReturn(Optional.of(usuario));
            when(reviewRepository.findByBeatAndUsuario(beat, usuario)).thenReturn(Optional.empty());
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            when(reviewMapper.toResponse(review)).thenReturn(reviewResponse);

            ReviewResponse result = reviewService.createReview(1, 10, request);

            assertNotNull(result);
            assertEquals(5, result.getRating());
            verify(reviewRepository, times(1)).save(any(Review.class));
        }

        @Test
        @DisplayName("Debe actualizar reseña existente (upsert)")
        void shouldUpdateExistingReview() {
            ReviewRequest request = ReviewRequest.builder().rating(3).comentario("Cambié de opinión").build();
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            when(usuarioRepository.findById(10)).thenReturn(Optional.of(usuario));
            when(reviewRepository.findByBeatAndUsuario(beat, usuario)).thenReturn(Optional.of(review));
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            when(reviewMapper.toResponse(review)).thenReturn(reviewResponse);

            ReviewResponse result = reviewService.createReview(1, 10, request);

            assertNotNull(result);
            assertEquals(3, review.getRating());
            verify(reviewRepository, times(1)).save(review);
        }

        @Test
        @DisplayName("Debe lanzar 404 si el beat no existe")
        void shouldThrowWhenBeatNotFound() {
            ReviewRequest request = ReviewRequest.builder().rating(5).build();
            when(beatRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> reviewService.createReview(999, 10, request));
        }

        @Test
        @DisplayName("Debe lanzar 404 si el usuario no existe")
        void shouldThrowWhenUsuarioNotFound() {
            ReviewRequest request = ReviewRequest.builder().rating(5).build();
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            when(usuarioRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> reviewService.createReview(1, 999, request));
        }
    }

    @Nested
    @DisplayName("Listar reseñas")
    class ListReviewsTests {
        @Test
        @DisplayName("Debe listar reseñas de un beat")
        void shouldListReviewsByBeat() {
            Review review2 = Review.builder().id(101).beat(beat).usuario(otroUsuario).rating(4).build();
            when(beatRepository.existsById(1)).thenReturn(true);
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            when(reviewRepository.findByBeatOrderByCreatedAtDesc(beat)).thenReturn(Arrays.asList(review, review2));
            when(reviewMapper.toResponse(review)).thenReturn(reviewResponse);
            ReviewResponse resp2 = ReviewResponse.builder().id(101).beatId(1).usuarioId(20).rating(4).build();
            when(reviewMapper.toResponse(review2)).thenReturn(resp2);

            List<ReviewResponse> result = reviewService.getReviewsByBeat(1);

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Debe lanzar 404 si el beat no existe al listar")
        void shouldThrowWhenBeatNotFoundListing() {
            when(beatRepository.existsById(999)).thenReturn(false);
            assertThrows(ResourceNotFoundException.class, () -> reviewService.getReviewsByBeat(999));
        }
    }

    @Nested
    @DisplayName("Eliminar reseña")
    class DeleteReviewTests {
        @Test
        @DisplayName("Debe eliminar reseña propia")
        void shouldDeleteOwnReview() {
            when(reviewRepository.findById(100)).thenReturn(Optional.of(review));
            doNothing().when(reviewRepository).delete(review);
            assertDoesNotThrow(() -> reviewService.deleteReview(100, 10));
            verify(reviewRepository, times(1)).delete(review);
        }

        @Test
        @DisplayName("Debe lanzar Unauthorized al eliminar reseña ajena")
        void shouldThrowWhenDeletingOtherUserReview() {
            when(reviewRepository.findById(100)).thenReturn(Optional.of(review));
            assertThrows(UnauthorizedException.class, () -> reviewService.deleteReview(100, 20));
            verify(reviewRepository, never()).delete(any(Review.class));
        }

        @Test
        @DisplayName("Debe lanzar 404 si la reseña no existe")
        void shouldThrowWhenReviewNotFound() {
            when(reviewRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> reviewService.deleteReview(999, 10));
        }
    }
}
