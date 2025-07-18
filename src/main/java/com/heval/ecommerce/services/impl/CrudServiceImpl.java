package com.heval.ecommerce.services.impl;

import com.heval.ecommerce.services.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CrudServiceImpl<T, ID> implements CrudService<T, ID> {

    protected final JpaRepository<T, ID> repository;

    @Transactional
    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Transactional
    @Override
    public T update(ID id, T entity) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Entity with id " + id + " not found");
        }
        return repository.save(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Transactional
    @Override
    public void delete(ID id) {
        repository.deleteById(id);
    }
}
