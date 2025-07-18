package com.heval.ecommerce.entity;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.heval.ecommerce.dto.enumeration.UserStatus;
import com.heval.ecommerce.dto.enumeration.UserRole;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre completo del usuario */
    private String name;

    /** Email único para identificación */
    private String email;

    @Column(name="identity_document")
    private String identityDocument;

    /** Celular único para identificación */
    private String cel;

    /** Contraseña encriptada del usuario */

    private String password;

    /** Rol del usuario (ADMIN, CLIENT) */
    @Enumerated(EnumType.STRING)
    private UserRole role;

    /** Fecha de creación o registro del usuario */
    private LocalDateTime createdAt;

    /** Estado del usuario (ACTIVE o INACTIVE) */
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ShippingAddress> shippingAddresses;


    @ElementCollection
    @CollectionTable(name = "payment_information", joinColumns = @JoinColumn(name="user_id"))
    private List<PaymentInformation> paymentInformation;

}

