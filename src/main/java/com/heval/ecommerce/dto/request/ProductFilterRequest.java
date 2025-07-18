package com.heval.ecommerce.dto.request;

import java.util.List;

public record ProductFilterRequest(List<String> colors,
                                   Long collectionId,
                                   String searchText
                                   ) {
}


