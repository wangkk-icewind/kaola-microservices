package com.kaola.marketing.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_coupon_dispatch")
public class CouponDispatch {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("coupon_id")
    private Long couponId;
    @TableField("dispatch_name")
    private String dispatchName;
    @TableField("rules")
    private String rules; // JSON
    @TableField("phone_list")
    private String phoneList; // comma-separated phones
    @TableField("target_count")
    private Integer targetCount;
    @TableField("sent_count")
    private Integer sentCount;
    @TableField("status")
    private Integer status; // 0=draft 1=executing 2=done 3=failed
    @TableField("sms_notify")
    private Integer smsNotify;
    @TableField("operator")
    private String operator;
    @TableField("remark")
    private String remark;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
    @TableField("deleted")
    private Integer deleted;
}
