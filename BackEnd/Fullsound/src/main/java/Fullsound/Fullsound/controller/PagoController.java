package Fullsound.Fullsound.controller;
import Fullsound.Fullsound.dto.request.PagoRequest;
import Fullsound.Fullsound.dto.response.PagoResponse;
import Fullsound.Fullsound.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@Tag(name = "💳 Pagos", description = "Gestión de pagos e integración con Stripe")
public class PagoController {
    private final PagoService pagoService;
    @Operation(
        summary = "Crear Payment Intent",
        description = "Crea un Payment Intent en Stripe para el pedido especificado. Requiere autenticación.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Payment Intent creado", content = @Content(schema = @Schema(implementation = PagoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Pedido ya pagado o error de Stripe", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    @PostMapping("/create-intent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagoResponse> createPaymentIntent(@Valid @RequestBody PagoRequest request) {
        PagoResponse response = pagoService.createPaymentIntent(request.getPedidoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Operation(
        summary = "Procesar pago",
        description = "Procesa un pago con el método de pago de Stripe especificado.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago procesado", content = @Content(schema = @Schema(implementation = PagoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    @PostMapping("/{pagoId}/process")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagoResponse> processPago(
            @Parameter(description = "ID del pago", required = true, example = "1")
            @PathVariable Integer pagoId,
            @Valid @RequestBody PagoRequest request) {
        PagoResponse response = pagoService.processPago(request);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Obtener pago por ID",
        description = "Devuelve la información de un pago específico.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago encontrado", content = @Content(schema = @Schema(implementation = PagoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagoResponse> getById(
            @Parameter(description = "ID del pago", required = true, example = "1")
            @PathVariable Integer id) {
        PagoResponse response = pagoService.getById(id);
        return ResponseEntity.ok(response);
    }
    @Operation(
        summary = "Confirmar pago",
        description = "Confirma el estado de un pago consultando Stripe por el Payment Intent ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago confirmado", content = @Content(schema = @Schema(implementation = PagoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    @PostMapping("/confirm")
    public ResponseEntity<PagoResponse> confirmPago(
            @Parameter(description = "Payment Intent ID de Stripe", required = true, example = "pi_1234567890")
            @RequestParam String paymentIntentId) {
        PagoResponse response = pagoService.confirmPago(paymentIntentId);
        return ResponseEntity.ok(response);
    }
}
