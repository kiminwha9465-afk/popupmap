package com.example.popupmap.repository;

import com.example.popupmap.domain.PopupNews;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PopupNewsRepository extends JpaRepository<PopupNews, Long> {
    List<PopupNews> findAllByOrderByIdDesc();
    List<PopupNews> findByTagNotOrderByIdDesc(String tag);
    List<PopupNews> findTop5ByOrderByIdDesc();
    List<PopupNews> findByTagOrderByIdDesc(String tag);
    List<PopupNews> findByPopupStoreIdOrderByPublishedAtDesc(Long popupStoreId);
    List<PopupNews> findByPopupStoreIdAndTagOrderByIdDesc(Long popupStoreId, String tag);
    long countByTag(String tag);
    long countByAuthorUsernameAndTag(String authorUsername, String tag);
    List<PopupNews> findByAuthorUsernameAndTagOrderByIdDesc(String authorUsername, String tag);
}
