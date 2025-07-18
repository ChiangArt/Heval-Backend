package com.heval.ecommerce.mapper;
import com.heval.ecommerce.dto.request.CollectionRequest;
import com.heval.ecommerce.dto.response.CollectionResponse;
import com.heval.ecommerce.entity.Collection;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class CollectionMapper {

    public Collection toEntity(CollectionRequest request) {
        return Collection.builder()
                .name(request.name())
                .headlineTitle(request.headlineTitle())
                .descriptionLine1(request.descriptionLine1())
                .descriptionLine2(request.descriptionLine2())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

    }

    public CollectionResponse toResponse(Collection collection) {
        return new CollectionResponse(
                collection.getId(),
                collection.getName(),
                collection.getCreatedAt(),
                collection.getSlug(),
                collection.getHeadlineTitle(),
                collection.getDescriptionLine1(),
                collection.getDescriptionLine2(),
                collection.isActive()
        );
    }

    public void updateEntityFromRequest(CollectionRequest request, Collection entity) {
        entity.setName(request.name());
    }
}
