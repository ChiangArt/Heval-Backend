package com.heval.ecommerce.services;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;

public interface MercadoPagoService {

    public Payment getPaymentById(Long paymentId) throws MPApiException, MPException;
}
