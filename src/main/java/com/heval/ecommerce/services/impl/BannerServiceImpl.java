package com.heval.ecommerce.services.impl;

import com.heval.ecommerce.entity.Banner;
import com.heval.ecommerce.exception.ApiValidateException;
import com.heval.ecommerce.repository.BannerRepository;
import com.heval.ecommerce.services.BannerService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class BannerServiceImpl extends CrudServiceImpl<Banner, Long> implements BannerService {

    private final BannerRepository bannerRepository;


    public BannerServiceImpl(BannerRepository repository) {
        super(repository);
        this.bannerRepository = repository;

    }

    @Override
    @Transactional
    public Banner save(Banner entity) {
        long count = bannerRepository.count();
        if (count >= 1) {
            throw new ApiValidateException("Solo se permiten 2 banners.");
        }
        return super.save(entity);
    }
}
