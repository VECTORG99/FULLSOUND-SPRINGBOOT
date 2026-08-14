package Fullsound.Fullsound.service;
import Fullsound.Fullsound.dto.request.PedidoRequest;
import Fullsound.Fullsound.dto.response.PedidoItemResponse;
import Fullsound.Fullsound.dto.response.PedidoResponse;
import Fullsound.Fullsound.dto.response.UsuarioResponse;
import Fullsound.Fullsound.exception.BadRequestException;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.mapper.PedidoMapper;
import Fullsound.Fullsound.model.Beat;
import Fullsound.Fullsound.model.Pedido;
import Fullsound.Fullsound.model.PedidoItem;
import Fullsound.Fullsound.model.Rol;
import Fullsound.Fullsound.model.Usuario;
import Fullsound.Fullsound.repository.BeatRepository;
import Fullsound.Fullsound.repository.PedidoRepository;
import Fullsound.Fullsound.repository.UsuarioRepository;
import Fullsound.Fullsound.service.impl.PedidoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private BeatRepository beatRepository;
    @Mock
    private PedidoMapper pedidoMapper;
    @InjectMocks
    private PedidoServiceImpl pedidoService;
    private Usuario usuario;
    private Beat beat;
    private Pedido pedido;
    private PedidoRequest pedidoRequest;
    private PedidoResponse pedidoResponse;
    private Rol rol;
    @BeforeEach
    void setUp() {
        rol = Rol.builder()
                .id(1)
                .tipo("cliente")
                .descripcion("Usuario cliente")
                .build();
        usuario = Usuario.builder()
                .id(1)
                .nombreUsuario("testuser")
                .correo("test@example.com")
                .contraseña("encodedPassword")
                .activo(true)
                .rol(rol)
                .build();
        beat = Beat.builder()
                .id(1)
                .titulo("Test Beat")
                .slug("test-beat")
                .precio(10000)
                .estado("DISPONIBLE")
                .build();
        pedido = Pedido.builder()
                .id(1)
                .numeroPedido("FS-20251201-123456")
                .usuario(usuario)
                .fechaCompra(LocalDateTime.now())
                .total(10000)
                .estado("PENDIENTE")
                .metodoPago("STRIPE")
                .items(new ArrayList<>())
                .build();
        PedidoItem pedidoItem = PedidoItem.builder()
                .id(1)
                .pedido(pedido)
                .beat(beat)
                .nombreItem("Test Beat")
                .cantidad(1)
                .precioUnitario(10000)
                .build();
        pedido.getItems().add(pedidoItem);
        pedidoRequest = PedidoRequest.builder()
                .beatIds(Arrays.asList(1))
                .metodoPago("STRIPE")
                .build();
        UsuarioResponse usuarioResponse = UsuarioResponse.builder()
                .id(1)
                .nombreUsuario("testuser")
                .correo("test@example.com")
                .activo(true)
                .roles(List.of("cliente"))
                .build();
        PedidoItemResponse itemResponse = PedidoItemResponse.builder()
                .id(1)
                .beatId(1)
                .nombreItem("Test Beat")
                .cantidad(1)
                .precioUnitario(10000)
                .subtotal(10000)
                .build();
        pedidoResponse = PedidoResponse.builder()
                .id(1)
                .numeroPedido("FS-20251201-123456")
                .usuario(usuarioResponse)
                .fechaCompra(LocalDateTime.now())
                .total(10000)
                .estado("PENDIENTE")
                .metodoPago("STRIPE")
                .items(Arrays.asList(itemResponse))
                .build();
    }
    @Nested
    @DisplayName("Create Pedido Tests")
    class CreatePedidoTests {
        @Test
        @DisplayName("Should create pedido successfully")
        void shouldCreatePedidoSuccessfully() {
            when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
            when(pedidoMapper.toResponse(any(Pedido.class))).thenReturn(pedidoResponse);
            PedidoResponse result = pedidoService.create(pedidoRequest, 1);
            assertNotNull(result);
            assertEquals("FS-20251201-123456", result.getNumeroPedido());
            assertEquals(10000, result.getTotal());
            assertEquals("PENDIENTE", result.getEstado());
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
        }
        @Test
        @DisplayName("Should throw exception when usuario not found")
        void shouldThrowExceptionWhenUsuarioNotFound() {
            when(usuarioRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> 
                pedidoService.create(pedidoRequest, 999));
        }
        @Test
        @DisplayName("Should throw exception when beat list is empty")
        void shouldThrowExceptionWhenBeatListIsEmpty() {
            PedidoRequest emptyRequest = PedidoRequest.builder()
                    .beatIds(new ArrayList<>())
                    .metodoPago("STRIPE")
                    .build();
            when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
            assertThrows(BadRequestException.class, () -> 
                pedidoService.create(emptyRequest, 1));
        }
        @Test
        @DisplayName("Should throw exception when beat list is null")
        void shouldThrowExceptionWhenBeatListIsNull() {
            PedidoRequest nullRequest = PedidoRequest.builder()
                    .beatIds(null)
                    .metodoPago("STRIPE")
                    .build();
            when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
            assertThrows(BadRequestException.class, () -> 
                pedidoService.create(nullRequest, 1));
        }
        @Test
        @DisplayName("Should throw exception when beat not found")
        void shouldThrowExceptionWhenBeatNotFound() {
            when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
            when(beatRepository.findById(1)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> 
                pedidoService.create(pedidoRequest, 1));
        }
        @Test
        @DisplayName("Should throw exception when beat is not available")
        void shouldThrowExceptionWhenBeatIsNotAvailable() {
            beat.setEstado("VENDIDO");
            when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            assertThrows(BadRequestException.class, () -> 
                pedidoService.create(pedidoRequest, 1));
        }
        @Test
        @DisplayName("Should create pedido with multiple beats")
        void shouldCreatePedidoWithMultipleBeats() {
            Beat beat2 = Beat.builder()
                    .id(2)
                    .titulo("Beat 2")
                    .precio(15000)
                    .estado("DISPONIBLE")
                    .build();
            PedidoRequest multiRequest = PedidoRequest.builder()
                    .beatIds(Arrays.asList(1, 2))
                    .metodoPago("STRIPE")
                    .build();
            Pedido multiPedido = Pedido.builder()
                    .id(1)
                    .numeroPedido("FS-20251201-123456")
                    .usuario(usuario)
                    .total(25000)
                    .estado("PENDIENTE")
                    .items(new ArrayList<>())
                    .build();
            PedidoResponse multiResponse = PedidoResponse.builder()
                    .id(1)
                    .numeroPedido("FS-20251201-123456")
                    .total(25000)
                    .estado("PENDIENTE")
                    .items(Arrays.asList(
                            PedidoItemResponse.builder().beatId(1).nombreItem("Test Beat").precioUnitario(10000).build(),
                            PedidoItemResponse.builder().beatId(2).nombreItem("Beat 2").precioUnitario(15000).build()
                    ))
                    .build();
            when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
            when(beatRepository.findById(1)).thenReturn(Optional.of(beat));
            when(beatRepository.findById(2)).thenReturn(Optional.of(beat2));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(multiPedido);
            when(pedidoMapper.toResponse(any(Pedido.class))).thenReturn(multiResponse);
            PedidoResponse result = pedidoService.create(multiRequest, 1);
            assertNotNull(result);
            assertEquals(25000, result.getTotal());
            assertEquals(2, result.getItems().size());
        }
    }
    @Nested
    @DisplayName("Read Pedido Tests")
    class ReadPedidoTests {
        @Test
        @DisplayName("Should get pedido by ID successfully")
        void shouldGetPedidoByIdSuccessfully() {
            when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
            when(pedidoMapper.toResponse(pedido)).thenReturn(pedidoResponse);
            PedidoResponse result = pedidoService.getById(1);
            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals("FS-20251201-123456", result.getNumeroPedido());
        }
        @Test
        @DisplayName("Should throw exception when pedido not found by ID")
        void shouldThrowExceptionWhenPedidoNotFoundById() {
            when(pedidoRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> pedidoService.getById(999));
        }
        @Test
        @DisplayName("Should get pedido by numero pedido successfully")
        void shouldGetPedidoByNumeroPedidoSuccessfully() {
            when(pedidoRepository.findByNumeroPedido("FS-20251201-123456"))
                    .thenReturn(Optional.of(pedido));
            when(pedidoMapper.toResponse(pedido)).thenReturn(pedidoResponse);
            PedidoResponse result = pedidoService.getByNumeroPedido("FS-20251201-123456");
            assertNotNull(result);
            assertEquals("FS-20251201-123456", result.getNumeroPedido());
        }
        @Test
        @DisplayName("Should throw exception when pedido not found by numero pedido")
        void shouldThrowExceptionWhenPedidoNotFoundByNumeroPedido() {
            when(pedidoRepository.findByNumeroPedido("INVALID")).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> 
                pedidoService.getByNumeroPedido("INVALID"));
        }
        @Test
        @DisplayName("Should get all pedidos by usuario")
        void shouldGetAllPedidosByUsuario() {
            Pedido pedido2 = Pedido.builder()
                    .id(2)
                    .numeroPedido("FS-20251201-654321")
                    .usuario(usuario)
                    .total(5000)
                    .estado("COMPLETADO")
                    .items(new ArrayList<>())
                    .build();
            PedidoResponse response2 = PedidoResponse.builder()
                    .id(2)
                    .numeroPedido("FS-20251201-654321")
                    .total(5000)
                    .estado("COMPLETADO")
                    .build();
            when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
            when(pedidoRepository.findByUsuarioOrderByFechaCompraDesc(usuario))
                    .thenReturn(Arrays.asList(pedido, pedido2));
            when(pedidoMapper.toResponse(pedido)).thenReturn(pedidoResponse);
            when(pedidoMapper.toResponse(pedido2)).thenReturn(response2);
            List<PedidoResponse> result = pedidoService.getByUsuario(1);
            assertNotNull(result);
            assertEquals(2, result.size());
        }
        @Test
        @DisplayName("Should throw exception when usuario not found for get pedidos")
        void shouldThrowExceptionWhenUsuarioNotFoundForGetPedidos() {
            when(usuarioRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> pedidoService.getByUsuario(999));
        }
        @Test
        @DisplayName("Should get all pedidos")
        void shouldGetAllPedidos() {
            when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedido));
            when(pedidoMapper.toResponse(pedido)).thenReturn(pedidoResponse);
            List<PedidoResponse> result = pedidoService.getAll();
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }
    @Nested
    @DisplayName("Update Pedido Estado Tests")
    class UpdatePedidoEstadoTests {
        @Test
        @DisplayName("Should update pedido estado successfully")
        void shouldUpdatePedidoEstadoSuccessfully() {
            when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
            PedidoResponse updatedResponse = PedidoResponse.builder()
                    .id(1)
                    .numeroPedido("FS-20251201-123456")
                    .estado("PROCESANDO")
                    .build();
            when(pedidoMapper.toResponse(any(Pedido.class))).thenReturn(updatedResponse);
            PedidoResponse result = pedidoService.updateEstado(1, "PROCESANDO");
            assertNotNull(result);
            assertEquals("PROCESANDO", result.getEstado());
        }
        @Test
        @DisplayName("Should throw exception when updating non-existent pedido")
        void shouldThrowExceptionWhenUpdatingNonExistentPedido() {
            when(pedidoRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> 
                pedidoService.updateEstado(999, "COMPLETADO"));
        }
        @Test
        @DisplayName("Should mark beats as sold when pedido is completed")
        void shouldMarkBeatsAsSoldWhenPedidoIsCompleted() {
            when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
            when(beatRepository.save(any(Beat.class))).thenReturn(beat);
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
            PedidoResponse completedResponse = PedidoResponse.builder()
                    .id(1)
                    .estado("COMPLETADO")
                    .build();
            when(pedidoMapper.toResponse(any(Pedido.class))).thenReturn(completedResponse);
            PedidoResponse result = pedidoService.updateEstado(1, "COMPLETADO");
            assertNotNull(result);
            verify(beatRepository, atLeast(1)).save(argThat(savedBeat -> 
                "VENDIDO".equals(savedBeat.getEstado())
            ));
        }
        @Test
        @DisplayName("Should release beats when pedido is cancelled")
        void shouldReleaseBeatsWhenPedidoIsCancelled() {
            beat.setEstado("VENDIDO");
            when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
            when(beatRepository.save(any(Beat.class))).thenReturn(beat);
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
            PedidoResponse cancelledResponse = PedidoResponse.builder()
                    .id(1)
                    .estado("CANCELADO")
                    .build();
            when(pedidoMapper.toResponse(any(Pedido.class))).thenReturn(cancelledResponse);
            PedidoResponse result = pedidoService.updateEstado(1, "CANCELADO");
            assertNotNull(result);
            verify(beatRepository, atLeast(1)).save(argThat(savedBeat -> 
                "DISPONIBLE".equals(savedBeat.getEstado())
            ));
        }
        @Test
        @DisplayName("Should release beats when pedido is refunded")
        void shouldReleaseBeatsWhenPedidoIsRefunded() {
            beat.setEstado("RESERVADO");
            when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
            when(beatRepository.save(any(Beat.class))).thenReturn(beat);
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
            PedidoResponse refundedResponse = PedidoResponse.builder()
                    .id(1)
                    .estado("REEMBOLSADO")
                    .build();
            when(pedidoMapper.toResponse(any(Pedido.class))).thenReturn(refundedResponse);
            PedidoResponse result = pedidoService.updateEstado(1, "REEMBOLSADO");
            assertNotNull(result);
            verify(beatRepository, atLeast(1)).save(argThat(savedBeat -> 
                "DISPONIBLE".equals(savedBeat.getEstado())
            ));
        }
    }
}
