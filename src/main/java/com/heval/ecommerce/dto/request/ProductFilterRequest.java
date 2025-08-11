package com.heval.ecommerce.dto.request;

import java.util.List;

public record ProductFilterRequest(
        String color,
        Long collectionId,
        String searchText
) {
}


