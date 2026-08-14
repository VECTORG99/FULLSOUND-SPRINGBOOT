package Fullsound.Fullsound.repository;
import Fullsound.Fullsound.model.Beat;
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
    List<Beat> findByPrecioBetween(Integer precioMin, Integer precioMax);
    List<Beat> findByBpmBetween(Integer bpmMin, Integer bpmMax);
    List<Beat> findByTonalidad(String tonalidad);
    List<Beat> findByGeneroContainingIgnoreCase(String genero);
    @Query("SELECT b FROM Beat b WHERE b.estado = 'DISPONIBLE' AND " +
           "(LOWER(b.titulo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.artista) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.etiquetas) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.genero) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Beat> search(@Param("query") String query);
    @Query("SELECT b FROM Beat b WHERE b.estado = 'DISPONIBLE' ORDER BY b.reproducciones DESC LIMIT :limit")
    List<Beat> findTopByOrderByReproduccionesDesc(@Param("limit") int limit);
    @Query("SELECT b FROM Beat b WHERE b.estado = 'DISPONIBLE' ORDER BY b.createdAt DESC LIMIT :limit")
    List<Beat> findTopByOrderByCreatedAtDesc(@Param("limit") int limit);
}
