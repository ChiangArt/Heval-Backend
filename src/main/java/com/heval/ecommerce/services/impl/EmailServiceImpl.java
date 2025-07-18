package com.heval.ecommerce.services.impl;

import com.heval.ecommerce.entity.EmailVerification;
import com.heval.ecommerce.exception.ApiValidateException;
import com.heval.ecommerce.repository.EmailVerificationRepository;
import com.heval.ecommerce.services.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository repository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Random random = new Random();

    @Override
    public String sendVerificationCode(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ApiValidateException("El correo no cumple con el formato adecuado.");
        }

        boolean resent = repository.findByEmail(email) != null;
        String code = generateOrUpdateCode(email);

        String htmlContent = buildVerificationEmail(
                "Verifica tu correo",
                "¡Hola! Gracias por utilizar nuestros servicios en Heval.",
                "Para continuar con tu registro, necesitamos comprobar que esta dirección de correo es tuya.",
                code,
                "Este código caducará en 5 minutos. Si no solicitaste este código, puedes ignorar este mensaje."
        );

        sendEmail(email, "Verificación de Correo - Heval Group", htmlContent);

        return resent
                ? "El código se volvió a enviar al correo " + email + ", se vencerá en 5 minutos."
                : "Se envió el código de verificación al correo " + email + ", se vencerá en 5 minutos.";
    }

    @Override
    public void sendPasswordRecoveryEmail(String email, String token) {
        String link = "http://localhost:3000/auth/login/reset-password?token=" + token;

        String htmlContent = buildLinkEmail(
                "Recuperación de contraseña",
                "Hemos recibido una solicitud para restablecer tu contraseña.",
                "Si fuiste tú, haz clic en el siguiente botón para continuar:",
                "<a href=\"" + link + "\" class=\"button\">Restablecer contraseña</a>",
                "Este enlace expirará en 15 minutos. Si no solicitaste este cambio, puedes ignorar este correo."
        );

        sendEmail(email, "Recupera tu contraseña - Heval Group", htmlContent);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ApiValidateException("El correo no cumple con el formato adecuado.");
        }

        EmailVerification existing = repository.findByEmail(email);
        if (existing == null) {
            throw new ApiValidateException("No se encontró un código para este correo.");
        }

        long secondsElapsed = Duration.between(existing.getCreatedAt(), LocalDateTime.now()).getSeconds();
        if (secondsElapsed > 300) {
            throw new ApiValidateException("El código ha expirado.");
        }

        if (!existing.getCode().equals(code)) {
            throw new ApiValidateException("El código ingresado no es válido.");
        }

        return true;
    }

    @Override
    public void sendWholesaleContactMessage(String name, String email, String message) {
        String htmlContent = """
        <html>
            <body>
                <h2>Nuevo mensaje de contacto al por mayor</h2>
                <p><strong>Nombre:</strong> %s</p>
                <p><strong>Email:</strong> %s</p>
                <p><strong>Mensaje:</strong></p>
                <p>%s</p>
            </body>
        </html>
        """.formatted(name, email, message);

        // Cambia esto si quieres otro correo de destino
        String to = "hevalgroup@gmail.com";

        sendEmail(to, "Contacto al por mayor - HevalShop", htmlContent);
    }


    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Verificado en Amazon SES
            String FROM = "heval.group.contact@gmail.com";
            helper.setFrom(FROM);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el correo: " + e.getMessage(), e);
        }
    }

    private String buildVerificationEmail(String title, String intro, String instruction, String code, String footerNote) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>%s</title>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; color: #333; margin: 0; padding: 0; }
                        .container { max-width: 600px; margin: 40px auto; background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); text-align: center; }
                        h1 { color: #002f6c; }
                        .code { font-size: 24px; font-weight: bold; padding: 12px 20px; border: 1px solid #002f6c; background: #f9f9f9; border-radius: 5px; margin: 20px 0; display: inline-block; }
                        .footer { font-size: 12px; color: #999; margin-top: 30px; border-top: 1px solid #eee; padding-top: 10px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>%s</h1>
                        <p>%s</p>
                        <p>%s</p>
                        <div class="code">%s</div>
                        <p>%s</p>
                        <div class="footer">
                            <p>&copy; 2025 Heval Group. Todos los derechos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(title, title, intro, instruction, code, footerNote);
    }

    private String buildLinkEmail(String title, String intro, String instruction, String buttonHtml, String footerNote) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>%s</title>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; color: #333; margin: 0; padding: 0; }
                        .container { max-width: 600px; margin: 40px auto; background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); text-align: center; }
                        h1 { color: #002f6c; }
                        .footer { font-size: 12px; color: #999; margin-top: 30px; border-top: 1px solid #eee; padding-top: 10px; }
                        .button { background-color: #002f6c; color: white; padding: 12px 20px; border-radius: 4px; text-decoration: none; display: inline-block; margin: 20px 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>%s</h1>
                        <p>%s</p>
                        <p>%s</p>
                        %s
                        <p>%s</p>
                        <div class="footer">
                            <p>&copy; 2025 Heval Group. Todos los derechos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(title, title, intro, instruction, buttonHtml, footerNote);
    }

    private String generateOrUpdateCode(String email) {
        String code = String.format("%06d", random.nextInt(1000000));

        EmailVerification existing = repository.findByEmail(email);
        if (existing != null) {
            existing.setCode(code);
            existing.setCreatedAt(LocalDateTime.now());
            repository.save(existing);
        } else {
            EmailVerification nuevo = new EmailVerification();
            nuevo.setEmail(email);
            nuevo.setCode(code);
            nuevo.setCreatedAt(LocalDateTime.now());
            repository.save(nuevo);
        }

        return code;
    }
}
