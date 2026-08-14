package Fullsound.Fullsound.service;
import Fullsound.Fullsound.dto.request.PagoRequest;
import Fullsound.Fullsound.dto.response.PagoResponse;
import Fullsound.Fullsound.exception.BadRequestException;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.mapper.PagoMapper;
import Fullsound.Fullsound.model.Pago;
import Fullsound.Fullsound.model.Pedido;
import Fullsound.Fullsound.model.Usuario;
import Fullsound.Fullsound.model.Rol;
import Fullsound.Fullsound.repository.PagoRepository;
import Fullsound.Fullsound.repository.PedidoRepository;
import Fullsound.Fullsound.service.impl.PagoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class PagoServiceTest {
    @Mock
    private PagoRepository pagoRepository;
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private PagoMapper pagoMapper;
    @InjectMocks
    private PagoServiceImpl pagoService;
    private Pedido pedido;
    private Pago pago;
    private PagoRequest pagoRequest;
    private PagoResponse pagoResponse;
    private Usuario usuario;
    private Rol rol;
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pagoService, "stripeApiKey", "sk_test_fake_key");
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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
        pago = Pago.builder()
                .id(1)
                .pedido(pedido)
                .stripePaymentIntentId("pi_test123")
                .stripeChargeId("ch_test123")
                .monto(10000)
                .moneda("CLP")
                .estado("PENDIENTE")
                .createdAt(LocalDateTime.now())
                .build();
        pagoRequest = PagoRequest.builder()
                .pedidoId(1)
                .paymentMethodId("pm_test123")
                .build();
        pagoResponse = PagoResponse.builder()
                .id(1)
                .pedidoId(1)
                .monto(10000)
                .moneda("CLP")
                .estado("PENDIENTE")
                .stripePaymentIntentId("pi_test123")
                .build();
    }
    @Nested
    @DisplayName("Create Payment Intent Tests")
    class CreatePaymentIntentTests {
        @Test
        @DisplayName("Should throw exception when pedido not found")
        void shouldThrowExceptionWhenPedidoNotFound() {
            when(pedidoRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> 
                pagoService.createPaymentIntent(999));
        }
        @Test
        @DisplayName("Should throw exception when pedido already has successful payment")
        void shouldThrowExceptionWhenPedidoAlreadyHasPayment() {
            Pago existingPago = Pago.builder()
                    .id(1)
                    .pedido(pedido)
                    .estado("COMPLETADO")
                    .build();
            when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
            when(pagoRepository.findByPedido(pedido)).thenReturn(List.of(existingPago));
            assertThrows(BadRequestException.class, () -> 
                pagoService.createPaymentIntent(1));
        }
    }
    @Nested
    @DisplayName("Process Pago Tests")
    class ProcessPagoTests {
        @Test
        @DisplayName("Should process pago successfully")
        void shouldProcessPagoSuccessfully() {
            when(pagoRepository.findById(1)).thenReturn(Optional.of(pago));
            when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
            when(pagoMapper.toResponse(any(Pago.class))).thenReturn(pagoResponse);
            PagoResponse response = pagoService.processPago(pagoRequest);
            assertNotNull(response);
            verify(pagoRepository, times(1)).save(any(Pago.class));
        }
        @Test
        @DisplayName("Should throw exception when pago not found")
        void shouldThrowExceptionWhenPagoNotFound() {
            when(pagoRepository.findById(999)).thenReturn(Optional.empty());
            PagoRequest invalidRequest = PagoRequest.builder()
                    .pedidoId(999)
                    .paymentMethodId("pm_test123")
                    .build();
            assertThrows(ResourceNotFoundException.class, () -> 
                pagoService.processPago(invalidRequest));
        }
    }
    @Nested
    @DisplayName("Get Pago Tests")
    class GetPagoTests {
        @Test
        @DisplayName("Should get pago by ID successfully")
        void shouldGetPagoByIdSuccessfully() {
            when(pagoRepository.findById(1)).thenReturn(Optional.of(pago));
            when(pagoMapper.toResponse(pago)).thenReturn(pagoResponse);
            PagoResponse response = pagoService.getById(1);
            assertNotNull(response);
            assertEquals(1, response.getId());
            assertEquals("PENDIENTE", response.getEstado());
            verify(pagoRepository, times(1)).findById(1);
        }
        @Test
        @DisplayName("Should throw exception when pago not found by ID")
        void shouldThrowExceptionWhenPagoNotFoundById() {
            when(pagoRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> 
                pagoService.getById(999));
        }
    }
    @Nested
    @DisplayName("Confirm Pago Tests")
    class ConfirmPagoTests {
        @Test
        @DisplayName("Should throw exception when payment intent not found")
        void shouldThrowExceptionWhenPaymentIntentNotFound() {
            when(pagoRepository.findByStripePaymentIntentId("pi_invalid"))
                    .thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> 
                pagoService.confirmPago("pi_invalid"));
        }
    }
}
