package com.heval.ecommerce.services.impl;

import com.heval.ecommerce.entity.Coupon;
import com.heval.ecommerce.repository.CouponRepository;
import com.heval.ecommerce.services.CouponService;
import org.springframework.stereotype.Service;

@Service
public class CouponServiceImpl extends CrudServiceImpl<Coupon, Long> implements CouponService {

    public CouponServiceImpl(CouponRepository repository) {
        super(repository);
    }
}
