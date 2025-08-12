package com.heval.ecommerce.services.impl;
import com.heval.ecommerce.entity.EmailVerification;
import com.heval.ecommerce.exception.ApiValidateException;
import com.heval.ecommerce.repository.EmailVerificationRepository;
import com.heval.ecommerce.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final RestTemplate restTemplate;
    private final EmailVerificationRepository repository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.brevo.api-key}")
    private String brevoApiKey;

    private static final String FROM_EMAIL = "heval.group.contact@gmail.com";
    private static final String FROM_NAME = "Heval Shop";

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Random random = new Random();

    private void sendEmail(String to, String subject, String htmlContent) {
        if (!StringUtils.hasText(to) || !EMAIL_PATTERN.matcher(to).matches()) {
            throw new IllegalArgumentException("Correo inválido");
        }

        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("sender", Map.of("name", FROM_NAME, "email", FROM_EMAIL));
        body.put("to", List.of(Map.of("email", to)));
        body.put("subject", subject);
        body.put("htmlContent", htmlContent);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error enviando email: " + response.getBody());
        }
    }

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
        String link = baseUrl + "/auth/login/reset-password?token=" + token;

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
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: 'Arial', sans-serif;
                            line-height: 1.6;
                            color: #333;
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                        }
                        .header {
                            background-color: #2c3e50;
                            padding: 20px;
                            text-align: center;
                            border-radius: 5px 5px 0 0;
                        }
                        .header h1 {
                            color: #ffffff;
                            margin: 0;
                            font-size: 24px;
                        }
                        .content {
                            padding: 20px;
                            background-color: #f9f9f9;
                            border: 1px solid #e1e1e1;
                            border-top: none;
                            border-radius: 0 0 5px 5px;
                        }
                        .detail {
                            margin-bottom: 15px;
                        }
                        .detail strong {
                            color: #2c3e50;
                            display: inline-block;
                            width: 80px;
                        }
                        .message {
                            background-color: #ffffff;
                            padding: 15px;
                            border: 1px solid #e1e1e1;
                            border-radius: 5px;
                            margin-top: 10px;
                        }
                        .footer {
                            margin-top: 20px;
                            font-size: 12px;
                            text-align: center;
                            color: #7f8c8d;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>Nuevo Mensaje de Contacto al por Mayor</h1>
                    </div>
                    <div class="content">
                        <div class="detail">
                            <strong>Nombre:</strong> %s
                        </div>
                        <div class="detail">
                            <strong>Email:</strong> <a href="mailto:%s">%s</a>
                        </div>
                        <div class="detail">
                            <strong>Mensaje:</strong>
                            <div class="message">%s</div>
                        </div>
                    </div>
                    <div class="footer">
                        Este mensaje fue enviado a través del formulario de contacto al por mayor de HevalShop.
                        <br>
                        © %d HevalShop - Todos los derechos reservados.
                    </div>
                </body>
                </html>
                """.formatted(
                HtmlUtils.htmlEscape(name),
                HtmlUtils.htmlEscape(email),
                HtmlUtils.htmlEscape(email),
                message.replace("\n", "<br>"),
                Year.now().getValue()
        );

        sendEmail("heval.group.contact@gmail.com", "Contacto al por mayor - HevalShop", htmlContent);
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
