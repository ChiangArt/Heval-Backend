package com.heval.ecommerce.controller;
import com.heval.ecommerce.dto.request.AuthRequest;
import com.heval.ecommerce.dto.request.UserRequest;
import com.heval.ecommerce.dto.response.AuthResponse;
import com.heval.ecommerce.dto.response.UserResponse;
import com.heval.ecommerce.entity.User;
import com.heval.ecommerce.exception.ApiValidateException;
import com.heval.ecommerce.mapper.UserMapper;
import com.heval.ecommerce.services.EmailService;
import com.heval.ecommerce.services.JwtService;
import com.heval.ecommerce.services.PasswordResetService;
import com.heval.ecommerce.services.UserService;
import com.heval.ecommerce.utility.JwtTokenUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "Endpoints for user authentication")
public class AuthController {


    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final PasswordResetService resetService;
    private final JwtTokenUtil jwtTokenUtil;


    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody UserRequest request) {
        boolean isValid = emailService.verifyCode(request.email(), request.code());
        if (!isValid) {
            throw new ApiValidateException("Código incorrecto.");
        }

        if (userService.emailExists(request.email())) {
            throw new ApiValidateException("El email ya se encuentra registrado.");
        }

        // 👉 Solo permitir rol CLIENT desde este endpoint
        if (request.role().name().equals("ADMIN")) {
            throw new ApiValidateException("No puedes registrarte como ADMIN.");
        }

        // Validar rol permitido
        if (!request.role().name().equals("CLIENT")) {
            throw new ApiValidateException("Rol inválido.");
        }

        User user = userMapper.toEntity(request);
        User savedUser = userService.createUser(user);
        return userMapper.toResponse(savedUser);
    }


    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        User user = userService.findByEmail(request.email())
                .orElseThrow(() -> new ApiValidateException("el correo no existe"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ApiValidateException("La contraseña es incorrecta");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token,
                user.getRole()
        );
    }

    @PostMapping("/send-code")
    public String sendCode(@RequestParam String email) {
        boolean exists = userService.emailExists(email);
        if (exists) {
            throw new ApiValidateException("El email ya está registrado.");
        }
        return emailService.sendVerificationCode(email);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        String token = resetService.createToken(email);
        emailService.sendPasswordRecoveryEmail(email, token);
        return ResponseEntity.ok("Se envió un enlace de recuperación al correo.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword
    ) {
        if (!resetService.isValid(token)) {
            return ResponseEntity.badRequest().body("Token inválido o expirado.");
        }

        String email = resetService.getEmailByToken(token);
        userService.updatePasswordByEmail(email, newPassword);
        resetService.markAsUsed(token);

        return ResponseEntity.ok("Contraseña actualizada exitosamente.");
    }


    @GetMapping("/anonymous-token")
    public ResponseEntity<String> getAnonymousToken() {
        String token = jwtTokenUtil.generateAnonymousToken();
        return ResponseEntity.ok(token);
    }

}
