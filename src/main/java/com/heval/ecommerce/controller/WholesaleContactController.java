package com.heval.ecommerce.controller;

import com.heval.ecommerce.dto.request.WholesaleContactRequest;
import com.heval.ecommerce.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wholesale-contact")
@RequiredArgsConstructor
public class WholesaleContactController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<String> handleContactForm(@RequestBody WholesaleContactRequest request) {
        emailService.sendWholesaleContactMessage(request.name(), request.email(), request.message());
        return ResponseEntity.ok("Mensaje enviado con éxito");
    }
}
