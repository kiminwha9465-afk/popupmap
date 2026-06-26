package com.example.popupmap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Value("${kakao.api.key:YOUR_KAKAO_JS_API_KEY}")
    private String kakaoApiKey;

    @ModelAttribute("kakaoApiKey")
    public String kakaoApiKey() {
        return kakaoApiKey;
    }
}
