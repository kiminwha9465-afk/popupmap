package com.example.popupmap.controller;

import com.example.popupmap.domain.PopupStore;
import com.example.popupmap.service.PopupStoreService;
import com.example.popupmap.service.UserBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class NotificationControllerAdvice {

    private final UserBookmarkService bookmarkService;
    private final PopupStoreService storeService;

    @ModelAttribute
    public void addBellNotifications(Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("bellCount", 0);
            model.addAttribute("bellItems", Collections.emptyList());
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(3);

        List<Map<String, Object>> bellItems = bookmarkService.getBookmarks(principal.getName()).stream()
                .map(bm -> storeService.getById(bm.getStoreId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(s -> s.getEndDate() != null
                        && !s.getEndDate().isBefore(today)
                        && !s.getEndDate().isAfter(limit))
                .sorted(Comparator.comparing(PopupStore::getEndDate))
                .map(s -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", s.getId());
                    m.put("name", s.getName());
                    m.put("dday", today.until(s.getEndDate(), ChronoUnit.DAYS));
                    return m;
                })
                .collect(Collectors.toList());

        model.addAttribute("bellItems", bellItems);
        model.addAttribute("bellCount", bellItems.size());
    }
}
