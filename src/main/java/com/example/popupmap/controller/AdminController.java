package com.example.popupmap.controller;

import com.example.popupmap.crawler.PopgaCrawlerService;
import com.example.popupmap.domain.PopupStore;
import com.example.popupmap.service.PopupNewsService;
import com.example.popupmap.service.PopupStoreService;
import com.example.popupmap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final PopupStoreService storeService;
    private final PopupNewsService newsService;
    private final UserService userService;
    private final PopgaCrawlerService crawlerService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("storeCount", storeService.count());
        model.addAttribute("newsCount", newsService.count());
        model.addAttribute("userCount", userService.count());
        return "admin/dashboard";
    }

    // ── 팝업 관리 ──────────────────────────────────────
    @GetMapping("/stores")
    public String stores(Model model) {
        model.addAttribute("stores", storeService.getAll());
        return "admin/stores";
    }

    @GetMapping("/stores/new")
    public String newStoreForm() {
        return "admin/store-form";
    }

    @PostMapping("/stores/new")
    public String createStore(
            @RequestParam String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String address,
            @RequestParam(defaultValue = "0") double latitude,
            @RequestParam(defaultValue = "0") double longitude,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String website,
            @RequestParam(required = false) String hours,
            @RequestParam(required = false) String description,
            RedirectAttributes ra) {

        PopupStore store = PopupStore.builder()
                .name(name).brand(brand).category(category).region(region)
                .address(address).latitude(latitude).longitude(longitude)
                .startDate(startDate).endDate(endDate)
                .imageUrl(imageUrl).website(website).hours(hours).description(description)
                .build();
        storeService.save(store);
        ra.addFlashAttribute("msg", "팝업스토어가 등록되었습니다.");
        return "redirect:/admin/stores";
    }

    @PostMapping("/stores/{id}/delete")
    public String deleteStore(@PathVariable Long id, RedirectAttributes ra) {
        storeService.delete(id);
        ra.addFlashAttribute("msg", "팝업스토어가 삭제되었습니다.");
        return "redirect:/admin/stores";
    }

    @PostMapping("/crawl")
    public String crawl(RedirectAttributes ra) {
        try {
            int count = crawlerService.crawlAndSave();
            ra.addFlashAttribute("msg", "크롤링 완료: " + count + "개 처리");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "크롤링 실패: " + e.getMessage());
        }
        return "redirect:/admin/stores";
    }

    // ── 게시판 관리 ────────────────────────────────────
    @GetMapping("/news")
    public String news(Model model) {
        model.addAttribute("newsList", newsService.getAll(null));
        return "admin/news";
    }

    @PostMapping("/news/{id}/delete")
    public String deleteNews(@PathVariable Long id, RedirectAttributes ra) {
        newsService.delete(id);
        ra.addFlashAttribute("msg", "게시글이 삭제되었습니다.");
        return "redirect:/admin/news";
    }

    // ── 후기 관리 ──────────────────────────────────────
    @GetMapping("/reviews")
    public String reviews(Model model) {
        model.addAttribute("reviewList", newsService.getAll("후기"));
        return "admin/reviews";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id, RedirectAttributes ra) {
        newsService.delete(id);
        ra.addFlashAttribute("msg", "후기가 삭제되었습니다.");
        return "redirect:/admin/reviews";
    }

    // ── 회원 관리 ──────────────────────────────────────
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteById(id);
        ra.addFlashAttribute("msg", "회원이 삭제되었습니다.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable Long id,
                             @RequestParam String role,
                             RedirectAttributes ra) {
        userService.changeRole(id, role);
        ra.addFlashAttribute("msg", "권한이 변경되었습니다.");
        return "redirect:/admin/users";
    }
}
