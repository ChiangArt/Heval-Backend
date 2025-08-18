package com.heval.ecommerce.mapper;
import com.heval.ecommerce.dto.request.ProductRequest;
import com.heval.ecommerce.dto.response.ProductCardResponse;
import com.heval.ecommerce.dto.response.ProductResponse;
import com.heval.ecommerce.entity.Collection;
import com.heval.ecommerce.entity.Product;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class ProductMapper {

    /**
     * Convierte un ProductRequest en una entidad Product (sin imágenes).
     */
    public Product toEntity(ProductRequest request) {
        return Product.builder()
                .title(request.title())
                .description(request.description())
                .price(request.price())
                .material(request.material())
                .color(request.color())
                .quantity(request.quantity())
                .discountPercentage(request.discountPercentage())
                .discountUntil(request.discountUntil())
                .imageUrls(request.imageUrls())
                .createdAt(LocalDateTime.now())
                .active(true)
                .collection(request.collectionId() != null ? Collection.builder().id(request.collectionId()).build() : null)
                .build();
    }

    /**
     * Convierte un Product existente + imágenes en un ProductResponse.
     */
    public ProductResponse toResponse(Product product, BigDecimal currentPrice) {

        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getSlug(),
                product.getPrice(),
                product.getMaterial(),
                currentPrice,
                product.getDiscountPercentage(),
                product.getDiscountUntil(),
                product.getColor(),
                product.getQuantity(),
                product.getImageUrls(),
                product.getCreatedAt(),
                product.getCollection() != null ? product.getCollection().getId() : null,
                product.isActive()
        );
    }


    public ProductCardResponse toCardResponse(Product product, BigDecimal currentPrice) {
        return new ProductCardResponse(
                product.getId(),
                product.getTitle(),
                product.getSlug(),
                product.getPrice(),
                product.getQuantity(),
                product.getDiscountPercentage(),
                currentPrice,
                product.getImageUrls(),
                product.getDiscountUntil()
        );
    }

}
