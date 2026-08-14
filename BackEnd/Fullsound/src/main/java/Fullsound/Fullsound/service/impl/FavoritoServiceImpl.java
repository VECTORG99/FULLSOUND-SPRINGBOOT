package Fullsound.Fullsound.service.impl;

import Fullsound.Fullsound.dto.response.BeatResponse;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.mapper.BeatMapper;
import Fullsound.Fullsound.model.Beat;
import Fullsound.Fullsound.model.Usuario;
import Fullsound.Fullsound.model.UsuarioFavorito;
import Fullsound.Fullsound.repository.BeatRepository;
import Fullsound.Fullsound.repository.UsuarioFavoritoRepository;
import Fullsound.Fullsound.repository.UsuarioRepository;
import Fullsound.Fullsound.service.FavoritoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoritoServiceImpl implements FavoritoService {

    private final UsuarioFavoritoRepository usuarioFavoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BeatRepository beatRepository;
    private final BeatMapper beatMapper;

    @Override
    @Transactional
    public void addFavorito(Integer usuarioId, Integer beatId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
        Beat beat = beatRepository.findById(beatId)
                .orElseThrow(() -> new ResourceNotFoundException("Beat", "id", beatId));
        if (usuarioFavoritoRepository.existsByUsuarioAndBeat(usuario, beat)) {
            log.debug("Beat {} ya es favorito del usuario {}", beatId, usuarioId);
            return;
        }
        UsuarioFavorito favorito = UsuarioFavorito.builder()
                .usuario(usuario)
                .beat(beat)
                .build();
        usuarioFavoritoRepository.save(favorito);
        log.info("Beat {} marcado como favorito por usuario {}", beatId, usuarioId);
    }

    @Override
    @Transactional
    public void removeFavorito(Integer usuarioId, Integer beatId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
        Beat beat = beatRepository.findById(beatId)
                .orElseThrow(() -> new ResourceNotFoundException("Beat", "id", beatId));
        if (!usuarioFavoritoRepository.existsByUsuarioAndBeat(usuario, beat)) {
            log.debug("Beat {} no es favorito del usuario {} (nada que eliminar)", beatId, usuarioId);
            return;
        }
        usuarioFavoritoRepository.deleteByUsuarioAndBeat(usuario, beat);
        log.info("Beat {} eliminado de favoritos del usuario {}", beatId, usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BeatResponse> getFavoritos(Integer usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
        return usuarioFavoritoRepository.findByUsuarioOrderByCreatedAtDesc(usuario, pageable)
                .map(UsuarioFavorito::getBeat)
                .map(beatMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorito(Integer usuarioId, Integer beatId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
        Beat beat = beatRepository.findById(beatId)
                .orElseThrow(() -> new ResourceNotFoundException("Beat", "id", beatId));
        return usuarioFavoritoRepository.existsByUsuarioAndBeat(usuario, beat);
    }
}
