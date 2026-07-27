package com.example.popupmap.controller;

import com.example.popupmap.domain.PopupNews;
import com.example.popupmap.domain.PopupStore;
import com.example.popupmap.service.PopupNewsService;
import com.example.popupmap.service.PopupStoreService;
import com.example.popupmap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class NewsController {

    private final PopupNewsService newsService;
    private final UserService userService;
    private final PopupStoreService storeService;

    @GetMapping("/news")
    public String list(@RequestParam(required = false) String tag, Model model) {
        List<PopupNews> newsList = newsService.getAll(tag);
        model.addAttribute("newsList", newsList);
        model.addAttribute("selectedTag", tag);

        if ("후기".equals(tag)) {
            Map<Long, PopupStore> storeMap = newsList.stream()
                    .filter(n -> n.getPopupStoreId() != null)
                    .map(n -> storeService.getById(n.getPopupStoreId()))
                    .filter(java.util.Optional::isPresent)
                    .map(java.util.Optional::get)
                    .collect(Collectors.toMap(PopupStore::getId, s -> s, (a, b) -> a));
            model.addAttribute("storeMap", storeMap);

            Map<String, Long> reviewCounts = newsList.stream()
                    .filter(n -> n.getAuthorUsername() != null)
                    .collect(Collectors.groupingBy(PopupNews::getAuthorUsername, Collectors.counting()));
            model.addAttribute("reviewCounts", reviewCounts);

            Map<Long, Long> likeCountMap = newsList.stream()
                    .collect(Collectors.toMap(PopupNews::getId, n -> newsService.getLikeCount(n.getId())));
            model.addAttribute("likeCountMap", likeCountMap);

            // JS 인라인용 DTO (LocalDate 포함 엔티티 직렬화 오류 방지)
            List<java.util.Map<String, Object>> reviewsJson = newsList.stream().map(n -> {
                java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
                m.put("id", n.getId());
                m.put("author", n.getAuthor());
                m.put("authorUsername", n.getAuthorUsername());
                m.put("rating", n.getRating());
                m.put("content", n.getContent());
                m.put("thumbnailUrl", n.getThumbnailUrl());
                m.put("popupStoreId", n.getPopupStoreId());
                m.put("publishedAt", n.getPublishedAt() != null ? n.getPublishedAt().toString() : null);
                return m;
            }).collect(Collectors.toList());
            model.addAttribute("reviewsJson", reviewsJson);

            java.util.Map<Long, java.util.Map<String, Object>> storesJson = new java.util.LinkedHashMap<>();
            storeMap.forEach((id, s) -> {
                java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
                m.put("id", s.getId());
                m.put("name", s.getName());
                m.put("imageUrl", s.getImageUrl());
                m.put("category", s.getCategory());
                m.put("region", s.getRegion());
                storesJson.put(id, m);
            });
            model.addAttribute("storesJson", storesJson);
        } else {
            model.addAttribute("storeMap", java.util.Collections.emptyMap());
            model.addAttribute("reviewCounts", java.util.Collections.emptyMap());
            model.addAttribute("likeCountMap", java.util.Collections.emptyMap());
            model.addAttribute("reviewsJson", java.util.Collections.emptyList());
            model.addAttribute("storesJson", java.util.Collections.emptyMap());
        }

        return "news";
    }

    @GetMapping("/news/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(required = false) String from,
                         Principal principal, Model model) {
        PopupNews news = newsService.getByIdAndIncrementView(id);
        model.addAttribute("news", news);

        String username = principal != null ? principal.getName() : null;
        boolean isAuthor = username != null
                && username.equals(news.getAuthorUsername());
        model.addAttribute("isAuthor", isAuthor);
        model.addAttribute("likeCount", newsService.getLikeCount(id));
        model.addAttribute("liked", username != null && newsService.isLikedBy(id, username));

        if (news.getPopupStoreId() != null) {
            storeService.getById(news.getPopupStoreId())
                    .ifPresent(s -> model.addAttribute("linkedStore", s));
        }
        String backUrl = (from != null && !from.isBlank()) ? "/news?tag=" + from : "/news";
        model.addAttribute("backUrl", backUrl);
        return "news-detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/news/write")
    public String writeForm(@RequestParam(required = false) String tag,
                            @RequestParam(required = false) Long storeId,
                            Model model) {
        model.addAttribute("defaultTag", tag != null ? tag : "공지");
        model.addAttribute("stores", storeService.getAll());
        if (storeId != null) {
            storeService.getById(storeId).ifPresent(s -> model.addAttribute("preSelectedStore", s));
        }
        return "news-write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/news/write")
    public String write(@RequestParam String title,
                        @RequestParam String tag,
                        @RequestParam String content,
                        @RequestParam(required = false) MultipartFile thumbnailFile,
                        @RequestParam(required = false) Long popupStoreId,
                        @RequestParam(required = false) Integer rating,
                        Principal principal) throws IOException {
        String thumbnailUrl = null;
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String original = thumbnailFile.getOriginalFilename();
            String ext = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf('.'))
                    : "";
            String filename = UUID.randomUUID() + ext;
            Path uploadDir = Paths.get("data/uploads");
            Files.createDirectories(uploadDir);
            Files.copy(thumbnailFile.getInputStream(), uploadDir.resolve(filename));
            thumbnailUrl = "/uploads/" + filename;
        }

        String username = principal.getName();
        String nickname = userService.getNicknameByUsername(username);

        Long resolvedStoreId = (popupStoreId != null && popupStoreId > 0) ? popupStoreId : null;

        newsService.save(PopupNews.builder()
                .title(title)
                .tag(tag)
                .content(content)
                .thumbnailUrl(thumbnailUrl)
                .author(nickname)
                .authorUsername(username)
                .popupStoreId(resolvedStoreId)
                .rating("후기".equals(tag) ? rating : null)
                .publishedAt(LocalDate.now())
                .build());
        return "redirect:/news";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/news/{id}/like")
    public String like(@PathVariable Long id, Principal principal) {
        newsService.toggleLike(id, principal.getName());
        return "redirect:/news/" + id;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/news/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        PopupNews news = newsService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("소식을 찾을 수 없습니다: " + id));
        if (!principal.getName().equals(news.getAuthorUsername())) {
            return "redirect:/news/" + id;
        }
        newsService.delete(id);
        return "redirect:/news";
    }
}
