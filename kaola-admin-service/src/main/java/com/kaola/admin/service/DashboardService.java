package com.kaola.admin.service;

import java.util.Map;

/**
 * 仪表盘服务接口
 *
 * @author Kaola Team
 */
public interface DashboardService {

    /**
     * 获取仪表盘数据
     * @return 仪表盘数据
     */
    Map<String, Object> getDashboardData();
}
