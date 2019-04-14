package com.chuntung.tool.util.calculator;


import java.math.BigDecimal;

public interface Calculator {
	CalcResultDTO calculateInterest(BigDecimal capital, BigDecimal apr, int duration);
}
