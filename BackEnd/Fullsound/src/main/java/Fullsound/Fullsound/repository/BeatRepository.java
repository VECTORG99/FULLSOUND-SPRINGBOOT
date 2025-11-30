package Fullsound.Fullsound.repository;

import Fullsound.Fullsound.model.Beat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para la entidad Beat.
 * Adaptado al schema de PostgreSQL.
 * 
 * @author VECTORG99
 * @version 2.0.0
 * @since 2025-11-30
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
     * @param estado el estado del beat (DISPONIBLE, VENDIDO, RESERVADO, INACTIVO)
     * @return lista de beats con ese estado
     */
    List<Beat> findByEstado(String estado);
    
    /**
     * Busca beats disponibles (estado DISPONIBLE).
     * 
     * @return lista de beats disponibles
     */
    @Query("SELECT b FROM Beat b WHERE b.estado = 'DISPONIBLE'")
    List<Beat> findAllAvailable();
    
    /**
     * Buscar beats por rango de precio.
     * 
     * @param precioMin precio mínimo
     * @param precioMax precio máximo
     * @return lista de beats en el rango de precio
     */
    List<Beat> findByPrecioBetween(Integer precioMin, Integer precioMax);
    
    /**
     * Busca beats por BPM.
     * 
     * @param bpmMin BPM mínimo
     * @param bpmMax BPM máximo
     * @return lista de beats en el rango de BPM
     */
    List<Beat> findByBpmBetween(Integer bpmMin, Integer bpmMax);
    
    /**
     * Busca beats por tonalidad.
     * 
     * @param tonalidad la tonalidad (ej: "C", "Am", "F#")
     * @return lista de beats con esa tonalidad
     */
    List<Beat> findByTonalidad(String tonalidad);
    
    /**
     * Busca beats por género.
     * 
     * @param genero el género (Trap, Lo-Fi, Hip Hop, etc.)
     * @return lista de beats con ese género
     */
    List<Beat> findByGeneroContainingIgnoreCase(String genero);
    
    /**
     * Búsqueda de beats por título, artista o etiquetas.
     * 
     * @param query término de búsqueda
     * @return lista de beats que coinciden
     */
    @Query("SELECT b FROM Beat b WHERE b.estado = 'DISPONIBLE' AND " +
           "(LOWER(b.titulo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.artista) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.etiquetas) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.genero) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Beat> search(@Param("query") String query);
    
    /**
     * Obtiene los beats más populares por reproducciones.
     * 
     * @param limit número máximo de resultados
     * @return lista de beats más reproducidos
     */
    @Query("SELECT b FROM Beat b WHERE b.estado = 'DISPONIBLE' ORDER BY b.reproducciones DESC LIMIT :limit")
    List<Beat> findTopByOrderByReproduccionesDesc(@Param("limit") int limit);
    
    /**
     * Obtiene los beats más recientes.
     * 
     * @param limit número máximo de resultados
     * @return lista de beats más recientes
     */
    @Query("SELECT b FROM Beat b WHERE b.estado = 'DISPONIBLE' ORDER BY b.createdAt DESC LIMIT :limit")
    List<Beat> findTopByOrderByCreatedAtDesc(@Param("limit") int limit);
}
