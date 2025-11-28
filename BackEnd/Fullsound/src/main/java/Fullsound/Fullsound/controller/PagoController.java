package Fullsound.Fullsound.controller;

import Fullsound.Fullsound.dto.request.PagoRequest;
import Fullsound.Fullsound.dto.response.PagoResponse;
import Fullsound.Fullsound.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de pagos con Stripe.
 */
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:4200", "http://localhost:8080"})
public class PagoController {

    private final PagoService pagoService;

    /**
     * Crea un Payment Intent en Stripe.
     *
     * @param request datos del pago
     * @return información del pago con clientSecret
     */
    @PostMapping("/create-intent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagoResponse> createPaymentIntent(@Valid @RequestBody PagoRequest request) {
        PagoResponse response = pagoService.createPaymentIntent(request.getPedidoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Procesa un pago (actualiza información del cargo).
     *
     * @param pagoId ID del pago
     * @param stripeChargeId ID del cargo de Stripe
     * @return pago actualizado
     */
    @PostMapping("/{pagoId}/process")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagoResponse> processPago(
            @PathVariable Integer pagoId,
            @RequestParam String stripeChargeId) {
        PagoRequest request = new PagoRequest();
        request.setPedidoId(pagoId);
        request.setPaymentMethodId(stripeChargeId);
        PagoResponse response = pagoService.processPago(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un pago por su ID.
     *
     * @param id ID del pago
     * @return pago encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagoResponse> getById(@PathVariable Integer id) {
        PagoResponse response = pagoService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Confirma un pago mediante su Payment Intent ID.
     * Endpoint usado típicamente por webhooks de Stripe.
     *
     * @param paymentIntentId ID del Payment Intent de Stripe
     * @return pago confirmado
     */
    @PostMapping("/confirm")
    public ResponseEntity<PagoResponse> confirmPago(@RequestParam String paymentIntentId) {
        PagoResponse response = pagoService.confirmPago(paymentIntentId);
        return ResponseEntity.ok(response);
    }
}
