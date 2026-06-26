package com.example.popupmap.controller;

import com.example.popupmap.domain.PopupStore;
import com.example.popupmap.service.PopupStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PopupStoreController {

    private final PopupStoreService service;

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String keyword,
            Model model) {

        List<PopupStore> stores = (keyword != null && !keyword.isBlank())
                ? service.search(keyword)
                : service.getOngoing();

        model.addAttribute("stores", stores);
        model.addAttribute("keyword", keyword);
        return "index";
    }

    @GetMapping("/stores")
    public String stores(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<PopupStore> stores;
        if (keyword != null && !keyword.isBlank()) {
            stores = service.search(keyword);
        } else {
            stores = service.getByFilter(category, region);
        }

        model.addAttribute("stores", stores);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedRegion", region);
        model.addAttribute("keyword", keyword);
        return "stores";
    }

    @GetMapping("/store/{id}")
    public String detail(@PathVariable Long id, Model model) {
        PopupStore store = service.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("팝업스토어를 찾을 수 없습니다: " + id));
        model.addAttribute("store", store);
        return "detail";
    }
}
