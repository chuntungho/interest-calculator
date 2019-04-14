package com.chuntung.tool.dto;

import com.chuntung.tool.util.calculator.CalculationMethodEnum;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class CalcReq {
    @NotNull(message = "请选择计息方式")
    private CalculationMethodEnum method;

    @NotNull(message = "本金需要大于0")
    @Positive(message = "本金需要大于0")
    private BigDecimal capital;

    @NotNull(message = "年化利率需要大于0")
    @Positive(message = "年化利率需要大于0")
    private BigDecimal apr;

    @NotNull(message = "期数在1-360")
    @Min(value = 1, message = "期数需要大于0")
    @Max(value = 360, message = "期数最大360")
    private Integer duration;

}
