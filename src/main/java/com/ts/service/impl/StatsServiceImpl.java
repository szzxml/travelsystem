package com.ts.service.impl;

import com.ts.repository.AttractionRepository;
import com.ts.repository.OrderRepository;
import com.ts.repository.TourRouteRepository;
import com.ts.repository.UserRepository;
import com.ts.service.StatsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class StatsServiceImpl implements StatsService {

    private static final DateTimeFormatter CSV_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final OrderRepository orderRepository;
    private final TourRouteRepository routeRepository;
    private final AttractionRepository attractionRepository;
    private final UserRepository userRepository;

    public StatsServiceImpl(OrderRepository orderRepository,
                            TourRouteRepository routeRepository,
                            AttractionRepository attractionRepository,
                            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.routeRepository = routeRepository;
        this.attractionRepository = attractionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, Object> getOverviewStats() {
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalRoutes", routeRepository.count());
        result.put("totalAttractions", attractionRepository.count());
        result.put("totalUsers", userRepository.count());
        result.put("totalOrders", orderRepository.count());
        result.put("todayOrders", orderRepository.countByCreatedAtAfter(todayStart));
        result.put("pendingOrders", orderRepository.countPending());
        result.put("totalRevenue", orderRepository.sumCompletedAmount());
        return result;
    }

    @Override
    public byte[] exportOverviewCsv() {
        Map<String, Object> stats = getOverviewStats();
        String generatedAt = LocalDateTime.now().format(CSV_TIME_FORMATTER);

        StringBuilder csv = new StringBuilder();
        csv.append('\uFEFF');
        csv.append("统计时间,线路总数,景点总数,用户总数,订单总数,今日订单数,待处理订单数,总营收").append('\n');
        csv.append(csvValue(generatedAt)).append(',')
                .append(csvValue(stats.get("totalRoutes"))).append(',')
                .append(csvValue(stats.get("totalAttractions"))).append(',')
                .append(csvValue(stats.get("totalUsers"))).append(',')
                .append(csvValue(stats.get("totalOrders"))).append(',')
                .append(csvValue(stats.get("todayOrders"))).append(',')
                .append(csvValue(stats.get("pendingOrders"))).append(',')
                .append(csvValue(stats.get("totalRevenue"))).append('\n');

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String csvValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.stripTrailingZeros().toPlainString();
        }
        String text = String.valueOf(value).replace("\"", "\"\"");
        return '"' + text + '"';
    }
}
