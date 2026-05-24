package com.ts.service;

import java.util.Map;

public interface StatsService {

    Map<String, Object> getOverviewStats();

    byte[] exportOverviewCsv();
}
