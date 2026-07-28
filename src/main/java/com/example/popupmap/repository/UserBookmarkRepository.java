package com.example.popupmap.repository;

import com.example.popupmap.domain.UserBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserBookmarkRepository extends JpaRepository<UserBookmark, Long> {
    List<UserBookmark> findByUsernameOrderByCreatedAtDesc(String username);
    Optional<UserBookmark> findByUsernameAndStoreId(String username, Long storeId);
    void deleteByUsernameAndStoreId(String username, Long storeId);
    boolean existsByUsernameAndStoreId(String username, Long storeId);
}
