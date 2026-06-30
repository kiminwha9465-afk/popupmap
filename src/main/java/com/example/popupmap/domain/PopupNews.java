package com.example.popupmap.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PopupNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 10000)
    private String content;

    private String tag;

    private String thumbnailUrl;

    private String author;         // 닉네임 (표시용)
    private String authorUsername; // 로그인 아이디 (권한 확인용)

    private Long popupStoreId;    // 연관 팝업스토어 ID (선택)

    private LocalDate publishedAt;

    @Builder.Default
    private int viewCount = 0;
}
