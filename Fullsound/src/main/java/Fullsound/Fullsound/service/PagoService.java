package Fullsound.Fullsound.service;

import Fullsound.Fullsound.dto.request.PagoRequest;
import Fullsound.Fullsound.dto.response.PagoResponse;

/**
 * Servicio de procesamiento de pagos con Stripe.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
public interface PagoService {
    
    /**
     * Crea un Payment Intent en Stripe.
     */
    PagoResponse createPaymentIntent(Integer pedidoId);
    
    /**
     * Procesa un pago con Stripe.
     */
    PagoResponse processPago(PagoRequest request);
    
    /**
     * Obtiene un pago por ID.
     */
    PagoResponse getById(Integer id);
    
    /**
     * Confirma un pago exitoso.
     */
    PagoResponse confirmPago(String paymentIntentId);
}
