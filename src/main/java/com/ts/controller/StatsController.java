package com.ts.controller;

import com.ts.common.ApiResponse;
import com.ts.service.StatsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/admin/stats")
public class StatsController {

    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public ApiResponse<?> stats() {
        return ApiResponse.ok(statsService.getOverviewStats());
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export() {
        String filename = "travel-stats-" + LocalDate.now().format(FILE_DATE_FORMATTER) + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(statsService.exportOverviewCsv());
    }
}
