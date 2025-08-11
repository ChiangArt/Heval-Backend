package com.heval.ecommerce.specification;
import com.heval.ecommerce.dto.request.ProductFilterRequest;
import com.heval.ecommerce.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filterBy(ProductFilterRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtrar por texto (usar "title" en vez de "name")
            if (request.searchText() != null && !request.searchText().isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("title")), "%" + request.searchText().toLowerCase() + "%")
                );
            }

            // Filtrar por color
            if (request.color() != null && !request.color().isEmpty()) {
                predicates.add(root.get("color").in(request.color()));
            }

            // Filtrar por colección
            if (request.collectionId() != null) {
                predicates.add(cb.equal(root.get("collection").get("id"), request.collectionId()));
            }

            // Solo productos activos
            predicates.add(cb.isTrue(root.get("active")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

