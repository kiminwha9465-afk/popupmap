package com.example.popupmap.init;

import com.example.popupmap.domain.PopupStore;
import com.example.popupmap.repository.PopupStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PopupStoreRepository repository;

    @Override
    public void run(String... args) {
        List<PopupStore> samples = List.of(
            PopupStore.builder()
                .name("나이키 에어맥스 팝업")
                .brand("Nike")
                .description("나이키 에어맥스 컬렉션을 직접 체험할 수 있는 특별 팝업스토어. 한정판 스니커즈 및 의류를 선착순 구매 가능합니다.")
                .category("패션")
                .region("서울")
                .address("서울특별시 성동구 아차산로17길 48 성수동")
                .latitude(37.5447)
                .longitude(127.0557)
                .startDate(LocalDate.now().minusDays(3))
                .endDate(LocalDate.now().plusDays(10))
                .imageUrl("https://placehold.co/600x400/FF6B35/white?text=Nike+Popup")
                .website("https://nike.com")
                .hours("평일 11:00~21:00 / 주말 10:00~22:00")
                .build(),

            PopupStore.builder()
                .name("무신사 스탠다드 성수")
                .brand("무신사")
                .description("무신사 스탠다드 신규 시즌 컬렉션 팝업스토어. 온라인 단독 상품의 실물 확인 및 구매 가능.")
                .category("패션")
                .region("서울")
                .address("서울특별시 성동구 연무장5길 7 성수동")
                .latitude(37.5430)
                .longitude(127.0580)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(14))
                .imageUrl("https://placehold.co/600x400/1A1A2E/white?text=Musinsa")
                .website("https://musinsa.com")
                .hours("매일 10:00~20:00")
                .build(),

            PopupStore.builder()
                .name("설화수 뷰티 하우스")
                .brand("설화수")
                .description("설화수의 전통 한방 뷰티를 경험하는 프리미엄 팝업스토어. 피부 진단 서비스 무료 제공.")
                .category("뷰티")
                .region("서울")
                .address("서울특별시 강남구 도산대로 317 신사동")
                .latitude(37.5219)
                .longitude(127.0245)
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().plusDays(7))
                .imageUrl("https://placehold.co/600x400/C8A96E/white?text=Sulwhasoo")
                .website("https://sulwhasoo.com")
                .hours("평일 12:00~20:00 / 주말 11:00~19:00")
                .build(),

            PopupStore.builder()
                .name("스타벅스 리저브 팝업")
                .brand("Starbucks")
                .description("스타벅스 리저브 한정 메뉴와 굿즈를 경험하는 팝업스토어. 스페셜 커피 체험 프로그램 운영.")
                .category("식음료")
                .region("서울")
                .address("서울특별시 마포구 와우산로29나길 5 홍대")
                .latitude(37.5520)
                .longitude(126.9240)
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(16))
                .imageUrl("https://placehold.co/600x400/00704A/white?text=Starbucks")
                .website("https://starbucks.co.kr")
                .hours("매일 08:00~22:00")
                .build(),

            PopupStore.builder()
                .name("카카오프렌즈 팝업 부산")
                .brand("카카오")
                .description("카카오프렌즈 부산 특별 팝업스토어. 부산 한정 굿즈 및 콜라보 제품 선착순 판매.")
                .category("라이프스타일")
                .region("부산")
                .address("부산광역시 해운대구 해운대해변로 296 해운대")
                .latitude(35.1587)
                .longitude(129.1604)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(20))
                .imageUrl("https://placehold.co/600x400/FFD700/black?text=Kakao+Friends")
                .website("https://kakaofriends.com")
                .hours("매일 10:00~21:00")
                .build(),

            PopupStore.builder()
                .name("현대백화점 아트 팝업")
                .brand("현대백화점")
                .description("신진 작가들의 작품을 직접 구매할 수 있는 아트 팝업마켓. 한정판 프린트 및 오리지널 작품 판매.")
                .category("아트/문화")
                .region("경기")
                .address("경기도 성남시 분당구 황새울로 360 판교현대백화점")
                .latitude(37.3946)
                .longitude(127.1103)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(5))
                .imageUrl("https://placehold.co/600x400/6C5CE7/white?text=Art+Popup")
                .website("https://thehyundai.com")
                .hours("매일 10:30~20:00")
                .build(),

            PopupStore.builder()
                .name("BTS 팝업스토어 서울")
                .brand("HYBE")
                .description("방탄소년단 공식 팝업스토어. 한정판 앨범, 포토카드, 굿즈 판매 및 AR 포토존 운영.")
                .category("엔터테인먼트")
                .region("서울")
                .address("서울특별시 용산구 이태원로 246 한남동")
                .latitude(37.5340)
                .longitude(126.9987)
                .startDate(LocalDate.now().minusDays(2))
                .endDate(LocalDate.now().plusDays(12))
                .imageUrl("https://placehold.co/600x400/7C3AED/white?text=BTS+Popup")
                .website("https://weverse.io")
                .hours("평일 12:00~21:00 / 주말 11:00~22:00")
                .build(),

            PopupStore.builder()
                .name("애플 시즌 체험관")
                .brand("Apple")
                .description("최신 애플 제품을 직접 체험할 수 있는 특별 팝업 체험관. 전문 크리에이터와 함께하는 워크샵 진행.")
                .category("테크")
                .region("서울")
                .address("서울특별시 강남구 테헤란로 521 삼성동 코엑스")
                .latitude(37.5125)
                .longitude(127.0592)
                .startDate(LocalDate.now().plusDays(3))
                .endDate(LocalDate.now().plusDays(17))
                .imageUrl("https://placehold.co/600x400/555555/white?text=Apple")
                .website("https://apple.com/kr")
                .hours("매일 10:00~20:00")
                .build(),

            PopupStore.builder()
                .name("올리브영 뷰티페스타")
                .brand("올리브영")
                .description("올리브영 인기 뷰티 브랜드를 한자리에서 체험하는 대형 팝업 페스타. 할인 혜택 및 샘플 증정.")
                .category("뷰티")
                .region("서울")
                .address("서울특별시 중구 을지로 281 동대문디자인플라자 DDP")
                .latitude(37.5672)
                .longitude(127.0097)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(8))
                .imageUrl("https://placehold.co/600x400/FF69B4/white?text=Olive+Young")
                .website("https://oliveyoung.co.kr")
                .hours("매일 11:00~21:00")
                .build(),

            PopupStore.builder()
                .name("이케아 리빙 팝업")
                .brand("IKEA")
                .description("이케아 신규 컬렉션을 체험하는 도심 팝업. 소형 주거 인테리어 솔루션 및 한정 제품 판매.")
                .category("라이프스타일")
                .region("서울")
                .address("서울특별시 서초구 강남대로 459 신논현역")
                .latitude(37.5044)
                .longitude(127.0255)
                .startDate(LocalDate.now().minusDays(4))
                .endDate(LocalDate.now().plusDays(9))
                .imageUrl("https://placehold.co/600x400/0058A3/white?text=IKEA")
                .website("https://ikea.com/kr")
                .hours("매일 10:00~21:00")
                .build()
        );
        repository.saveAll(samples);
    }
}
