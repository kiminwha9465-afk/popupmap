package com.example.popupmap.init;

import com.example.popupmap.crawler.PopgaCrawlerService;
import com.example.popupmap.domain.PopupStore;
import com.example.popupmap.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PopupStoreRepository repository;
    private final PopgaCrawlerService crawlerService;

    @Override
    public void run(String... args) {
        try {
            int count = crawlerService.crawlAndSave();
            log.info("팝가 크롤링 완료: {}개 저장", count);
        } catch (Exception e) {
            log.warn("팝가 크롤링 실패, 샘플 데이터로 대체: {}", e.getMessage());
            repository.saveAll(sampleData());
        }
    }

    private List<PopupStore> sampleData() {
        return List.of(
            PopupStore.builder()
                .name("나이키 에어맥스 팝업").brand("Nike")
                .description("나이키 에어맥스 컬렉션을 직접 체험할 수 있는 특별 팝업스토어.")
                .category("패션").region("서울")
                .address("서울특별시 성동구 아차산로17길 48 성수동")
                .latitude(37.5447).longitude(127.0557)
                .startDate(LocalDate.now().minusDays(3)).endDate(LocalDate.now().plusDays(10))
                .imageUrl("https://placehold.co/600x400/FF6B35/white?text=Nike+Popup")
                .website("https://nike.com").hours("평일 11:00~21:00 / 주말 10:00~22:00")
                .build(),
            PopupStore.builder()
                .name("설화수 뷰티 하우스").brand("설화수")
                .description("설화수의 전통 한방 뷰티를 경험하는 프리미엄 팝업스토어.")
                .category("뷰티").region("서울")
                .address("서울특별시 강남구 도산대로 317 신사동")
                .latitude(37.5219).longitude(127.0245)
                .startDate(LocalDate.now().minusDays(5)).endDate(LocalDate.now().plusDays(7))
                .imageUrl("https://placehold.co/600x400/C8A96E/white?text=Sulwhasoo")
                .website("https://sulwhasoo.com").hours("평일 12:00~20:00 / 주말 11:00~19:00")
                .build(),
            PopupStore.builder()
                .name("BTS 팝업스토어 서울").brand("HYBE")
                .description("방탄소년단 공식 팝업스토어. 한정판 앨범, 포토카드, 굿즈 판매.")
                .category("엔터테인먼트").region("서울")
                .address("서울특별시 용산구 이태원로 246 한남동")
                .latitude(37.5340).longitude(126.9987)
                .startDate(LocalDate.now().minusDays(2)).endDate(LocalDate.now().plusDays(12))
                .imageUrl("https://placehold.co/600x400/7C3AED/white?text=BTS+Popup")
                .website("https://weverse.io").hours("평일 12:00~21:00 / 주말 11:00~22:00")
                .build()
        );
    }
}
