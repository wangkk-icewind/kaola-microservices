package com.kaola.marketing.model.vo;

import lombok.Data;
import java.util.List;

@Data
public class DispatchPreviewVO {
    private Integer targetCount;
    private List<String> samplePhones; // 前5条样本手机号（脱敏）
}
