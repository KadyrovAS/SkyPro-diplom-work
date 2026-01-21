package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

public interface AdService {
    Ads getAllAds();
    Ad addAd(CreateOrUpdateAd properties, MultipartFile image, Authentication authentication);
    ExtendedAd getAd(Integer id);
    void deleteAd(Integer id, Authentication authentication);
    Ad updateAd(Integer id, CreateOrUpdateAd updateAd, Authentication authentication);
    Ads getMyAds(Authentication authentication);
    void updateAdImage(Integer id, MultipartFile image, Authentication authentication);
    byte[] getAdImage(Integer id);
}