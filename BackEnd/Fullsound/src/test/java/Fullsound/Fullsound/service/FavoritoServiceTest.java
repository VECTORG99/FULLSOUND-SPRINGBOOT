package Fullsound.Fullsound.service;

import Fullsound.Fullsound.dto.response.BeatResponse;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.mapper.BeatMapper;
import Fullsound.Fullsound.model.Beat;
import Fullsound.Fullsound.model.Usuario;
import Fullsound.Fullsound.model.UsuarioFavorito;
import Fullsound.Fullsound.repository.BeatRepository;
import Fullsound.Fullsound.repository.UsuarioFavoritoRepository;
import Fullsound.Fullsound.repository.UsuarioRepository;
import Fullsound.Fullsound.service.impl.FavoritoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FavoritoService - Tests unitarios")
class FavoritoServiceTest {

    @Mock
    private UsuarioFavoritoRepository usuarioFavoritoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private BeatRepository beatRepository;
    @Mock
    private BeatMapper beatMapper;

    @InjectMocks
    private FavoritoServiceImpl favoritoService;

    private Usuario usuario;
    private Beat beat;
    private BeatResponse beatResponse;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder().id(10).nombreUsuario("client_test").correo("c@test.com").build();
        beat = Beat.builder().id(1).titulo("Test Beat").estado("DISPONIBLE").build();
        beatResponse = BeatResponse.builder().idBeat(1).titulo("Test Beat").estado("DISPONIBLE").build();
    }

    @Nested
    @DisplayName("Añadir favorito")
    class AddFavoritoTests {
        @Test
        @DisplayName("Debe añadir beat a favoritos")
        void shouldAddFavorito() {
            when(usuarioRepository.findById(10)).thenReturn(Optional.of(usuario));
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            when(usuarioFavoritoRepository.existsByUsuarioAndBeat(usuario, beat)).thenReturn(false);
            when(usuarioFavoritoRepository.save(any(UsuarioFavorito.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            assertDoesNotThrow(() -> favoritoService.addFavorito(10, 1));
            verify(usuarioFavoritoRepository, times(1)).save(any(UsuarioFavorito.class));
        }

        @Test
        @DisplayName("No debe duplicar favorito existente")
        void shouldNotDuplicateFavorito() {
            when(usuarioRepository.findById(10)).thenReturn(Optional.of(usuario));
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            when(usuarioFavoritoRepository.existsByUsuarioAndBeat(usuario, beat)).thenReturn(true);

            favoritoService.addFavorito(10, 1);
            verify(usuarioFavoritoRepository, never()).save(any(UsuarioFavorito.class));
        }

        @Test
        @DisplayName("Debe lanzar 404 si el beat no existe")
        void shouldThrowWhenBeatNotFound() {
            when(usuarioRepository.findById(10)).thenReturn(Optional.of(usuario));
            when(beatRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> favoritoService.addFavorito(10, 999));
        }
    }

    @Nested
    @DisplayName("Eliminar favorito")
    class RemoveFavoritoTests {
        @Test
        @DisplayName("Debe eliminar favorito existente")
        void shouldRemoveFavorito() {
            when(usuarioRepository.findById(10)).thenReturn(Optional.of(usuario));
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            when(usuarioFavoritoRepository.existsByUsuarioAndBeat(usuario, beat)).thenReturn(true);
            doNothing().when(usuarioFavoritoRepository).deleteByUsuarioAndBeat(usuario, beat);

            assertDoesNotThrow(() -> favoritoService.removeFavorito(10, 1));
            verify(usuarioFavoritoRepository, times(1)).deleteByUsuarioAndBeat(usuario, beat);
        }

        @Test
        @DisplayName("No debe fallar al eliminar favorito inexistente")
        void shouldNotFailWhenFavoritoMissing() {
            when(usuarioRepository.findById(10)).thenReturn(Optional.of(usuario));
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            when(usuarioFavoritoRepository.existsByUsuarioAndBeat(usuario, beat)).thenReturn(false);

            assertDoesNotThrow(() -> favoritoService.removeFavorito(10, 1));
            verify(usuarioFavoritoRepository, never()).deleteByUsuarioAndBeat(any(), any());
        }
    }

    @Nested
    @DisplayName("Listar y consultar favoritos")
    class ListAndCheckFavoritoTests {
        @Test
        @DisplayName("Debe listar favoritos paginados")
        void shouldListFavoritos() {
            UsuarioFavorito fav = UsuarioFavorito.builder().id(1).usuario(usuario).beat(beat).build();
            Page<UsuarioFavorito> page = new PageImpl<>(Arrays.asList(fav));
            Pageable pageable = PageRequest.of(0, 10);

            when(usuarioRepository.findById(10)).thenReturn(Optional.of(usuario));
            when(usuarioFavoritoRepository.findByUsuarioOrderByCreatedAtDesc(usuario, pageable)).thenReturn(page);
            when(beatMapper.toResponse(beat)).thenReturn(beatResponse);

            Page<BeatResponse> result = favoritoService.getFavoritos(10, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("Test Beat", result.getContent().get(0).getTitulo());
        }

        @Test
        @DisplayName("Debe indicar si un beat es favorito")
        void shouldCheckIsFavorito() {
            when(usuarioRepository.findById(10)).thenReturn(Optional.of(usuario));
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            when(usuarioFavoritoRepository.existsByUsuarioAndBeat(usuario, beat)).thenReturn(true);

            assertTrue(favoritoService.isFavorito(10, 1));
        }

        @Test
        @DisplayName("Debe indicar false cuando no es favorito")
        void shouldCheckIsNotFavorito() {
            when(usuarioRepository.findById(10)).thenReturn(Optional.of(usuario));
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            when(usuarioFavoritoRepository.existsByUsuarioAndBeat(usuario, beat)).thenReturn(false);

            assertFalse(favoritoService.isFavorito(10, 1));
        }
    }
}
