package com.example.popupmap.controller;

import com.example.popupmap.domain.PopupStore;
import com.example.popupmap.service.PopupNewsService;
import com.example.popupmap.service.PopupStoreService;
import com.example.popupmap.service.UserBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PopupStoreController {

    private final PopupStoreService service;
    private final PopupNewsService newsService;
    private final UserBookmarkService bookmarkService;

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String keyword,
            Principal principal, Model model) {

        List<PopupStore> stores = (keyword != null && !keyword.isBlank())
                ? service.search(keyword, null, null)
                : service.getOngoing();

        model.addAttribute("stores", stores);
        model.addAttribute("keyword", keyword);
        model.addAttribute("recentNews", newsService.getRecent5());

        boolean loggedIn = principal != null;
        model.addAttribute("isLoggedIn", loggedIn);
        if (loggedIn) {
            List<Long> bmIds = new ArrayList<>(bookmarkService.getBookmarkedStoreIds(principal.getName()));
            model.addAttribute("serverBookmarks", bmIds);
        } else {
            model.addAttribute("serverBookmarks", Collections.emptyList());
        }
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
            stores = service.search(keyword, category, region);
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
    public String detail(@PathVariable Long id, Principal principal, Model model) {
        PopupStore store = service.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("팝업스토어를 찾을 수 없습니다: " + id));
        model.addAttribute("store", store);
        model.addAttribute("reviews", newsService.getReviewsByStoreId(id));
        boolean loggedIn = principal != null;
        model.addAttribute("isLoggedIn", loggedIn);
        model.addAttribute("isBookmarked", loggedIn && bookmarkService.isBookmarked(principal.getName(), id));
        return "detail";
    }
}
