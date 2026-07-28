package com.example.popupmap.controller;

import com.example.popupmap.domain.PopupStore;
import com.example.popupmap.domain.UserBookmark;
import com.example.popupmap.service.PopupNewsService;
import com.example.popupmap.service.PopupStoreService;
import com.example.popupmap.service.UserBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MypageController {

    private final UserBookmarkService bookmarkService;
    private final PopupStoreService storeService;
    private final PopupNewsService newsService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage")
    public String mypage(Principal principal, Model model) {
        String username = principal.getName();
        List<UserBookmark> bookmarks = bookmarkService.getBookmarks(username);

        LocalDate today = LocalDate.now();
        List<Map<String, Object>> items = new ArrayList<>();
        List<Map<String, Object>> itemsJson = new ArrayList<>();

        for (UserBookmark bm : bookmarks) {
            Optional<PopupStore> opt = storeService.getById(bm.getStoreId());
            if (opt.isEmpty()) continue;

            PopupStore s = opt.get();
            LocalDate start = s.getStartDate();
            LocalDate end   = s.getEndDate();

            String storeStatus;
            long dday;
            String ddayLabel;

            if (start != null && start.isAfter(today)) {
                storeStatus = "upcoming";
                dday = ChronoUnit.DAYS.between(today, start);
                ddayLabel = "오픈 D-" + dday;
            } else if (end != null && !end.isBefore(today)) {
                storeStatus = "ongoing";
                dday = ChronoUnit.DAYS.between(today, end);
                ddayLabel = dday == 0 ? "오늘 마감" : "종료 D-" + dday;
            } else {
                storeStatus = "ended";
                dday = 0;
                ddayLabel = "종료됨";
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("store", s);
            item.put("visitStatus", bm.getVisitStatus().name());
            item.put("storeStatus", storeStatus);
            item.put("dday", dday);
            item.put("ddayLabel", ddayLabel);
            items.add(item);

            Map<String, Object> ij = new LinkedHashMap<>();
            ij.put("id", s.getId());
            ij.put("name", s.getName());
            ij.put("region", s.getRegion());
            ij.put("category", s.getCategory());
            ij.put("imageUrl", s.getImageUrl());
            ij.put("latitude", s.getLatitude());
            ij.put("longitude", s.getLongitude());
            ij.put("startDate", start != null ? start.toString() : null);
            ij.put("endDate",   end   != null ? end.toString()   : null);
            ij.put("visitStatus", bm.getVisitStatus().name());
            ij.put("storeStatus", storeStatus);
            ij.put("dday", dday);
            ij.put("ddayLabel", ddayLabel);
            itemsJson.add(ij);
        }

        long plannedCount   = items.stream().filter(m -> "PLANNED".equals(m.get("visitStatus"))).count();
        long completedCount = items.stream().filter(m -> "COMPLETED".equals(m.get("visitStatus"))).count();

        List<Map<String, Object>> ddayItems = new ArrayList<>(items);
        ddayItems.sort(Comparator
                .comparingInt((Map<String, Object> m) -> {
                    String st = (String) m.get("storeStatus");
                    return "ongoing".equals(st) ? 0 : "upcoming".equals(st) ? 1 : 2;
                })
                .thenComparingLong(m -> (long) m.get("dday")));

        model.addAttribute("items", items);
        model.addAttribute("ddayItems", ddayItems);
        model.addAttribute("itemsJson", itemsJson);
        model.addAttribute("username", username);
        model.addAttribute("totalCount", items.size());
        model.addAttribute("plannedCount", plannedCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("reviewCount", newsService.countReviewsByUser(username));
        model.addAttribute("likedCount", newsService.countLikesByUser(username));
        model.addAttribute("userReviews", newsService.getReviewsByUser(username));
        model.addAttribute("likedPosts", newsService.getLikedPostsByUser(username));

        return "mypage";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/bookmarks/{storeId}/toggle")
    @ResponseBody
    public ResponseEntity<?> toggle(@PathVariable Long storeId, Principal principal) {
        boolean bookmarked = bookmarkService.toggle(principal.getName(), storeId);
        return ResponseEntity.ok(Map.of("bookmarked", bookmarked));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/api/bookmarks/{storeId}/status")
    @ResponseBody
    public ResponseEntity<?> updateStatus(@PathVariable Long storeId,
                                          @RequestBody Map<String, String> body,
                                          Principal principal) {
        UserBookmark.VisitStatus status = UserBookmark.VisitStatus.valueOf(body.get("status"));
        bookmarkService.updateStatus(principal.getName(), storeId, status);
        return ResponseEntity.ok(Map.of("status", status.name()));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/api/bookmarks/{storeId}")
    @ResponseBody
    public ResponseEntity<?> remove(@PathVariable Long storeId, Principal principal) {
        bookmarkService.remove(principal.getName(), storeId);
        return ResponseEntity.ok(Map.of("removed", true));
    }
}
