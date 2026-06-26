package com.example.popupmap.crawler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/crawler")
@RequiredArgsConstructor
public class CrawlerController {

    private final PopgaCrawlerService crawlerService;

    @PostMapping("/popga")
    public ResponseEntity<Map<String, Object>> crawlPopga() {
        try {
            int count = crawlerService.crawlAndSave();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "count", count,
                "message", count + "개의 팝업스토어를 저장했습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "크롤링 실패: " + e.getMessage()
            ));
        }
    }
}
