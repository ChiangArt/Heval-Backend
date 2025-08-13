package com.heval.ecommerce.services;

import com.heval.ecommerce.entity.Banner;
import com.heval.ecommerce.repository.BannerRepository;

public interface BannerService extends CrudService<Banner, Long> {

    void deleteBanner(Long id);

    }
