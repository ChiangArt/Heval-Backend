package com.heval.ecommerce.services;

public interface EmailService {

    String sendVerificationCode(String email);

    boolean verifyCode(String email, String code);

    void sendPasswordRecoveryEmail(String email, String token);

    void sendWholesaleContactMessage(String name, String email, String message);

}
