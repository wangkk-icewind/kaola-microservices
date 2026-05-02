package com.kaola.marketing.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class DispatchRequest {
    private Long couponId;
    private String dispatchName;
    /** 规则列表，每条规则: {"field":"totalSpend","operator":"gte","value":"1000"} */
    private List<DispatchRule> rules;
    /** 指定手机号列表（优先于规则，为空则按规则筛选） */
    private List<String> phoneList;
    private Boolean smsNotify;
    private String remark;

    @Data
    public static class DispatchRule {
        /** 字段: totalSpend/orderCount/storeId/level/customerType */
        private String field;
        /** 运算符: eq/gte/lte/in */
        private String operator;
        /** 值 */
        private String value;
    }
}
