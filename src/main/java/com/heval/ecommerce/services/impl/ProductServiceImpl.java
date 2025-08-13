package com.heval.ecommerce.services.impl;
import com.heval.ecommerce.dto.request.ProductFilterRequest;
import com.heval.ecommerce.dto.response.ProductCardResponse;
import com.heval.ecommerce.entity.*;
import com.heval.ecommerce.exception.ApiValidateException;
import com.heval.ecommerce.mapper.ProductMapper;
import com.heval.ecommerce.repository.*;
import com.heval.ecommerce.services.*;
import com.heval.ecommerce.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
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
    private final ProductMapper productMapper;
    private final S3Service s3Service;


    @Override
    public Page<ProductCardResponse> getfilteredProducts(ProductFilterRequest productFilterRequest, Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(
                ProductSpecification.filterBy(productFilterRequest),
                pageable
        );

        return productPage.map(product -> {
            BigDecimal currentPrice = calculateCurrentPrice(
                    product.getPrice(),
                    product.getDiscountPercentage(),
                    product.getDiscountUntil()
            );
            return productMapper.toCardResponse(product, currentPrice);
        });
    }





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
    @Override
    public Product updateProduct(Long productId, Product product) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ApiValidateException("Producto no encontrado con id: " + productId));

        existingProduct.setTitle(product.getTitle());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setColor(product.getColor());
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


        if (product.getImageUrls() != null) {
            for (String imageUrl : product.getImageUrls()) {
                String key = s3Service.extractKeyFromUrl(imageUrl);
                s3Service.deleteFile(key);
            }
        }
        product.setActive(false);

        productRepository.save(product);
    }





    @Override
    public List<Product> findProductsByCollectionId(Long collectionId) {
        return productRepository.findByCollectionIdAndActiveTrue(collectionId);
    }






    @Override
    public List<String> obtenerColores() {
        return productRepository.findDistinctColors();
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