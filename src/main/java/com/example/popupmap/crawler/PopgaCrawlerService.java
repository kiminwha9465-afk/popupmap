package com.example.popupmap.crawler;

import com.example.popupmap.domain.PopupStore;
import com.example.popupmap.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PopgaCrawlerService {

    private final PopupStoreRepository repository;

    private static final String API_URL =
        "https://api.popga.co.kr/user/v2/home-sections/40/items" +
        "?periodTypes%5B0%5D=IN_PROGRESS&periodTypes%5B1%5D=READY&recommendationType=HOTTEST";

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

        List<PopupStore> stores = response.getBody().getData().stream()
            .map(this::toPopupStore)
            .toList();

        repository.saveAll(stores);
        return stores.size();
    }

    private PopupStore toPopupStore(PopgaApiResponse.PopgaItem item) {
        return PopupStore.builder()
            .name(item.getTitle())
            .brand(extractBrand(item.getTitle()))
            .description(item.getTitle())
            .category(mapCategory(item.getCategoryName()))
            .region(mapRegion(item.getArea()))
            .address(mapAddress(item.getArea()))
            .latitude(mapLat(item.getArea()))
            .longitude(mapLng(item.getArea()))
            .startDate(LocalDate.parse(item.getOpenDate()))
            .endDate(LocalDate.parse(item.getCloseDate()))
            .imageUrl(item.getImagePath())
            .hours("매일 10:00~20:00")
            .build();
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
            case "패션"          -> "패션";
            case "뷰티"          -> "뷰티";
            case "F&B"          -> "식음료";
            case "라이프스타일"   -> "라이프스타일";
            case "애니/캐릭터",
                 "연예인/셀럽",
                 "엔터테인먼트"   -> "엔터테인먼트";
            case "전시", "아트"  -> "아트/문화";
            case "테크"          -> "테크";
            default             -> "라이프스타일";
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
