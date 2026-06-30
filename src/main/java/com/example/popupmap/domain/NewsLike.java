package com.example.popupmap.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"news_id", "username"}))
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class NewsLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "news_id", nullable = false)
    private Long newsId;

    @Column(nullable = false)
    private String username;
}
