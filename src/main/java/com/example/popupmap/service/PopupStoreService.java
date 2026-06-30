package com.example.popupmap.service;

import com.example.popupmap.domain.PopupStore;
import com.example.popupmap.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PopupStoreService {

    private final PopupStoreRepository repository;

    public List<PopupStore> getAll() {
        return repository.findAll();
    }

    public List<PopupStore> getOngoing() {
        return repository.findOngoing(LocalDate.now());
    }

    public List<PopupStore> getByFilter(String category, String region) {
        String cat = (category != null && category.isBlank()) ? null : category;
        String reg = (region != null && region.isBlank()) ? null : region;
        return repository.findByFilter(cat, reg, LocalDate.now());
    }

    public List<PopupStore> search(String keyword, String category, String region) {
        String cat = (category != null && category.isBlank()) ? null : category;
        String reg = (region   != null && region.isBlank())   ? null : region;
        return repository.searchByKeyword(keyword, cat, reg);
    }

    public Optional<PopupStore> getById(Long id) {
        return repository.findById(id);
    }
}
