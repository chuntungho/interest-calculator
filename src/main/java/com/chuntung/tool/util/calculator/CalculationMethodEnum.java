package com.chuntung.tool.util.calculator;

import com.chuntung.tool.util.calculator.impl.AverageCapitalCalculator;
import com.chuntung.tool.util.calculator.impl.AverageCapitalPlusInterestCalculator;
import com.chuntung.tool.util.calculator.impl.FirstInterestCalculator;
import com.chuntung.tool.util.calculator.impl.LumpSumCalculator;

public enum CalculationMethodEnum {
    AVERAGE_CAPITAL(AverageCapitalCalculator.class),
    AVERAGE_CAPITAL_PLUS_INTEREST(AverageCapitalPlusInterestCalculator.class),
    FIRST_INTEREST(FirstInterestCalculator.class),
    LUMP_SUM(LumpSumCalculator.class),
    ;

    private CalculationMethodEnum(Class<? extends Calculator> calculatorClazz) {
        this.clazz = calculatorClazz;
    }

    private Class<? extends Calculator> clazz;

    public Class<? extends Calculator> getCalculatorClass() {
        return this.clazz;
    }
}
