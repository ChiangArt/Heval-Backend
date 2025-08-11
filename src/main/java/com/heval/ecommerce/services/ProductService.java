package com.heval.ecommerce.services;

import com.heval.ecommerce.dto.request.ProductFilterRequest;
import com.heval.ecommerce.dto.response.ProductCardResponse;
import com.heval.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductService {

    Page<ProductCardResponse> getfilteredProducts(ProductFilterRequest productFilterRequest, Pageable pageable);
    Product findProductById(Long id);
    Product findProductBySlug(String slug);
    Product createProduct(Product product);
    Product updateProduct(Long productId, Product product);
    void deleteProduct(Long id);
    List<Product> findProductsByCollectionId(Long collectionId);
    BigDecimal calculateCurrentPrice(BigDecimal price, Integer discountPercent, LocalDateTime discountUntil);
    List<String> obtenerColores();

}
