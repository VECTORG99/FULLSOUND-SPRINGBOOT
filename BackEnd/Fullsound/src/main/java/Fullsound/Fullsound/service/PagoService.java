package Fullsound.Fullsound.service;
import Fullsound.Fullsound.dto.request.PagoRequest;
import Fullsound.Fullsound.dto.response.PagoResponse;
public interface PagoService {
    PagoResponse createPaymentIntent(Integer pedidoId);
    PagoResponse processPago(PagoRequest request);
    PagoResponse getById(Integer id);
    PagoResponse confirmPago(String paymentIntentId);
}
