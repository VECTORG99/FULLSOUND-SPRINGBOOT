package Fullsound.Fullsound.controller;

import Fullsound.Fullsound.dto.response.MessageResponse;
import Fullsound.Fullsound.service.ReviewService;
import Fullsound.Fullsound.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para operaciones sobre reseñas individuales (p.ej. eliminación).
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@Tag(name = "⭐ Reseñas", description = "Gestión de reseñas y valoraciones de beats")
public class ReviewController {

    private final ReviewService reviewService;
    private final UsuarioService usuarioService;

    @Operation(
        summary = "Eliminar reseña propia",
        description = "Elimina una reseña perteneciente al usuario autenticado. No se pueden eliminar reseñas de otros usuarios.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reseña eliminada", content = @Content(schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autorizado (no es tu reseña)", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> deleteReview(
            @Parameter(description = "ID de la reseña", required = true, example = "1")
            @PathVariable Integer id,
            Authentication authentication) {
        Integer usuarioId = usuarioService.getIdByNombreUsuario(authentication.getName());
        reviewService.deleteReview(id, usuarioId);
        return ResponseEntity.ok(new MessageResponse("Reseña eliminada correctamente", true));
    }
}
