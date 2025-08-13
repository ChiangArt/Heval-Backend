package com.heval.ecommerce.services.impl;
import com.heval.ecommerce.entity.Banner;
import com.heval.ecommerce.exception.ApiValidateException;
import com.heval.ecommerce.repository.BannerRepository;
import com.heval.ecommerce.services.BannerService;
import com.heval.ecommerce.services.S3Service;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class BannerServiceImpl extends CrudServiceImpl<Banner, Long> implements BannerService {
    private final BannerRepository bannerRepository;
    private final S3Service s3Service;

    public BannerServiceImpl(BannerRepository repository, S3Service s3Service) {
        super(repository);
        this.bannerRepository = repository;
        this.s3Service = s3Service;
    }

    @Override
    @Transactional
    public Banner save(Banner entity) {
        long count = bannerRepository.count();
        if (count >= 2) {
            throw new ApiValidateException("Solo se permiten 2 banners.");
        }
        return super.save(entity);
    }

    @Override
    @Transactional
    public void deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Banner no encontrado con id: " + id));

        if (banner.getUrls() != null) {
            for (String imageUrl : banner.getUrls()) {
                try {
                    String key = s3Service.extractKeyFromUrl(imageUrl);
                    s3Service.deleteFile(key);
                } catch (Exception e) {
                    System.err.println("Error eliminando archivo S3: " + imageUrl + " -> " + e.getMessage());
                }
            }
        }

        bannerRepository.delete(banner);
    }
}
