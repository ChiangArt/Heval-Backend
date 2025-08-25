package com.heval.ecommerce.controller;
import com.heval.ecommerce.dto.request.ProductFilterRequest;
import com.heval.ecommerce.dto.request.ProductRequest;
import com.heval.ecommerce.dto.request.UpdateProductRequest;
import com.heval.ecommerce.dto.response.ProductCardResponse;
import com.heval.ecommerce.dto.response.ProductResponse;
import com.heval.ecommerce.entity.Product;
import com.heval.ecommerce.mapper.ProductMapper;
import com.heval.ecommerce.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        Product productToSave = productMapper.toEntity(request);
        Product createdProduct = productService.createProduct(productToSave);

        BigDecimal currentPrice = productService.calculateCurrentPrice(createdProduct.getPrice(), createdProduct.getDiscountPercentage(), createdProduct.getDiscountUntil());
        ProductResponse response = productMapper.toResponse(createdProduct, currentPrice);

        return ResponseEntity.ok(response);
    }



    @GetMapping
    public ResponseEntity<Page<ProductCardResponse>> getFilteredProducts(
            @RequestParam(required = false) String colors,
            @RequestParam(required = false) Long collectionId,
            @RequestParam(required = false) String searchText,
            Pageable pageable
    ) {
        ProductFilterRequest request = new ProductFilterRequest(colors, collectionId, searchText);
        Page<ProductCardResponse> responsePage = productService.getfilteredProducts(request, pageable);
        return ResponseEntity.ok(responsePage);
    }




    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = productService.findProductById(id);
        BigDecimal currentPrice = productService.calculateCurrentPrice(product.getPrice(), product.getDiscountPercentage(), product.getDiscountUntil());
        return ResponseEntity.ok(productMapper.toResponse(product, currentPrice));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductResponse> getProductBySlug(@PathVariable String slug) {
        Product product = productService.findProductBySlug(slug);
        BigDecimal currentPrice = productService.calculateCurrentPrice(product.getPrice(), product.getDiscountPercentage(), product.getDiscountUntil());
        return ResponseEntity.ok(productMapper.toResponse(product, currentPrice));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProductRequest request) {

        Product updatedProduct = productService.updateProduct(id, request);

        BigDecimal currentPrice = productService.calculateCurrentPrice(
                updatedProduct.getPrice(),
                updatedProduct.getDiscountPercentage(),
                updatedProduct.getDiscountUntil()
        );

        return ResponseEntity.ok(productMapper.toResponse(updatedProduct, currentPrice));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-collection/{collectionId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCollection(@PathVariable Long collectionId) {
        List<Product> products = productService.findProductsByCollectionId(collectionId);

        List<ProductResponse> responses = products.stream().map(p -> {
            BigDecimal currentPrice = productService.calculateCurrentPrice(p.getPrice(), p.getDiscountPercentage(), p.getDiscountUntil());
            return productMapper.toResponse(p, currentPrice);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/colors")
    public ResponseEntity<List<String>> getColors() {
        List<String> colors = productService.obtenerColores();
        return ResponseEntity.ok(colors);
    }
}
