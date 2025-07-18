package com.heval.ecommerce.entity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncryptPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "Heval$Admin2024";
        String encrypted = encoder.encode(rawPassword);
        System.out.println("Encrypted password: " + encrypted);
    }
}
