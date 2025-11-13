package Fullsound.Fullsound.controller;

import Fullsound.Fullsound.dto.request.BeatRequest;
import Fullsound.Fullsound.dto.response.BeatResponse;
import Fullsound.Fullsound.dto.response.MessageResponse;
import Fullsound.Fullsound.service.BeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para la gestión de beats.
 */
@RestController
@RequestMapping("/api/beats")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:4200", "http://localhost:8080"})
public class BeatController {

    private final BeatService beatService;

    /**
     * Crea un nuevo beat (solo administradores).
     *
     * @param request datos del beat
     * @return beat creado
     */
    @PostMapping
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<BeatResponse> create(@Valid @RequestBody BeatRequest request) {
        BeatResponse response = beatService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza un beat existente (solo administradores).
     *
     * @param id ID del beat
     * @param request datos actualizados
     * @return beat actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<BeatResponse> update(@PathVariable Integer id, @Valid @RequestBody BeatRequest request) {
        BeatResponse response = beatService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un beat por su ID.
     *
     * @param id ID del beat
     * @return beat encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<BeatResponse> getById(@PathVariable Integer id) {
        BeatResponse response = beatService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un beat por su slug.
     *
     * @param slug slug del beat
     * @return beat encontrado
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<BeatResponse> getBySlug(@PathVariable String slug) {
        BeatResponse response = beatService.getBySlug(slug);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todos los beats activos.
     *
     * @return lista de beats activos
     */
    @GetMapping
    public ResponseEntity<List<BeatResponse>> getAllActive() {
        List<BeatResponse> responses = beatService.getAllActive();
        return ResponseEntity.ok(responses);
    }

    /**
     * Obtiene los beats destacados.
     *
     * @param limit cantidad máxima de beats (opcional, default 10)
     * @return lista de beats destacados
     */
    @GetMapping("/featured")
    public ResponseEntity<List<BeatResponse>> getFeatured(@RequestParam(defaultValue = "10") Integer limit) {
        List<BeatResponse> responses = beatService.getFeatured(limit);
        return ResponseEntity.ok(responses);
    }

    /**
     * Busca beats por término de búsqueda.
     *
     * @param q término de búsqueda
     * @return lista de beats encontrados
     */
    @GetMapping("/search")
    public ResponseEntity<List<BeatResponse>> search(@RequestParam String q) {
        List<BeatResponse> responses = beatService.search(q);
        return ResponseEntity.ok(responses);
    }

    /**
     * Filtra beats por rango de precio.
     *
     * @param min precio mínimo
     * @param max precio máximo
     * @return lista de beats en el rango
     */
    @GetMapping("/filter/price")
    public ResponseEntity<List<BeatResponse>> filterByPrice(
            @RequestParam BigDecimal min, 
            @RequestParam BigDecimal max) {
        List<BeatResponse> responses = beatService.filterByPrice(min, max);
        return ResponseEntity.ok(responses);
    }

    /**
     * Filtra beats por rango de BPM.
     *
     * @param min BPM mínimo
     * @param max BPM máximo
     * @return lista de beats en el rango
     */
    @GetMapping("/filter/bpm")
    public ResponseEntity<List<BeatResponse>> filterByBpm(
            @RequestParam Integer min, 
            @RequestParam Integer max) {
        List<BeatResponse> responses = beatService.filterByBpm(min, max);
        return ResponseEntity.ok(responses);
    }

    /**
     * Elimina (desactiva) un beat (solo administradores).
     *
     * @param id ID del beat
     * @return mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('administrador')")
    public ResponseEntity<MessageResponse> delete(@PathVariable Integer id) {
        beatService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Beat eliminado correctamente", true));
    }

    /**
     * Incrementa el contador de reproducciones de un beat.
     *
     * @param id ID del beat
     * @return beat actualizado
     */
    @PostMapping("/{id}/play")
    public ResponseEntity<BeatResponse> incrementPlays(@PathVariable Integer id) {
        BeatResponse response = beatService.incrementPlays(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Incrementa el contador de likes de un beat.
     *
     * @param id ID del beat
     * @return beat actualizado
     */
    @PostMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BeatResponse> incrementLikes(@PathVariable Integer id) {
        BeatResponse response = beatService.incrementLikes(id);
        return ResponseEntity.ok(response);
    }
}
