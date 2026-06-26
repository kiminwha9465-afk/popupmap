package com.example.popupmap.crawler;

import com.example.popupmap.domain.PopupStore;
import com.example.popupmap.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopgaCrawlerService {

    private final PopupStoreRepository repository;

    private static final String API_URL =
        "https://api.popga.co.kr/user/v2/home-sections/40/items" +
        "?periodTypes%5B0%5D=IN_PROGRESS&periodTypes%5B1%5D=READY&recommendationType=HOTTEST";

    /** 서버 시작 1시간 후부터 1시간마다 자동 실행 */
    @Scheduled(initialDelay = 3_600_000, fixedDelay = 3_600_000)
    public void scheduledCrawl() {
        try {
            int count = crawlAndSave();
            log.info("[스케줄] 팝가 크롤링 완료: {}개 처리", count);
        } catch (Exception e) {
            log.warn("[스케줄] 팝가 크롤링 실패: {}", e.getMessage());
        }
    }

    public int crawlAndSave() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        headers.set("Referer", "https://www.popga.co.kr");
        headers.set("Accept", "application/json");

        ResponseEntity<PopgaApiResponse> response = restTemplate.exchange(
            URI.create(API_URL), HttpMethod.GET, new HttpEntity<>(headers), PopgaApiResponse.class
        );

        if (response.getBody() == null || response.getBody().getData() == null) return 0;

        List<PopgaApiResponse.PopgaItem> items = response.getBody().getData();
        int count = 0;
        for (PopgaApiResponse.PopgaItem item : items) {
            upsert(item);
            count++;
        }
        return count;
    }

    /** popgaId 기준으로 이미 있으면 업데이트, 없으면 신규 저장 */
    private void upsert(PopgaApiResponse.PopgaItem item) {
        PopupStore store = repository.findByPopgaId(item.getId())
                .orElse(PopupStore.builder().popgaId(item.getId()).build());

        store.setName(item.getTitle());
        store.setBrand(extractBrand(item.getTitle()));
        store.setDescription(item.getTitle());
        store.setCategory(mapCategory(item.getCategoryName()));
        store.setRegion(mapRegion(item.getArea()));
        store.setAddress(mapAddress(item.getArea()));
        store.setLatitude(mapLat(item.getArea()));
        store.setLongitude(mapLng(item.getArea()));
        store.setImageUrl(item.getImagePath());
        store.setHours("매일 10:00~20:00");

        try {
            store.setStartDate(LocalDate.parse(item.getOpenDate()));
            store.setEndDate(LocalDate.parse(item.getCloseDate()));
        } catch (Exception ignored) {}

        repository.save(store);
    }

    private String extractBrand(String title) {
        if (title.contains(" - ")) return title.split(" - ")[0].trim();
        if (title.contains(" @")) return title.split(" @")[0].trim();
        if (title.contains(" X ")) return title.split(" X ")[0].trim();
        return title.length() > 12 ? title.substring(0, 12) : title;
    }

    private String mapCategory(String cat) {
        if (cat == null) return "라이프스타일";
        return switch (cat) {
            case "패션"                      -> "패션";
            case "뷰티"                      -> "뷰티";
            case "F&B"                      -> "식음료";
            case "라이프스타일"               -> "라이프스타일";
            case "애니/캐릭터", "연예인/셀럽",
                 "엔터테인먼트"               -> "엔터테인먼트";
            case "전시", "아트"              -> "아트/문화";
            case "테크"                      -> "테크";
            default                         -> "라이프스타일";
        };
    }

    private String mapRegion(String area) {
        if (area == null) return "서울";
        return switch (area) {
            case "부산" -> "부산";
            case "제주" -> "제주";
            case "수원", "판교", "분당" -> "경기";
            default -> "서울";
        };
    }

    private String mapAddress(String area) {
        if (area == null) return "서울특별시";
        return switch (area) {
            case "성수"  -> "서울특별시 성동구 성수동";
            case "홍대"  -> "서울특별시 마포구 홍대";
            case "강남"  -> "서울특별시 강남구 강남대로";
            case "신촌"  -> "서울특별시 서대문구 신촌로";
            case "여의도" -> "서울특별시 영등포구 여의도동";
            case "명동"  -> "서울특별시 중구 명동";
            case "이태원" -> "서울특별시 용산구 이태원로";
            case "잠실"  -> "서울특별시 송파구 잠실동";
            case "마포"  -> "서울특별시 마포구";
            case "압구정" -> "서울특별시 강남구 압구정로";
            case "건대"  -> "서울특별시 광진구 건대입구";
            case "인사동" -> "서울특별시 종로구 인사동";
            case "판교"  -> "경기도 성남시 분당구 판교";
            case "부산"  -> "부산광역시 해운대구";
            case "제주"  -> "제주특별자치도 제주시";
            default     -> "서울특별시 " + area;
        };
    }

    private Double mapLat(String area) {
        if (area == null) return 37.5665;
        return switch (area) {
            case "성수"  -> 37.5447;
            case "홍대"  -> 37.5520;
            case "강남"  -> 37.4979;
            case "신촌"  -> 37.5596;
            case "여의도" -> 37.5219;
            case "명동"  -> 37.5600;
            case "이태원" -> 37.5340;
            case "잠실"  -> 37.5131;
            case "마포"  -> 37.5551;
            case "압구정" -> 37.5269;
            case "건대"  -> 37.5404;
            case "인사동" -> 37.5743;
            case "판교"  -> 37.3946;
            case "부산"  -> 35.1796;
            case "제주"  -> 33.4890;
            default     -> 37.5665;
        };
    }

    private Double mapLng(String area) {
        if (area == null) return 126.9780;
        return switch (area) {
            case "성수"  -> 127.0557;
            case "홍대"  -> 126.9240;
            case "강남"  -> 127.0276;
            case "신촌"  -> 126.9426;
            case "여의도" -> 126.9245;
            case "명동"  -> 126.9869;
            case "이태원" -> 126.9987;
            case "잠실"  -> 127.1001;
            case "마포"  -> 126.9169;
            case "압구정" -> 127.0405;
            case "건대"  -> 127.0702;
            case "인사동" -> 126.9853;
            case "판교"  -> 127.1103;
            case "부산"  -> 129.0756;
            case "제주"  -> 126.4983;
            default     -> 126.9780;
        };
    }
}
