package com.example.popupmap.repository;

import com.example.popupmap.domain.NewsLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsLikeRepository extends JpaRepository<NewsLike, Long> {
    boolean existsByNewsIdAndUsername(Long newsId, String username);
    void deleteByNewsIdAndUsername(Long newsId, String username);
    long countByNewsId(Long newsId);
    long countByUsername(String username);
    java.util.List<com.example.popupmap.domain.NewsLike> findByUsernameOrderByIdDesc(String username);
}
