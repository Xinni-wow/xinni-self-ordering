package com.xinni.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderSubmitDTO implements Serializable {

    private int payMethod; // 付款方式
    private String remark; // 备注
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Integer pickupStatus; // 配送状态  1立即送出  0选择具体时间
    private Integer tablewareNumber; // 餐具数量
    private Integer tablewareStatus; // 餐具数量状态  1按餐量提供  0选择具体数量
    private Integer packAmount; // 打包费
    private BigDecimal amount; // 总金额
    private Integer dineType; // 用餐类型 1堂食 2打包
}