package Fullsound.Fullsound.service.impl;
import Fullsound.Fullsound.dto.request.PagoRequest;
import Fullsound.Fullsound.dto.response.PagoResponse;
import Fullsound.Fullsound.exception.BadRequestException;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.mapper.PagoMapper;
import Fullsound.Fullsound.model.Pago;
import Fullsound.Fullsound.model.Pedido;
import Fullsound.Fullsound.repository.PagoRepository;
import Fullsound.Fullsound.repository.PedidoRepository;
import Fullsound.Fullsound.service.PagoService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {
    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final PagoMapper pagoMapper;
    @Value("${stripe.api.key}")
    private String stripeApiKey;
    @Override
    @Transactional
    public PagoResponse createPaymentIntent(Integer pedidoId) {
        Stripe.apiKey = stripeApiKey;
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", pedidoId.toString()));
        if (pagoRepository.findByPedido(pedido).stream()
                .anyMatch(p -> "COMPLETADO".equals(p.getEstado()))) {
            throw new BadRequestException("El pedido ya tiene un pago exitoso");
        }
        try {
            long amount = pedido.getTotal().longValue();
            Map<String, String> metadata = new HashMap<>();
            metadata.put("pedido_id", pedido.getId().toString());
            metadata.put("numero_pedido", pedido.getNumeroPedido());
            metadata.put("usuario_id", pedido.getUsuario().getId().toString());
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency("clp")
                    .setDescription("Compra de beats - Pedido: " + pedido.getNumeroPedido())
                    .putAllMetadata(metadata)
                    .setConfirm(false)  
                    .build();
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            Pago pago = new Pago();
            pago.setPedido(pedido);
            pago.setStripePaymentIntentId(paymentIntent.getId());
            pago.setEstado("PENDIENTE");
            pago.setMonto(pedido.getTotal());
            pago.setMoneda("USD");
            pago.setCreatedAt(LocalDateTime.now());
            Pago pagoGuardado = pagoRepository.save(pago);
            pedido.setEstado("PROCESANDO");
            pedidoRepository.save(pedido);
            return pagoMapper.toResponse(pagoGuardado);
        } catch (StripeException e) {
            throw new BadRequestException("Error al crear Payment Intent: " + e.getMessage());
        }
    }
    @Override
    @Transactional
    public PagoResponse processPago(PagoRequest request) {
        Integer pagoId = request.getPedidoId();
        String stripeChargeId = request.getPaymentMethodId();
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", "id", pagoId.toString()));
        pago.setStripeChargeId(stripeChargeId);
        pago.setEstado("PROCESANDO");
        pago.setProcessedAt(LocalDateTime.now());
        Pago pagoActualizado = pagoRepository.save(pago);
        return pagoMapper.toResponse(pagoActualizado);
    }
    @Override
    @Transactional(readOnly = true)
    public PagoResponse getById(Integer id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", "id", id.toString()));
        return pagoMapper.toResponse(pago);
    }
    @Override
    @Transactional
    public PagoResponse confirmPago(String stripePaymentIntentId) {
        Pago pago = pagoRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", "stripePaymentIntentId", stripePaymentIntentId));
        try {
            Stripe.apiKey = stripeApiKey;
            PaymentIntent paymentIntent = PaymentIntent.retrieve(stripePaymentIntentId);
            if ("succeeded".equals(paymentIntent.getStatus())) {
                pago.setEstado("COMPLETADO");
                pago.setStripeChargeId(paymentIntent.getLatestCharge());
                pago.setProcessedAt(LocalDateTime.now());
                Pedido pedido = pago.getPedido();
                pedido.setEstado("COMPLETADO");
                pedidoRepository.save(pedido);
            } else if ("canceled".equals(paymentIntent.getStatus())) {
                pago.setEstado("FALLIDO");
                Pedido pedido = pago.getPedido();
                pedido.setEstado("CANCELADO");
                pedidoRepository.save(pedido);
            } else {
                pago.setEstado("PROCESANDO");
            }
            Pago pagoActualizado = pagoRepository.save(pago);
            return pagoMapper.toResponse(pagoActualizado);
        } catch (StripeException e) {
            throw new BadRequestException("Error al confirmar pago: " + e.getMessage());
        }
    }
}
