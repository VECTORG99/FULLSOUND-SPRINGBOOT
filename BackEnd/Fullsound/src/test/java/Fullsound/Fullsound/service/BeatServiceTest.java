package Fullsound.Fullsound.service;
import Fullsound.Fullsound.dto.request.BeatRequest;
import Fullsound.Fullsound.dto.response.BeatResponse;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.mapper.BeatMapper;
import Fullsound.Fullsound.model.Beat;
import Fullsound.Fullsound.repository.BeatRepository;
import Fullsound.Fullsound.service.impl.BeatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class BeatServiceTest {
    @Mock
    private BeatRepository beatRepository;
    @Mock
    private BeatMapper beatMapper;
    @InjectMocks
    private BeatServiceImpl beatService;
    private Beat beat;
    private BeatRequest beatRequest;
    private BeatResponse beatResponse;
    @BeforeEach
    void setUp() {
        beat = Beat.builder()
                .id(1)
                .titulo("Test Beat")
                .slug("test-beat")
                .artista("Test Artist")
                .precio(10000)
                .bpm(120)
                .tonalidad("Am")
                .duracion(180)
                .genero("Trap")
                .etiquetas("trap,dark,808")
                .descripcion("Test description")
                .imagenUrl("http://example.com/image.jpg")
                .audioUrl("http://example.com/audio.mp3")
                .audioDemoUrl("http://example.com/demo.mp3")
                .reproducciones(0)
                .estado("DISPONIBLE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        beatRequest = BeatRequest.builder()
                .titulo("Test Beat")
                .artista("Test Artist")
                .precio(10000)
                .bpm(120)
                .tonalidad("Am")
                .duracion(180)
                .genero("Trap")
                .etiquetas("trap,dark,808")
                .descripcion("Test description")
                .imagenUrl("http://example.com/image.jpg")
                .audioUrl("http://example.com/audio.mp3")
                .audioDemoUrl("http://example.com/demo.mp3")
                .estado("DISPONIBLE")
                .build();
        beatResponse = BeatResponse.builder()
                .idBeat(1)
                .titulo("Test Beat")
                .slug("test-beat")
                .artista("Test Artist")
                .precio(10000)
                .precioFormateado("$10,000")
                .bpm(120)
                .tonalidad("Am")
                .duracion(180)
                .genero("Trap")
                .etiquetas("trap,dark,808")
                .descripcion("Test description")
                .imagenUrl("http://example.com/image.jpg")
                .audioUrl("http://example.com/audio.mp3")
                .audioDemoUrl("http://example.com/demo.mp3")
                .reproducciones(0)
                .estado("DISPONIBLE")
                .enlaceProducto("/beats/test-beat")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    @Nested
    @DisplayName("Create Beat Tests")
    class CreateBeatTests {
        @Test
        @DisplayName("Should create beat successfully")
        void shouldCreateBeatSuccessfully() {
            when(beatMapper.toEntity(any(BeatRequest.class))).thenReturn(beat);
            when(beatRepository.findBySlug(anyString())).thenReturn(Optional.empty());
            when(beatRepository.save(any(Beat.class))).thenReturn(beat);
            when(beatMapper.toResponse(any(Beat.class))).thenReturn(beatResponse);
            BeatResponse result = beatService.create(beatRequest);
            assertNotNull(result);
            assertEquals("Test Beat", result.getTitulo());
            assertEquals("Test Artist", result.getArtista());
            assertEquals(10000, result.getPrecio());
            verify(beatRepository, times(1)).save(any(Beat.class));
        }
        @Test
        @DisplayName("Should generate unique slug")
        void shouldGenerateUniqueSlug() {
            when(beatMapper.toEntity(any(BeatRequest.class))).thenReturn(beat);
            when(beatRepository.findBySlug("test-beat")).thenReturn(Optional.of(beat));
            when(beatRepository.findBySlug("test-beat-1")).thenReturn(Optional.empty());
            when(beatRepository.save(any(Beat.class))).thenReturn(beat);
            when(beatMapper.toResponse(any(Beat.class))).thenReturn(beatResponse);
            BeatResponse result = beatService.create(beatRequest);
            assertNotNull(result);
            verify(beatRepository, atLeast(1)).findBySlug(anyString());
        }
    }
    @Nested
    @DisplayName("Read Beat Tests")
    class ReadBeatTests {
        @Test
        @DisplayName("Should get beat by ID successfully")
        void shouldGetBeatByIdSuccessfully() {
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            when(beatMapper.toResponse(beat)).thenReturn(beatResponse);
            BeatResponse result = beatService.getById(1);
            assertNotNull(result);
            assertEquals(1, result.getIdBeat());
            assertEquals("Test Beat", result.getTitulo());
        }
        @Test
        @DisplayName("Should throw exception when beat not found by ID")
        void shouldThrowExceptionWhenBeatNotFoundById() {
            when(beatRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> beatService.getById(999));
        }
        @Test
        @DisplayName("Should get beat by slug successfully")
        void shouldGetBeatBySlugSuccessfully() {
            when(beatRepository.findBySlug("test-beat")).thenReturn(Optional.of(beat));
            when(beatMapper.toResponse(beat)).thenReturn(beatResponse);
            BeatResponse result = beatService.getBySlug("test-beat");
            assertNotNull(result);
            assertEquals("test-beat", result.getSlug());
        }
        @Test
        @DisplayName("Should throw exception when beat not found by slug")
        void shouldThrowExceptionWhenBeatNotFoundBySlug() {
            when(beatRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> beatService.getBySlug("nonexistent"));
        }
        @Test
        @DisplayName("Should get all active beats")
        void shouldGetAllActiveBeats() {
            Beat beat2 = Beat.builder()
                    .id(2)
                    .titulo("Beat 2")
                    .estado("DISPONIBLE")
                    .build();
            BeatResponse response2 = BeatResponse.builder()
                    .idBeat(2)
                    .titulo("Beat 2")
                    .estado("DISPONIBLE")
                    .build();
            when(beatRepository.findAll()).thenReturn(Arrays.asList(beat, beat2));
            when(beatMapper.toResponse(beat)).thenReturn(beatResponse);
            when(beatMapper.toResponse(beat2)).thenReturn(response2);
            List<BeatResponse> result = beatService.getAllActive();
            assertNotNull(result);
            assertEquals(2, result.size());
        }
        @Test
        @DisplayName("Should search beats by query")
        void shouldSearchBeatsByQuery() {
            when(beatRepository.search("trap")).thenReturn(Arrays.asList(beat));
            when(beatMapper.toResponse(beat)).thenReturn(beatResponse);
            List<BeatResponse> result = beatService.search("trap");
            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(result.get(0).getGenero().toLowerCase().contains("trap"));
        }
        @Test
        @DisplayName("Should filter beats by price range")
        void shouldFilterBeatsByPriceRange() {
            when(beatRepository.findAll()).thenReturn(Arrays.asList(beat));
            when(beatMapper.toResponse(beat)).thenReturn(beatResponse);
            List<BeatResponse> result = beatService.filterByPrice(5000, 15000);
            assertNotNull(result);
            assertEquals(1, result.size());
        }
        @Test
        @DisplayName("Should filter beats by BPM range")
        void shouldFilterBeatsByBpmRange() {
            when(beatRepository.findAll()).thenReturn(Arrays.asList(beat));
            when(beatMapper.toResponse(beat)).thenReturn(beatResponse);
            List<BeatResponse> result = beatService.filterByBpm(100, 140);
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }
    @Nested
    @DisplayName("Update Beat Tests")
    class UpdateBeatTests {
        @Test
        @DisplayName("Should update beat successfully")
        void shouldUpdateBeatSuccessfully() {
            BeatRequest updateRequest = BeatRequest.builder()
                    .titulo("Updated Beat")
                    .precio(15000)
                    .build();
            Beat updatedBeat = Beat.builder()
                    .id(1)
                    .titulo("Updated Beat")
                    .precio(15000)
                    .slug("test-beat")
                    .build();
            BeatResponse updatedResponse = BeatResponse.builder()
                    .idBeat(1)
                    .titulo("Updated Beat")
                    .precio(15000)
                    .slug("test-beat")
                    .build();
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            doNothing().when(beatMapper).updateEntity(any(BeatRequest.class), any(Beat.class));
            when(beatRepository.save(any(Beat.class))).thenReturn(updatedBeat);
            when(beatMapper.toResponse(any(Beat.class))).thenReturn(updatedResponse);
            BeatResponse result = beatService.update(1, updateRequest);
            assertNotNull(result);
            assertEquals("Updated Beat", result.getTitulo());
            verify(beatRepository, times(1)).save(any(Beat.class));
        }
        @Test
        @DisplayName("Should throw exception when updating non-existent beat")
        void shouldThrowExceptionWhenUpdatingNonExistentBeat() {
            when(beatRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> beatService.update(999, beatRequest));
        }
    }
    @Nested
    @DisplayName("Delete Beat Tests")
    class DeleteBeatTests {
        @Test
        @DisplayName("Should delete beat successfully")
        void shouldDeleteBeatSuccessfully() {
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            doNothing().when(beatRepository).delete(beat);
            assertDoesNotThrow(() -> beatService.delete(1));
            verify(beatRepository, times(1)).delete(beat);
        }
        @Test
        @DisplayName("Should throw exception when deleting non-existent beat")
        void shouldThrowExceptionWhenDeletingNonExistentBeat() {
            when(beatRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> beatService.delete(999));
        }
    }
    @Nested
    @DisplayName("Increment Operations Tests")
    class IncrementOperationsTests {
        @Test
        @DisplayName("Should increment play count")
        void shouldIncrementPlayCount() {
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            when(beatRepository.save(any(Beat.class))).thenReturn(beat);
            beatService.incrementPlays(1);
            verify(beatRepository, times(1)).save(argThat(savedBeat -> 
                savedBeat.getReproducciones() == 1
            ));
        }
        @Test
        @DisplayName("Should throw exception when incrementing plays for non-existent beat")
        void shouldThrowExceptionWhenIncrementingPlaysForNonExistentBeat() {
            when(beatRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> beatService.incrementPlays(999));
        }
        @Test
        @DisplayName("Should throw UnsupportedOperationException for incrementLikes")
        void shouldThrowUnsupportedOperationExceptionForIncrementLikes() {
            assertThrows(UnsupportedOperationException.class, () -> beatService.incrementLikes(1));
        }
    }
}
