package Fullsound.Fullsound.controller;
import Fullsound.Fullsound.dto.request.BeatRequest;
import Fullsound.Fullsound.dto.response.BeatResponse;
import Fullsound.Fullsound.dto.response.MessageResponse;
import Fullsound.Fullsound.service.BeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.List;
@RestController
@RequestMapping("/api/beats")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@Tag(name = "🎵 Beats", description = "Gestión del catálogo de beats musicales")
public class BeatController {
    private final BeatService beatService;
    @Operation(
        summary = "Crear nuevo beat",
        description = "Registra un nuevo beat en el catálogo. Requiere rol de administrador.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Beat creado exitosamente",
            content = @Content(schema = @Schema(implementation = BeatResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<BeatResponse> create(@Valid @RequestBody BeatRequest request) {
        BeatResponse response = beatService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<BeatResponse> update(@PathVariable Integer id, @Valid @RequestBody BeatRequest request) {
        BeatResponse response = beatService.update(id, request);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Obtener beat por ID",
        description = "Busca y devuelve un beat específico por su identificador."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Beat encontrado",
            content = @Content(schema = @Schema(implementation = BeatResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Beat no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<BeatResponse> getById(
        @Parameter(description = "ID del beat", required = true, example = "1")
        @PathVariable Integer id
    ) {
        BeatResponse response = beatService.getById(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/slug/{slug}")
    public ResponseEntity<BeatResponse> getBySlug(@PathVariable String slug) {
        BeatResponse response = beatService.getBySlug(slug);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Listar beats activos",
        description = "Obtiene el catálogo completo de beats disponibles para compra."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de beats obtenida exitosamente",
        content = @Content(schema = @Schema(implementation = BeatResponse.class))
    )
    @GetMapping
    public ResponseEntity<List<BeatResponse>> getAllActive() {
        List<BeatResponse> responses = beatService.getAllActive();
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/featured")
    public ResponseEntity<List<BeatResponse>> getFeatured(@RequestParam(defaultValue = "10") Integer limit) {
        List<BeatResponse> responses = beatService.getFeatured();
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/search")
    public ResponseEntity<List<BeatResponse>> search(@RequestParam String q) {
        List<BeatResponse> responses = beatService.search(q);
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/filter/price")
    public ResponseEntity<List<BeatResponse>> filterByPrice(
            @RequestParam Integer min, 
            @RequestParam Integer max) {
        List<BeatResponse> responses = beatService.filterByPrice(min, max);
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/filter/bpm")
    public ResponseEntity<List<BeatResponse>> filterByBpm(
            @RequestParam Integer min, 
            @RequestParam Integer max) {
        List<BeatResponse> responses = beatService.filterByBpm(min, max);
        return ResponseEntity.ok(responses);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<MessageResponse> delete(@PathVariable Integer id) {
        beatService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Beat eliminado correctamente", true));
    }
    @PostMapping("/{id}/play")
    public ResponseEntity<Void> incrementPlays(@PathVariable Integer id) {
        beatService.incrementPlays(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> incrementLikes(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
            .body(new MessageResponse("La funcionalidad de likes ha sido removida", false));
    }
}
