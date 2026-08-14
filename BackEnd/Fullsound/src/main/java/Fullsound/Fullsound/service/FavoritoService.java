package Fullsound.Fullsound.service;

import Fullsound.Fullsound.dto.response.BeatResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para la gestion de favoritos/wishlist de usuarios.
 */
public interface FavoritoService {

    /**
     * Marca un beat como favorito para el usuario. Si ya existe, no duplica.
     */
    void addFavorito(Integer usuarioId, Integer beatId);

    /**
     * Elimina un beat de los favoritos del usuario. No falla si no existia.
     */
    void removeFavorito(Integer usuarioId, Integer beatId);

    /**
     * Lista los beats favoritos del usuario (paginado).
     */
    Page<BeatResponse> getFavoritos(Integer usuarioId, Pageable pageable);

    /**
     * Indica si un beat esta marcado como favorito por el usuario.
     */
    boolean isFavorito(Integer usuarioId, Integer beatId);
}
