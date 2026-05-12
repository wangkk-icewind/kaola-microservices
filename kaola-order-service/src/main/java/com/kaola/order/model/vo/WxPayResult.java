package com.kaola.order.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信 JSAPI 调起支付参数（返回给小程序）
 * package 是 Java 关键字，通过 @JsonProperty 映射为 "package"
 */
@Data
public class WxPayResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appId;
    private String timeStamp;
    private String nonceStr;

    @JsonProperty("package")
    private String packageValue;

    private String signType;
    private String paySign;
}
