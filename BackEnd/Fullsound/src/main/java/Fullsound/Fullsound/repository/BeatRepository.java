package Fullsound.Fullsound.repository;

import Fullsound.Fullsound.enums.EstadoBeat;
import Fullsound.Fullsound.model.Beat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository para la entidad Beat.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Repository
public interface BeatRepository extends JpaRepository<Beat, Integer> {
    
    /**
     * Busca un beat por su slug.
     * 
     * @param slug el slug del beat
     * @return Optional conteniendo el beat si existe
     */
    Optional<Beat> findBySlug(String slug);
    
    /**
     * Busca beats por estado.
     * 
     * @param estado el estado del beat
     * @return lista de beats con ese estado
     */
    List<Beat> findByEstado(EstadoBeat estado);
    
    /**
     * Busca beats activos.
     * 
     * @param activo true para activos, false para inactivos
     * @return lista de beats
     */
    List<Beat> findByActivo(Boolean activo);
    
    /**
     * Busca beats activos (método simplificado).
     * 
     * @return lista de beats activos
     */
    List<Beat> findByActivoTrue();
    
    /**
     * Busca beats destacados.
     * 
     * @return lista de beats destacados
     */
    List<Beat> findByDestacadoTrueAndActivoTrue();
    
    /**
     * Busca beats disponibles y activos.
     * 
     * @return lista de beats disponibles
     */
    List<Beat> findByEstadoAndActivoTrue(EstadoBeat estado);
    
    /**
     * Busca beats por rango de precio.
     * 
     * @param precioMin precio mínimo
     * @param precioMax precio máximo
     * @return lista de beats en el rango de precio
     */
    List<Beat> findByPrecioBetweenAndActivoTrue(BigDecimal precioMin, BigDecimal precioMax);
    
    /**
     * Busca beats por BPM.
     * 
     * @param bpmMin BPM mínimo
     * @param bpmMax BPM máximo
     * @return lista de beats en el rango de BPM
     */
    List<Beat> findByBpmBetweenAndActivoTrue(Integer bpmMin, Integer bpmMax);
    
    /**
     * Busca beats por tonalidad.
     * 
     * @param tonalidad la tonalidad (ej: "C", "Am", "F#")
     * @return lista de beats con esa tonalidad
     */
    List<Beat> findByTonalidadAndActivoTrue(String tonalidad);
    
    /**
     * Busca beats por mood.
     * 
     * @param mood el mood (ej: "happy", "dark", "epic")
     * @return lista de beats con ese mood
     */
    List<Beat> findByMoodContainingIgnoreCaseAndActivoTrue(String mood);
    
    /**
     * Búsqueda de beats por título o artista.
     * 
     * @param query término de búsqueda
     * @return lista de beats que coinciden
     */
    @Query("SELECT b FROM Beat b WHERE b.activo = true AND " +
           "(LOWER(b.titulo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.artista) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.tags) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Beat> search(@Param("query") String query);
    
    /**
     * Obtiene los beats más populares por reproducciones.
     * 
     * @param limit número máximo de resultados
     * @return lista de beats más reproducidos
     */
    @Query("SELECT b FROM Beat b WHERE b.activo = true ORDER BY b.reproducciones DESC LIMIT :limit")
    List<Beat> findTopByOrderByReproduccionesDesc(@Param("limit") int limit);
    
    /**
     * Obtiene los beats más descargados.
     * 
     * @param limit número máximo de resultados
     * @return lista de beats más descargados
     */
    @Query("SELECT b FROM Beat b WHERE b.activo = true ORDER BY b.descargas DESC LIMIT :limit")
    List<Beat> findTopByOrderByDescargasDesc(@Param("limit") int limit);
    
    /**
     * Obtiene los beats con más likes.
     * 
     * @param limit número máximo de resultados
     * @return lista de beats con más likes
     */
    @Query("SELECT b FROM Beat b WHERE b.activo = true ORDER BY b.likes DESC LIMIT :limit")
    List<Beat> findTopByOrderByLikesDesc(@Param("limit") int limit);
}
