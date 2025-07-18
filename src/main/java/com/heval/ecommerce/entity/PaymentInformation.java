package com.heval.ecommerce.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;


@Embeddable
public class PaymentInformation {


    @Column(name = "card_holder_name")
    private String cardHolderName;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "cvv")
    private String cvv;
}
