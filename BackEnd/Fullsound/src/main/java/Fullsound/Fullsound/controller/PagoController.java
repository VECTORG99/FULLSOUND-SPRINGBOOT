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
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:4200", "http://localhost:8080"})
public class PagoController {
    private final PagoService pagoService;
    @PostMapping("/create-intent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagoResponse> createPaymentIntent(@Valid @RequestBody PagoRequest request) {
        PagoResponse response = pagoService.createPaymentIntent(request.getPedidoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
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
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagoResponse> getById(@PathVariable Integer id) {
        PagoResponse response = pagoService.getById(id);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/confirm")
    public ResponseEntity<PagoResponse> confirmPago(@RequestParam String paymentIntentId) {
        PagoResponse response = pagoService.confirmPago(paymentIntentId);
        return ResponseEntity.ok(response);
    }
}
