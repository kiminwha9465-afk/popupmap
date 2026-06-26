package com.example.popupmap.controller;

import com.example.popupmap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String email,
                         @RequestParam String password,
                         @RequestParam String passwordConfirm,
                         RedirectAttributes ra) {
        if (!password.equals(passwordConfirm)) {
            ra.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/signup";
        }
        try {
            userService.register(username, email, password);
            ra.addFlashAttribute("success", "회원가입이 완료됐습니다. 로그인해주세요.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        }
    }
}
