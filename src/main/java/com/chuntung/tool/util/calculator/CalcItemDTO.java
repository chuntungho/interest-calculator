package com.chuntung.tool.util.calculator;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 计算出的每期还款本金
 * 
 * @author Tony Ho
 *
 */
@Data
public class CalcItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer index;
    private BigDecimal amount;
    private BigDecimal capital;
    private BigDecimal interest;

}
