package Fullsound.Fullsound.repository;

import Fullsound.Fullsound.model.Beat;
import Fullsound.Fullsound.model.Review;
import Fullsound.Fullsound.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByBeatOrderByCreatedAtDesc(Beat beat);
    Optional<Review> findByBeatAndUsuario(Beat beat, Usuario usuario);
    boolean existsByBeatAndUsuario(Beat beat, Usuario usuario);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.beat.id = :beatId")
    Double getAverageRatingForBeat(@Param("beatId") Integer beatId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.beat.id = :beatId")
    Long countByBeatId(@Param("beatId") Integer beatId);
}
