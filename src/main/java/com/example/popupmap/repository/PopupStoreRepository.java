package com.example.popupmap.repository;

import com.example.popupmap.domain.PopupStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PopupStoreRepository extends JpaRepository<PopupStore, Long> {

    List<PopupStore> findByCategory(String category);

    List<PopupStore> findByRegion(String region);

    @Query("SELECT p FROM PopupStore p WHERE p.endDate >= :today ORDER BY p.startDate ASC")
    List<PopupStore> findOngoing(@Param("today") LocalDate today);

    @Query("SELECT p FROM PopupStore p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:region IS NULL OR p.region = :region) AND " +
           "p.endDate >= :today " +
           "ORDER BY p.startDate ASC")
    List<PopupStore> findByFilter(
            @Param("category") String category,
            @Param("region") String region,
            @Param("today") LocalDate today);

    @Query("SELECT p FROM PopupStore p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<PopupStore> searchByKeyword(@Param("keyword") String keyword);
}
