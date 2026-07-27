package com.example.popupmap.controller;

import com.example.popupmap.crawler.PopgaCrawlerService;
import com.example.popupmap.service.PopupNewsService;
import com.example.popupmap.service.PopupStoreService;
import com.example.popupmap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
