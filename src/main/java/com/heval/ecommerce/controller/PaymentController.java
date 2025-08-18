package com.heval.ecommerce.controller;
import com.heval.ecommerce.dto.enumeration.OrderStatus;
import com.heval.ecommerce.entity.Order;
import com.heval.ecommerce.entity.PaymentDetails;
import com.heval.ecommerce.services.MercadoPagoService;
import com.heval.ecommerce.services.OrderService;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;



@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;
    private final MercadoPagoService mercadoPagoService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handlePaymentWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("🔔 Webhook recibido: " + payload);

        try {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");

            if (data == null || data.get("id") == null) {
                return ResponseEntity.badRequest().body("ID de pago no proporcionado");
            }

            Long paymentId = Long.valueOf(data.get("id").toString());
            System.out.println("🧾 ID de pago recibido: " + paymentId);

            // Obtener pago desde MercadoPago
            Payment payment = mercadoPagoService.getPaymentById(paymentId);
            if (payment == null) {
                return ResponseEntity.status(404).body("Pago no encontrado");
            }

            String externalReference = payment.getExternalReference();
            String status = payment.getStatus();

            System.out.println(" External Reference: " + externalReference);
            System.out.println(" Estado del pago: " + status);

            // Buscar orden y actualizar datos
            Order order = orderService.findOrderByOrderId(externalReference);

            // Llenar detalles de pago embebidos
            PaymentDetails paymentDetails = PaymentDetails.builder()
                    .externalReference(payment.getExternalReference())
                    .paymentId(payment.getId())
                    .paymentMethod(payment.getPaymentMethodId())
                    .paymentType(payment.getPaymentTypeId())
                    .status(payment.getStatus())
                    .build();

            order.setPaymentDetails(paymentDetails);

            // Establecer estado de orden según estado de pago
            switch (status) {
                case "approved", "accredited" -> {
                    order.setOrderStatus(OrderStatus.PAID);
                    orderService.reduceStockAfterPayment(order);
                }
                case "pending", "in_process" -> order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
                case "rejected", "cancelled" -> order.setOrderStatus(OrderStatus.FAILED);
                case "refunded" -> order.setOrderStatus(OrderStatus.REFUNDED);
                default -> {
                    System.out.println("Estado no manejado: " + status);
                    order.setOrderStatus(OrderStatus.FAILED);
                }
            }

            orderService.saveOrder(order);


            return ResponseEntity.ok(" Webhook procesado con éxito");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(" Error procesando webhook: " + e.getMessage());
        }
    }
}
