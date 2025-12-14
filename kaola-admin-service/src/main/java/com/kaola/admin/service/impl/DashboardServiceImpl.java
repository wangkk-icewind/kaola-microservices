package com.kaola.admin.service.impl;

import com.kaola.admin.mapper.DashboardMapper;
import com.kaola.admin.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 仪表盘服务实现
 *
 * @author Kaola Team
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);
    private static final String DASHBOARD_CACHE_KEY = "dashboard:data";
    private static final int CACHE_TTL = 300; // 5分钟缓存

    private final DashboardMapper dashboardMapper;

    @Autowired
    public DashboardServiceImpl(DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    /**
     * 获取仪表盘数据（带缓存优化）
     * 缓存5分钟，减少数据库查询压力
     */
    @Override
    @Cacheable(value = "dashboardCache", key = "'dashboardData'", unless = "#result == null")
    public Map<String, Object> getDashboardData() {
        long startTime = System.currentTimeMillis();
        Map<String, Object> data = new HashMap<>();

        try {
            // 今日统计 - 4个简单查询，已优化索引
            Integer todayOrders = dashboardMapper.countTodayOrders();
            Double todayIncome = dashboardMapper.getTodayIncome();
            Integer todayUsers = dashboardMapper.countTodayUsers();
            Integer todayAppointments = dashboardMapper.countTodayAppointments();

            data.put("todayOrders", todayOrders != null ? todayOrders : 0);
            data.put("todayIncome", todayIncome != null ? todayIncome : 0.0);
            data.put("todayUsers", todayUsers != null ? todayUsers : 0);
            data.put("todayAppointments", todayAppointments != null ? todayAppointments : 0);

            // 订单趋势（最近7天） - 聚合查询
            List<Map<String, Object>> orderTrend = dashboardMapper.getOrderTrend(7);
            // 确保有7天的数据，没有数据的日期补0
            orderTrend = fillMissingDates(orderTrend, 7);
            data.put("orderTrend", orderTrend);

            // 门店排行 TOP 10 - JOIN查询，已限制LIMIT
            List<Map<String, Object>> storeRanking = dashboardMapper.getStoreRanking(10);
            data.put("storeRanking", storeRanking != null ? storeRanking : new ArrayList<>());

            // 热门项目 TOP 10 - JOIN查询，已限制LIMIT
            List<Map<String, Object>> hotProjects = dashboardMapper.getHotProjects(10);
            data.put("hotProjects", hotProjects != null ? hotProjects : new ArrayList<>());

            long duration = System.currentTimeMillis() - startTime;
            log.info("获取仪表盘数据成功: todayOrders={}, todayIncome={}, 耗时: {}ms",
                     todayOrders, todayIncome, duration);
        } catch (Exception e) {
            log.error("获取仪表盘数据失败", e);
            // 返回默认值
            data.put("todayOrders", 0);
            data.put("todayIncome", 0.0);
            data.put("todayUsers", 0);
            data.put("todayAppointments", 0);
            data.put("orderTrend", new ArrayList<>());
            data.put("storeRanking", new ArrayList<>());
            data.put("hotProjects", new ArrayList<>());
        }

        return data;
    }

    /**
     * 填充缺失的日期数据
     * @param trendData 原始趋势数据
     * @param days 天数
     * @return 补全后的数据
     */
    private List<Map<String, Object>> fillMissingDates(List<Map<String, Object>> trendData, int days) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 创建一个Map用于快速查找
        Map<String, Map<String, Object>> dataMap = new HashMap<>();
        if (trendData != null) {
            for (Map<String, Object> item : trendData) {
                String date = (String) item.get("date");
                if (date != null) {
                    dataMap.put(date, item);
                }
            }
        }

        // 生成最近N天的日期
        Calendar calendar = Calendar.getInstance();
        for (int i = days - 1; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -i);

            String dateStr = String.format("%02d-%02d",
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));

            if (dataMap.containsKey(dateStr)) {
                result.add(dataMap.get(dateStr));
            } else {
                // 补充空数据
                Map<String, Object> emptyData = new HashMap<>();
                emptyData.put("date", dateStr);
                emptyData.put("count", 0);
                emptyData.put("amount", 0.0);
                result.add(emptyData);
            }
        }

        return result;
    }
}
