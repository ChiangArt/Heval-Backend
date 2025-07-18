package com.heval.ecommerce.repository;

import com.heval.ecommerce.dto.response.ProductCardProjection;
import com.heval.ecommerce.dto.response.ProductAdminProjection;
import com.heval.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByCollectionId(Long collectionId);
    Optional<Product> findBySlugAndActiveTrue(String slug);

    // Para cliente
    @Query(value = """
        SELECT 
            p.id AS id,
            p.title AS title,
            p.slug AS slug,
            p.price AS price,
            CASE 
                WHEN p.discount_until IS NOT NULL AND p.discount_until > CURRENT_TIMESTAMP
                THEN (p.price - (p.price * p.discount_percentage / 100.0))
                ELSE p.price
            END AS currentPrice,
            p.discount_percentage AS discountPercentage,
            p.discount_until AS discountUntil,
            (
                SELECT ARRAY_AGG(pi.image_url)
                FROM product_images pi
                WHERE pi.product_id = p.id
            ) AS imageUrls,
            (
                SELECT ARRAY_AGG(pc.color)
                FROM product_colors pc
                WHERE pc.product_id = p.id
            ) AS colors
        FROM products p
        WHERE p.active = true
          AND (:collectionId IS NULL OR p.collection_id = :collectionId)
          AND (:searchText IS NULL OR 
               LOWER(p.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
               LOWER(p.description) LIKE LOWER(CONCAT('%', :searchText, '%')))
          AND (
               :colorsCsv IS NULL OR EXISTS (
                   SELECT 1 FROM product_colors pc 
                   WHERE pc.product_id = p.id AND pc.color = ANY(string_to_array(:colorsCsv, ','))
               )
        )
        """,
            countQuery = """
        SELECT COUNT(*) 
        FROM products p
        WHERE p.active = true
          AND (:collectionId IS NULL OR p.collection_id = :collectionId)
          AND (:searchText IS NULL OR 
               LOWER(p.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
               LOWER(p.description) LIKE LOWER(CONCAT('%', :searchText, '%')))
          AND (
               :colorsCsv IS NULL OR EXISTS (
                   SELECT 1 FROM product_colors pc 
                   WHERE pc.product_id = p.id AND pc.color = ANY(string_to_array(:colorsCsv, ','))
               )
        )
        """,
            nativeQuery = true)
    Page<ProductCardProjection> findAllCardProductsNative(
            @Param("collectionId") Long collectionId,
            @Param("searchText") String searchText,
            @Param("colorsCsv") String colorsCsv,
            Pageable pageable
    );

    // Para administrador
    @Query(value = """
    SELECT 
        p.id AS id,
        p.title AS title,
        p.slug AS slug,
        p.price AS price,
        CASE 
            WHEN p.discount_until IS NOT NULL AND p.discount_until > CURRENT_TIMESTAMP
            THEN (p.price - (p.price * p.discount_percentage / 100.0))
            ELSE p.price
        END AS currentPrice,
        p.discount_percentage AS discountPercentage,
        p.discount_until AS discountUntil,
        p.quantity AS quantity,
        (
            SELECT ARRAY_AGG(pi.image_url)
            FROM product_images pi
            WHERE pi.product_id = p.id
        ) AS imageUrls,
        (
            SELECT ARRAY_AGG(pc.color)
            FROM product_colors pc
            WHERE pc.product_id = p.id
        ) AS colors
    FROM products p
    WHERE p.active = true
      AND (:searchText IS NULL OR 
           LOWER(p.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
           LOWER(p.description) LIKE LOWER(CONCAT('%', :searchText, '%')))
    """,
            countQuery = """
    SELECT COUNT(*) 
    FROM products p
    WHERE p.active = true
      AND (:searchText IS NULL OR 
           LOWER(p.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
           LOWER(p.description) LIKE LOWER(CONCAT('%', :searchText, '%')))
    """,
            nativeQuery = true)
    Page<ProductAdminProjection> findAllAdminProducts(
            @Param("searchText") String searchText,
            Pageable pageable
    );
}
