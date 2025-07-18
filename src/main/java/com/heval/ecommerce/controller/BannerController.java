package com.heval.ecommerce.controller;
import com.heval.ecommerce.dto.request.BannerRequest;
import com.heval.ecommerce.entity.Banner;
import com.heval.ecommerce.services.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @PostMapping
    public Banner create(@RequestBody BannerRequest request) {
        Banner banner = Banner.builder()
                .urls(request.urls())
                .build();
        return bannerService.save(banner);
    }

    @PutMapping("/{id}")
    public Banner update(@PathVariable Long id, @RequestBody BannerRequest request) {
        Banner banner = Banner.builder()
                .id(id)
                .urls(request.urls())
                .build();
        return bannerService.update(id, banner);
    }

    @GetMapping
    public List<Banner> getAll() {
        return bannerService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Banner> getById(@PathVariable Long id) {
        return bannerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bannerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

