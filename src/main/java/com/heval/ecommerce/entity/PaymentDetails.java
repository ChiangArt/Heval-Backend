package com.heval.ecommerce.entity;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetails {

    private String paymentMethod;
    private String status;
    private Long paymentId;

    // ID de orden o ref interna enviada a Mercado Pago
    private String externalReference;

    // Correo del pagador
    private String payerEmail;

    // Monto pagado (como string si hay decimales)
    private String transactionAmount;

    // Ej: "credit_card", "ticket", "account_money"
    private String paymentType;

}
