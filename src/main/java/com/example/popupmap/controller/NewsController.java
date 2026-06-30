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
        model.addAttribute("newsList", newsService.getAll(tag));
        model.addAttribute("selectedTag", tag);
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
