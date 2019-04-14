package com.chuntung.tool.util.calculator;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 还款方式计算结果
 * 
 * @author Tony Ho
 *
 */
@Data
public class CalcResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 总金额
    private BigDecimal totalAmount;

    // 总本金
    private BigDecimal totalInterest;

    // 总利息
    private BigDecimal totalCapital;

    // 年化利率
    private BigDecimal apr;

    // 每期还款列表
    private List<CalcItemDTO> RepaymentItems;

}
