package com.heval.ecommerce.services.impl;

import com.heval.ecommerce.services.MercadoPagoService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Service;

@Service
public class MercadoPagoServiceImpl implements MercadoPagoService {

    @Override
    public Payment getPaymentById(Long paymentId) throws MPApiException, MPException {
        MercadoPagoConfig.setAccessToken("APP_USR-7950396496541940-062611-8363f05fe12f8b134e5202788d2c3d7a-2512611759");

        PaymentClient paymentClient = new PaymentClient();

        try {
            return paymentClient.get(paymentId);
        } catch (MPApiException e) {
            System.out.println("❌ Error de API Mercado Pago: " + e.getApiResponse().getContent());
            throw e;
        }
    }
}
