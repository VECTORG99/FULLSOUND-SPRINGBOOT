package Fullsound.Fullsound.service.impl;

import Fullsound.Fullsound.dto.request.PagoRequest;
import Fullsound.Fullsound.dto.response.PagoResponse;
import Fullsound.Fullsound.enums.EstadoPago;
import Fullsound.Fullsound.enums.EstadoPedido;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del servicio de pagos con integración Stripe.
 */
@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final PagoMapper pagoMapper;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Override
    @Transactional
    public PagoResponse createPaymentIntent(PagoRequest request) {
        // Configurar Stripe API key
        Stripe.apiKey = stripeApiKey;

        // Buscar pedido
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", request.getPedidoId().toString()));

        // Validar que el pedido no tenga un pago exitoso previo
        if (pagoRepository.findByPedido(pedido).stream()
                .anyMatch(p -> p.getEstado() == EstadoPago.EXITOSO)) {
            throw new BadRequestException("El pedido ya tiene un pago exitoso");
        }

        try {
            // Convertir total a centavos (Stripe trabaja en centavos)
            long amountInCents = pedido.getTotal().multiply(new BigDecimal("100")).longValue();

            // Crear metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("pedido_id", pedido.getId().toString());
            metadata.put("numero_pedido", pedido.getNumeroPedido());
            metadata.put("usuario_id", pedido.getUsuario().getId().toString());

            // Crear Payment Intent en Stripe
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("usd")
                    .setDescription("Compra de beats - Pedido: " + pedido.getNumeroPedido())
                    .putAllMetadata(metadata)
                    .setPaymentMethod(request.getPaymentMethodId())
                    .setConfirm(false) // No confirmar automáticamente
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Crear entidad Pago
            Pago pago = new Pago();
            pago.setPedido(pedido);
            pago.setStripePaymentIntentId(paymentIntent.getId());
            pago.setEstado(EstadoPago.PENDIENTE);
            pago.setMonto(pedido.getTotal());
            pago.setMoneda("USD");
            pago.setClientSecret(paymentIntent.getClientSecret());
            pago.setCreatedAt(LocalDateTime.now());

            // Guardar pago
            Pago pagoGuardado = pagoRepository.save(pago);

            // Actualizar estado del pedido
            pedido.setEstado(EstadoPedido.PROCESANDO);
            pedidoRepository.save(pedido);

            return pagoMapper.toResponse(pagoGuardado);

        } catch (StripeException e) {
            throw new BadRequestException("Error al crear Payment Intent: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PagoResponse processPago(Integer pagoId, String stripeChargeId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", "id", pagoId.toString()));

        // Actualizar información del pago
        pago.setStripeChargeId(stripeChargeId);
        pago.setEstado(EstadoPago.PROCESANDO);
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
            // Configurar Stripe API key
            Stripe.apiKey = stripeApiKey;

            // Recuperar Payment Intent de Stripe
            PaymentIntent paymentIntent = PaymentIntent.retrieve(stripePaymentIntentId);

            // Verificar estado
            if ("succeeded".equals(paymentIntent.getStatus())) {
                pago.setEstado(EstadoPago.EXITOSO);
                pago.setStripeChargeId(paymentIntent.getLatestCharge());
                pago.setProcessedAt(LocalDateTime.now());

                // Actualizar pedido a COMPLETADO
                Pedido pedido = pago.getPedido();
                pedido.setEstado(EstadoPedido.COMPLETADO);
                pedidoRepository.save(pedido);

            } else if ("canceled".equals(paymentIntent.getStatus())) {
                pago.setEstado(EstadoPago.FALLIDO);

                // Actualizar pedido a CANCELADO
                Pedido pedido = pago.getPedido();
                pedido.setEstado(EstadoPedido.CANCELADO);
                pedidoRepository.save(pedido);

            } else {
                pago.setEstado(EstadoPago.PROCESANDO);
            }

            Pago pagoActualizado = pagoRepository.save(pago);
            return pagoMapper.toResponse(pagoActualizado);

        } catch (StripeException e) {
            throw new BadRequestException("Error al confirmar pago: " + e.getMessage());
        }
    }
}
