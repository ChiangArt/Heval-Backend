package com.heval.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "collections")
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "headline_title")
    private String headlineTitle;

    @Column(name = "description_line_one")
    private String descriptionLine1;

    @Column(name = "description_line_two")
    private String descriptionLine2;

    @Column(name = "active")
    private boolean active;

    @OneToMany(mappedBy = "collection")
    private List<Product> products;

    private String slug;

    private LocalDateTime createdAt;


}
