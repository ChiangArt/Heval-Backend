package com.heval.ecommerce.services;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class EncryptPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("AdminHeval@2025");
        System.out.println(encodedPassword);
    }
}
