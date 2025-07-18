package com.heval.ecommerce.controller;
import com.heval.ecommerce.entity.Order;
import com.heval.ecommerce.entity.Product;
import com.heval.ecommerce.services.JwtService;
import com.heval.ecommerce.services.OrderService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mercado-pago")
public class MercadoPagoController {

    private final OrderService orderService;
    private final JwtService jwtService;

    @PostMapping("/preference/{orderId}")
    public ResponseEntity<?> createPreference(
            @RequestHeader("Authorization") String token,
            @PathVariable String orderId
    ) {
        try {
            // 1. Extraer ID del usuario desde el token
            Long userId = jwtService.extractUserId(token);

            // 2. Obtener la orden del usuario
            Order order = orderService.findOrderByOrderId(orderId);

            // 3. Verificar que la orden le pertenezca al usuario
            if (!order.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("No autorizado para esta orden.");
            }

            // 4. Crear los items de la orden
            List<PreferenceItemRequest> items = order.getOrderItems().stream()
                    .map(item -> {
                        Product product = item.getProduct();
                        String title = product.getTitle();
                        if (title == null || title.trim().isEmpty()) {
                            title = "Producto sin nombre";
                        }

                        String imageUrl = (product.getImageUrls() != null && !product.getImageUrls().isEmpty())
                                ? product.getImageUrls().get(0)
                                : "https://via.placeholder.com/150";

                        return PreferenceItemRequest.builder()
                                .id(String.valueOf(product.getId()))
                                .title(title)
                                .quantity(item.getQuantity())
                                .unitPrice(item.getDiscountPrice())
                                .pictureUrl(imageUrl)
                                .currencyId("PEN")
                                .build();
                    })
                    .toList();


            // 5. URLs de redirección
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("https://wedding-landscapes-causing-clarity.trycloudflare.com/shop/checkout/success")
                    .pending("https://wedding-landscapes-causing-clarity.trycloudflare.com/shop/checkout/pending")
                    .failure("https://wedding-landscapes-causing-clarity.trycloudflare.com/shop/checkout/failure")
                    .build();


            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .externalReference(order.getOrderId())
                    .notificationUrl("https://strategic-seas-bobby-rankings.trycloudflare.com/api/v1/payments/webhook")
                    .build();

            MercadoPagoConfig.setAccessToken("APP_USR-7950396496541940-062611-8363f05fe12f8b134e5202788d2c3d7a-2512611759");

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return ResponseEntity.ok(preference.getInitPoint());

        } catch (MPApiException e) {
            System.err.println("MPApiException: " + e.getApiResponse().getContent());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error de MercadoPago: " + e.getApiResponse().getContent());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }

}
