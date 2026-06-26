package com.example.popupmap.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopupStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    private String brand;

    private String category;

    private String region;

    private String address;

    private double latitude;

    private double longitude;

    private LocalDate startDate;

    private LocalDate endDate;

    private String imageUrl;

    private String website;

    private String hours;
}
