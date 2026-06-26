package com.example.popupmap.crawler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PopgaApiResponse {

    private List<PopgaItem> data;
    private int totalCount;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PopgaItem {
        private Long id;
        private String title;
        private String imagePath;
        private String openDate;
        private String closeDate;
        private String area;
        private String categoryName;
    }
}
