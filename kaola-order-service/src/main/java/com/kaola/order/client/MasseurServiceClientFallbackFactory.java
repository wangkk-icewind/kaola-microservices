package com.kaola.order.client;

import com.kaola.common.core.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 技师服务降级工厂
 *
 * @author Kaola Team
 */
@Slf4j
@Component
public class MasseurServiceClientFallbackFactory implements FallbackFactory<MasseurServiceClient> {

    @Override
    public MasseurServiceClient create(Throwable cause) {
        log.error("技师服务调用失败，启用降级策略", cause);

        return new MasseurServiceClient() {
            @Override
            public Result<MasseurServiceClient.MasseurVO> getMasseurDetail(Long id) {
                log.warn("技师服务降级：无法获取技师ID={}的详情信息", id);

                // 返回错误响应，让上层服务处理（从缓存读取或返回默认数据）
                return Result.error("技师服务暂时不可用，请稍后重试");
            }
        };
    }
}
