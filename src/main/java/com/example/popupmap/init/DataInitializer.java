package com.example.popupmap.init;

import com.example.popupmap.crawler.PopgaCrawlerService;
import com.example.popupmap.domain.PopupNews;
import com.example.popupmap.domain.PopupStore;
import com.example.popupmap.domain.User;
import com.example.popupmap.repository.PopupNewsRepository;
import com.example.popupmap.repository.PopupStoreRepository;
import com.example.popupmap.repository.UserRepository;
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
    private final PopupNewsRepository newsRepository;
    private final PopgaCrawlerService crawlerService;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        // 닉네임 없는 기존 계정 → 아이디를 닉네임으로 설정
        userRepository.findAll().stream()
                .filter(u -> u.getNickname() == null)
                .forEach(u -> {
                    u.setNickname(u.getUsername());
                    userRepository.save(u);
                    log.info("닉네임 마이그레이션: {} → nickname={}", u.getUsername(), u.getUsername());
                });
        try {
            int count = crawlerService.crawlAndSave();
            log.info("팝가 크롤링 완료: {}개 저장", count);
        } catch (Exception e) {
            log.warn("팝가 크롤링 실패, 샘플 데이터로 대체: {}", e.getMessage());
            repository.saveAll(sampleData());
        }
        if (newsRepository.count() == 0) {
            newsRepository.saveAll(sampleNews());
        }
    }

    private List<PopupNews> sampleNews() {
        return List.of(
            PopupNews.builder()
                .title("서울 성수동 팝업스토어 밀집 지역 완벽 가이드")
                .tag("소식")
                .content("성수동은 서울에서 팝업스토어가 가장 활발하게 열리는 지역 중 하나입니다.\n\n뚝섬역부터 성수역 사이의 골목골목에는 패션, 뷰티, 식음료 등 다양한 브랜드의 팝업스토어가 자리잡고 있습니다.\n\n주말에는 하루에만 수천 명이 방문할 정도로 인기 있는 명소가 되었습니다.")
                .publishedAt(LocalDate.of(2026, 6, 24))
                .build(),
            PopupNews.builder()
                .title("이번 주말 놓치면 안 될 팝업스토어 TOP 5")
                .tag("소식")
                .content("이번 주말 꼭 방문해야 할 팝업스토어 5곳을 소개합니다.\n\n1. 마팽킴 전시 - NEW WORDS, NEW WORTH\n2. 스텔라이브 팝업 - SIX STAR STELLIVE\n3. 귀멸의 칼날 전시 - 전집중展\n4. 너무착한데? & 틀린 건 아닌데 전시 @종로\n5. 진격의 거인 전시")
                .publishedAt(LocalDate.of(2026, 6, 22))
                .build(),
            PopupNews.builder()
                .title("2026 상반기 팝업스토어 트렌드 분석")
                .tag("트렌드")
                .content("2026년 상반기 팝업스토어 시장을 분석해보면 몇 가지 주요 트렌드가 눈에 띕니다.\n\n첫째, IP(지식재산권) 기반 팝업스토어의 급증입니다. 애니메이션, 게임, 아이돌 등 기존 팬덤을 보유한 IP를 활용한 팝업스토어가 큰 인기를 끌었습니다.\n\n둘째, 체험형 콘텐츠의 강화입니다. 단순 전시나 판매를 넘어 참여형 인터랙티브 콘텐츠를 제공하는 팝업스토어가 늘었습니다.")
                .publishedAt(LocalDate.of(2026, 6, 20))
                .build(),
            PopupNews.builder()
                .title("팝업스토어 예약 꿀팁 및 웨이팅 줄이는 방법")
                .tag("후기")
                .content("인기 팝업스토어는 오픈 전부터 줄이 길게 늘어서는 경우가 많습니다.\n\n사전 예약 시스템을 운영하는 곳은 반드시 사전 예약을 활용하세요.\n\n평일 오전 시간대를 노리면 대기 시간을 크게 줄일 수 있습니다.\n\n공식 SNS를 팔로우하여 실시간 혼잡도 정보를 확인하는 것도 좋은 방법입니다.")
                .publishedAt(LocalDate.of(2026, 6, 18))
                .build(),
            PopupNews.builder()
                .title("7월 오픈 예정 팝업스토어 미리보기")
                .tag("소식")
                .content("7월에 오픈 예정인 팝업스토어들을 미리 살펴봅니다.\n\n다양한 브랜드들이 여름 시즌을 맞아 특별한 팝업스토어를 준비 중입니다.\n\n자세한 일정과 위치는 팝업맵에서 확인하세요!")
                .publishedAt(LocalDate.of(2026, 6, 15))
                .build()
        );
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
