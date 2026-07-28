package com.example.popupmap.service;

import com.example.popupmap.domain.UserBookmark;
import com.example.popupmap.repository.UserBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserBookmarkService {

    private final UserBookmarkRepository repo;

    @Transactional(readOnly = true)
    public List<UserBookmark> getBookmarks(String username) {
        return repo.findByUsernameOrderByCreatedAtDesc(username);
    }

    @Transactional(readOnly = true)
    public Set<Long> getBookmarkedStoreIds(String username) {
        return repo.findByUsernameOrderByCreatedAtDesc(username).stream()
                .map(UserBookmark::getStoreId)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(String username, Long storeId) {
        return repo.existsByUsernameAndStoreId(username, storeId);
    }

    public boolean toggle(String username, Long storeId) {
        Optional<UserBookmark> existing = repo.findByUsernameAndStoreId(username, storeId);
        if (existing.isPresent()) {
            repo.delete(existing.get());
            return false;
        }
        repo.save(UserBookmark.builder()
                .username(username)
                .storeId(storeId)
                .visitStatus(UserBookmark.VisitStatus.PLANNED)
                .createdAt(LocalDateTime.now())
                .build());
        return true;
    }

    public void updateStatus(String username, Long storeId, UserBookmark.VisitStatus status) {
        repo.findByUsernameAndStoreId(username, storeId).ifPresent(bm -> {
            bm.setVisitStatus(status);
            repo.save(bm);
        });
    }

    public void remove(String username, Long storeId) {
        repo.deleteByUsernameAndStoreId(username, storeId);
    }
}
