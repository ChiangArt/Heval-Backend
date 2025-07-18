package com.heval.ecommerce.services.impl;
import com.heval.ecommerce.dto.request.CollectionRequest;
import com.heval.ecommerce.dto.response.CollectionResponse;
import com.heval.ecommerce.entity.Collection;
import com.heval.ecommerce.exception.ApiValidateException;
import com.heval.ecommerce.mapper.CollectionMapper;
import com.heval.ecommerce.repository.CollectionRepository;
import com.heval.ecommerce.services.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final CollectionMapper collectionMapper;


    @Override
    public List<CollectionResponse> getAllCollections() {
        return collectionRepository.findByActiveTrue()
                .stream()
                .map(collectionMapper::toResponse)
                .toList();
    }

    @Override
    public Optional<CollectionResponse> getCollectionById(Long id) {
        return collectionRepository.findById(id)
                .map(collectionMapper::toResponse);
    }

    @Override
    public CollectionResponse createCollection(CollectionRequest collectionRequest) {

        var entity = collectionMapper.toEntity(collectionRequest);

        var savedEntity = collectionRepository.save(entity);

        String slug = generateSlug(savedEntity);

        savedEntity.setSlug(slug);

        savedEntity = collectionRepository.save(savedEntity);

        return collectionMapper.toResponse(savedEntity);
    }


    @Override
    public CollectionResponse updateCollection(Long id, CollectionRequest collectionRequest) {
        var existing = collectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        collectionMapper.updateEntityFromRequest(collectionRequest, existing);
        var updated = collectionRepository.save(existing);
        return collectionMapper.toResponse(updated);
    }

    @Override
    public void deleteCollection(Long id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Colección no encontrada"));

        collection.setActive(false);
        collectionRepository.save(collection);
    }

    private String generateSlug(Collection collection) {
        String titleSlug = collection.getName().toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9\\-]", "");

        return collection.getId() + "-" + titleSlug;
    }

}
