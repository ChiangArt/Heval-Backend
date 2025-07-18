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

    private String externalReference;   // ID de orden o ref interna enviada a Mercado Pago
    private String payerEmail;          // Correo del pagador
    private String transactionAmount;   // Monto pagado (como string si hay decimales)
    private String paymentType;         // Ej: "credit_card", "ticket", "account_money"

}
