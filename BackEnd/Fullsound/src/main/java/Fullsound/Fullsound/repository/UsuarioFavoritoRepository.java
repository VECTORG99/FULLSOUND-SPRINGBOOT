package Fullsound.Fullsound.repository;

import Fullsound.Fullsound.model.Beat;
import Fullsound.Fullsound.model.Usuario;
import Fullsound.Fullsound.model.UsuarioFavorito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioFavoritoRepository extends JpaRepository<UsuarioFavorito, Integer> {
    Optional<UsuarioFavorito> findByUsuarioAndBeat(Usuario usuario, Beat beat);
    boolean existsByUsuarioAndBeat(Usuario usuario, Beat beat);
    List<UsuarioFavorito> findByUsuarioOrderByCreatedAtDesc(Usuario usuario);
    Page<UsuarioFavorito> findByUsuarioOrderByCreatedAtDesc(Usuario usuario, Pageable pageable);
    long countByBeat(Beat beat);
    void deleteByUsuarioAndBeat(Usuario usuario, Beat beat);
}
