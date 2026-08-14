package Fullsound.Fullsound.repository;
import Fullsound.Fullsound.model.Beat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface BeatRepository extends JpaRepository<Beat, Integer> {
    Optional<Beat> findBySlug(String slug);
    List<Beat> findByEstado(String estado);
    @Query("SELECT b FROM Beat b WHERE b.estado = 'DISPONIBLE'")
    List<Beat> findAllAvailable();
    Page<Beat> findByEstado(String estado, Pageable pageable);
    List<Beat> findByPrecioBetween(Integer precioMin, Integer precioMax);
    Page<Beat> findByPrecioBetween(Integer precioMin, Integer precioMax, Pageable pageable);
    List<Beat> findByBpmBetween(Integer bpmMin, Integer bpmMax);
    Page<Beat> findByBpmBetween(Integer bpmMin, Integer bpmMax, Pageable pageable);
    List<Beat> findByTonalidad(String tonalidad);
    List<Beat> findByGeneroContainingIgnoreCase(String genero);
    @Query("SELECT b FROM Beat b WHERE b.estado = 'DISPONIBLE' AND " +
           "(LOWER(b.titulo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.artista) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.etiquetas) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.genero) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Beat> search(@Param("query") String query);
    @Query("SELECT b FROM Beat b WHERE b.estado = 'DISPONIBLE' AND " +
           "(LOWER(b.titulo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.artista) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.etiquetas) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.genero) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.descripcion) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Beat> search(@Param("query") String query, Pageable pageable);

    /**
     * Busqueda full-text nativa de PostgreSQL usando tsvector/tsquery con
     * ranking (ts_rank) y la columna generada search_vector. Solo funciona
     * en PostgreSQL; en otros motores (H2) lanza una excepcion que el
     * servicio captura para degradar a la busqueda LIKE.
     */
    @Query(value = "SELECT b.* FROM beat b " +
                   "WHERE b.estado = 'DISPONIBLE' " +
                   "AND b.search_vector @@ plainto_tsquery('spanish', :query) " +
                   "ORDER BY ts_rank(b.search_vector, plainto_tsquery('spanish', :query)) DESC, " +
                   "b.reproducciones DESC",
           countQuery = "SELECT count(*) FROM beat b " +
                   "WHERE b.estado = 'DISPONIBLE' " +
                   "AND b.search_vector @@ plainto_tsquery('spanish', :query)",
           nativeQuery = true)
    Page<Beat> searchFullText(@Param("query") String query, Pageable pageable);
    @Query("SELECT b FROM Beat b WHERE b.estado = 'DISPONIBLE' ORDER BY b.reproducciones DESC LIMIT :limit")
    List<Beat> findTopByOrderByReproduccionesDesc(@Param("limit") int limit);
    @Query("SELECT b FROM Beat b WHERE b.estado = 'DISPONIBLE' ORDER BY b.createdAt DESC LIMIT :limit")
    List<Beat> findTopByOrderByCreatedAtDesc(@Param("limit") int limit);
}
