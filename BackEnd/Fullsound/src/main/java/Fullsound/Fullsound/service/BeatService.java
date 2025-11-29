package Fullsound.Fullsound.service;

import Fullsound.Fullsound.dto.request.BeatRequest;
import Fullsound.Fullsound.dto.response.BeatResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de gestión de beats.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
public interface BeatService {
    
    /**
     * Crea un nuevo beat.
     */
    BeatResponse create(BeatRequest request);
    
    /**
     * Actualiza un beat existente.
     */
    BeatResponse update(Integer id, BeatRequest request);
    
    /**
     * Obtiene un beat por ID.
     */
    BeatResponse getById(Integer id);
    
    /**
     * Obtiene un beat por slug.
     */
    BeatResponse getBySlug(String slug);
    
    /**
     * Obtiene todos los beats activos.
     */
    List<BeatResponse> getAllActive();
    
    /**
     * Obtiene beats destacados.
     */
    List<BeatResponse> getFeatured();
    
    /**
     * Busca beats por término.
     */
    List<BeatResponse> search(String query);
    
    /**
     * Filtra beats por precio.
     */
    List<BeatResponse> filterByPrice(Integer min, Integer max);
    
    /**
     * Filtra beats por BPM.
     */
    List<BeatResponse> filterByBpm(Integer min, Integer max);
    
    /**
     * Elimina un beat (soft delete).
     */
    void delete(Integer id);
    
    /**
     * Incrementa las reproducciones de un beat.
     */
    void incrementPlays(Integer id);
    
    /**
     * Incrementa los likes de un beat.
     */
    void incrementLikes(Integer id);
}
