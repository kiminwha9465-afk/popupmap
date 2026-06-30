package com.example.popupmap.service;

import com.example.popupmap.domain.NewsLike;
import com.example.popupmap.domain.PopupNews;
import com.example.popupmap.repository.NewsLikeRepository;
import com.example.popupmap.repository.PopupNewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PopupNewsService {

    private final PopupNewsRepository repository;
    private final NewsLikeRepository likeRepository;

    public List<PopupNews> getAll(String tag) {
        if (tag != null && !tag.isBlank()) {
            return repository.findByTagOrderByIdDesc(tag);
        }
        return repository.findAllByOrderByIdDesc();
    }

    public List<PopupNews> getRecent5() {
        return repository.findTop5ByOrderByIdDesc();
    }

    public List<PopupNews> getReviewsByStoreId(Long storeId) {
        return repository.findByPopupStoreIdAndTagOrderByIdDesc(storeId, "후기");
    }

    public Optional<PopupNews> getById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public PopupNews getByIdAndIncrementView(Long id) {
        PopupNews news = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("소식을 찾을 수 없습니다: " + id));
        news.setViewCount(news.getViewCount() + 1);
        return news;
    }

    public PopupNews save(PopupNews news) {
        return repository.save(news);
    }

    public void delete(Long id) {
        likeRepository.deleteAll(likeRepository.findAll().stream()
                .filter(l -> l.getNewsId().equals(id)).toList());
        repository.deleteById(id);
    }

    @Transactional
    public boolean toggleLike(Long newsId, String username) {
        if (likeRepository.existsByNewsIdAndUsername(newsId, username)) {
            likeRepository.deleteByNewsIdAndUsername(newsId, username);
            return false;
        } else {
            likeRepository.save(NewsLike.builder().newsId(newsId).username(username).build());
            return true;
        }
    }

    public long getLikeCount(Long newsId) {
        return likeRepository.countByNewsId(newsId);
    }

    public boolean isLikedBy(Long newsId, String username) {
        return likeRepository.existsByNewsIdAndUsername(newsId, username);
    }
}
