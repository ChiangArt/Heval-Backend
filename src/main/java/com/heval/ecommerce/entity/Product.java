package com.heval.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String material;

    private String descriptionArchetype;

    private String slug;

    @Column(name = "active")
    private boolean active;

    private BigDecimal price;

    private String color;

    private Integer quantity;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    @Column(name = "discount_until")
    private LocalDateTime discountUntil;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;
}
