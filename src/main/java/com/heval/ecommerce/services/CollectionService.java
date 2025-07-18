package com.heval.ecommerce.services;

import com.heval.ecommerce.dto.request.CollectionRequest;
import com.heval.ecommerce.dto.response.CollectionResponse;

import java.util.List;
import java.util.Optional;

public interface CollectionService {

    List<CollectionResponse> getAllCollections();

    Optional<CollectionResponse> getCollectionById(Long id);

    CollectionResponse createCollection(CollectionRequest collectionResponse);

    CollectionResponse updateCollection(Long id, CollectionRequest collectionResponse);

    void deleteCollection(Long id);
}
