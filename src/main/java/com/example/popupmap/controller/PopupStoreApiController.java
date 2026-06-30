package com.example.popupmap.controller;

import com.example.popupmap.domain.PopupStore;
import com.example.popupmap.service.PopupStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class PopupStoreApiController {

    private final PopupStoreService service;

    @GetMapping
    public List<PopupStore> getStores(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String keyword) {

        if (keyword != null && !keyword.isBlank()) {
            return service.search(keyword, category, region);
        }
        return service.getByFilter(category, region);
    }
}
