package com.heval.ecommerce.services.impl;

import com.heval.ecommerce.entity.Banner;
import com.heval.ecommerce.repository.BannerRepository;
import com.heval.ecommerce.services.BannerService;
import org.springframework.stereotype.Service;

@Service
public class BannerServiceImpl extends CrudServiceImpl<Banner, Long> implements BannerService {

    public BannerServiceImpl(BannerRepository repository) {
        super(repository);
    }
}
