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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    @Operation(
        summary = "Actualizar beat",
        description = "Actualiza los datos de un beat existente. Requiere rol de administrador.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Beat actualizado exitosamente", content = @Content(schema = @Schema(implementation = BeatResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Beat no encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<BeatResponse> update(
            @Parameter(description = "ID del beat", required = true, example = "1")
            @PathVariable Integer id,
            @Valid @RequestBody BeatRequest request) {
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
    @Operation(
        summary = "Obtener beat por slug",
        description = "Busca y devuelve un beat específico por su slug (URL amigable)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Beat encontrado", content = @Content(schema = @Schema(implementation = BeatResponse.class))),
        @ApiResponse(responseCode = "404", description = "Beat no encontrado", content = @Content)
    })
    @GetMapping("/slug/{slug}")
    public ResponseEntity<BeatResponse> getBySlug(
            @Parameter(description = "Slug del beat", required = true, example = "test-beat")
            @PathVariable String slug) {
        BeatResponse response = beatService.getBySlug(slug);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Listar beats activos (paginado)",
        description = "Obtiene el catálogo de beats disponibles para compra con soporte de paginación y ordenamiento. Parámetros opcionales: page, size, sort."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Página de beats obtenida exitosamente",
        content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping
    public ResponseEntity<Page<BeatResponse>> getAllActive(@PageableDefault(size = 20) Pageable pageable) {
        Page<BeatResponse> responses = beatService.getAllActive(pageable);
        return ResponseEntity.ok(responses);
    }
    @Operation(
        summary = "Obtener beats destacados",
        description = "Devuelve una lista de beats destacados para mostrar en la página principal."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de beats destacados",
        content = @Content(schema = @Schema(implementation = BeatResponse.class))
    )
    @GetMapping("/featured")
    public ResponseEntity<List<BeatResponse>> getFeatured(@RequestParam(defaultValue = "10") Integer limit) {
        List<BeatResponse> responses = beatService.getFeatured();
        return ResponseEntity.ok(responses);
    }
    @Operation(
        summary = "Buscar beats (paginado)",
        description = "Busca beats por título, artista, etiquetas o género con soporte de paginación."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Página de resultados de búsqueda",
        content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping("/search")
    public ResponseEntity<Page<BeatResponse>> search(
            @Parameter(description = "Término de búsqueda", required = true, example = "trap")
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BeatResponse> responses = beatService.search(q, pageable);
        return ResponseEntity.ok(responses);
    }
    @Operation(
        summary = "Filtrar beats por rango de precio (paginado)",
        description = "Filtra beats cuyo precio está dentro del rango especificado."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Página de beats filtrados por precio",
        content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping("/filter/price")
    public ResponseEntity<Page<BeatResponse>> filterByPrice(
            @Parameter(description = "Precio mínimo", required = true, example = "5000")
            @RequestParam Integer min,
            @Parameter(description = "Precio máximo", required = true, example = "15000")
            @RequestParam Integer max,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BeatResponse> responses = beatService.filterByPrice(min, max, pageable);
        return ResponseEntity.ok(responses);
    }
    @Operation(
        summary = "Filtrar beats por rango de BPM (paginado)",
        description = "Filtra beats cuyo BPM está dentro del rango especificado."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Página de beats filtrados por BPM",
        content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping("/filter/bpm")
    public ResponseEntity<Page<BeatResponse>> filterByBpm(
            @Parameter(description = "BPM mínimo", required = true, example = "90")
            @RequestParam Integer min,
            @Parameter(description = "BPM máximo", required = true, example = "140")
            @RequestParam Integer max,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BeatResponse> responses = beatService.filterByBpm(min, max, pageable);
        return ResponseEntity.ok(responses);
    }
    @Operation(
        summary = "Eliminar beat",
        description = "Elimina un beat del catálogo. Requiere rol de administrador.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Beat eliminado exitosamente", content = @Content(schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "404", description = "Beat no encontrado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "ID del beat", required = true, example = "1")
            @PathVariable Integer id) {
        beatService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Beat eliminado correctamente", true));
    }
    @Operation(
        summary = "Incrementar reproducciones",
        description = "Incrementa el contador de reproducciones de un beat."
    )
    @ApiResponse(responseCode = "200", description = "Reproducción registrada", content = @Content)
    @ApiResponse(responseCode = "404", description = "Beat no encontrado", content = @Content)
    @PostMapping("/{id}/play")
    public ResponseEntity<Void> incrementPlays(
            @Parameter(description = "ID del beat", required = true, example = "1")
            @PathVariable Integer id) {
        beatService.incrementPlays(id);
        return ResponseEntity.ok().build();
    }
}
