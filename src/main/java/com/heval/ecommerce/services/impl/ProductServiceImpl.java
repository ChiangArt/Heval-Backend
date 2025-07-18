package com.heval.ecommerce.services.impl;
import com.heval.ecommerce.dto.request.ProductAdminFilterRequest;
import com.heval.ecommerce.dto.request.ProductFilterRequest;
import com.heval.ecommerce.dto.response.ProductAdminProjection;
import com.heval.ecommerce.dto.response.ProductCardProjection;
import com.heval.ecommerce.dto.response.ProductCardResponse;
import com.heval.ecommerce.entity.Collection;
import com.heval.ecommerce.entity.Product;
import com.heval.ecommerce.exception.ApiValidateException;
import com.heval.ecommerce.repository.CollectionRepository;
import com.heval.ecommerce.repository.ProductRepository;
import com.heval.ecommerce.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CollectionRepository collectionRepository;

    @Override
    public Page<ProductCardResponse> filterProducts(ProductFilterRequest filter, Pageable pageable) {
        String colorsCsv = (filter.colors() != null && !filter.colors().isEmpty())
                ? String.join(",", filter.colors())
                : null;

        Page<ProductCardProjection> projections = productRepository.findAllCardProductsNative(
                filter.collectionId(),
                filter.searchText(),
                colorsCsv,
                pageable
        );

        return projections.map(proj -> {
            BigDecimal recalculatedPrice = calculateCurrentPrice(
                    proj.getPrice(),
                    proj.getDiscountPercentage(),
                    proj.getDiscountUntil()
            );

            return new ProductCardResponse(
                    proj.getId(),
                    proj.getTitle(),
                    proj.getSlug(),
                    proj.getPrice(),
                    proj.getDiscountPercentage(),
                    recalculatedPrice,
                    List.of(proj.getImageUrls()),
                    List.of(proj.getColors()),
                    proj.getDiscountUntil()
            );
        });
    }

    @Override
    public Page<ProductAdminProjection> filterAdminProducts(ProductAdminFilterRequest filter, Pageable pageable) {
        String searchText = (filter.searchText() != null && !filter.searchText().isBlank())
                ? filter.searchText()
                : null;

        return productRepository.findAllAdminProducts(searchText, pageable);
    }



    @Cacheable(value = "productById", key = "#id")
    @Override
    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Producto no encontrado con id: " + id));
    }

    @Cacheable(value = "productBySlug", key = "#slug")
    @Override
    public Product findProductBySlug(String slug) {
        return productRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() -> new ApiValidateException("Producto no encontrado con slug: " + slug));
    }

    @Transactional
    @CacheEvict(value = "productsByCollection", key = "#product.collection.id")
    @Override
    public Product createProduct(Product product) {
        Collection collection = collectionRepository.findById(product.getCollection().getId())
                .orElseThrow(() -> new ApiValidateException("Colección no encontrada"));

        product.setCollection(collection);
        product.setSlug(generateSlug(product.getTitle()));

        return productRepository.save(product);
    }

    @Transactional
    @CacheEvict(value = "productsByCollection", key = "#product.collection.id")
    @Override
    public Product updateProduct(Long productId, Product product) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ApiValidateException("Producto no encontrado con id: " + productId));

        existingProduct.setTitle(product.getTitle());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setColors(product.getColors());
        existingProduct.setDescriptionArchetype(product.getDescriptionArchetype());
        existingProduct.setMaterial(product.getMaterial());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDiscountPercentage(product.getDiscountPercentage());
        existingProduct.setImageUrls(product.getImageUrls());
        existingProduct.setDiscountUntil(product.getDiscountUntil());

        if (product.getCollection() != null && product.getCollection().getId() != null) {
            Collection collection = collectionRepository.findById(product.getCollection().getId())
                    .orElseThrow(() -> new ApiValidateException("Colección no encontrada con id: " + product.getCollection().getId()));
            existingProduct.setCollection(collection);
        } else {
            existingProduct.setCollection(null);
        }

        return productRepository.save(existingProduct);
    }

    @Transactional
    @CacheEvict(value = "productsByCollection", allEntries = true)
    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Producto no encontrado con id: " + id));

        product.setActive(false);

        productRepository.save(product);
    }

    @Cacheable(value = "productsByCollection", key = "#collectionId")
    @Override
    public List<Product> findProductsByCollectionId(Long collectionId) {
        return productRepository.findByCollectionId(collectionId);
    }

    public BigDecimal calculateCurrentPrice(BigDecimal price, Integer discountPercent, LocalDateTime discountUntil) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        boolean isDiscountValid = discountPercent != null && discountPercent > 0
                && (discountUntil == null || LocalDateTime.now().isBefore(discountUntil));

        if (isDiscountValid) {
            BigDecimal multiplier = BigDecimal.valueOf(100 - discountPercent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return price.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        }

        return price.setScale(2, RoundingMode.HALF_UP);
    }

    private String generateSlug(String title) {
        if (title == null || title.isBlank()) {
            return UUID.randomUUID().toString().substring(0, 8);
        }

        String base = title.toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "");

        return UUID.randomUUID().toString().substring(0, 8) + "-" + base;
    }
}