package com.chuntung.tool.util.calculator.impl;

import com.chuntung.tool.util.calculator.AbstractCalculator;
import com.chuntung.tool.util.calculator.CalcItemDTO;
import com.chuntung.tool.util.calculator.CalcResultDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 先息后本：每期只还利息，最后一期还本付息。
 *
 * @author Tony Ho
 */
public class FirstInterestCalculator extends AbstractCalculator {

    @Override
    public CalcResultDTO calculateInterest(BigDecimal capital, BigDecimal apr, int duration) {
        if (!checkArguments(capital, apr, duration)) {
            return null;
        }

        // 本金，保留2位小数
        BigDecimal totalCapital = capital.setScale(2, RoundingMode.HALF_UP);

        // 月利率使用Double构造，保留精度
        BigDecimal monthRate = new BigDecimal(apr.doubleValue() / (12 * 100));
        logger.debug("capital: {}, apr: {}, month rate: {}", capital, apr, monthRate);

        List<CalcItemDTO> list = new ArrayList<CalcItemDTO>(duration);

        // 总利息，保留2位小数
        BigDecimal totalInterest = totalCapital.multiply(monthRate).multiply(BigDecimal.valueOf(duration));
        totalInterest = totalInterest.setScale(2, RoundingMode.HALF_UP);

        // 每月利息，保留2位小数，后面调整
        BigDecimal monthInterest = totalCapital.multiply(monthRate).setScale(2, RoundingMode.HALF_UP);
        for (int i = 1; i <= duration; i++) {
            BigDecimal monthCapital = BigDecimal.ZERO;
            if (i == duration) {
                monthCapital = totalCapital;
            }

            CalcItemDTO item = new CalcItemDTO();
            item.setIndex(i);
            item.setCapital(monthCapital);
            item.setInterest(monthInterest);
            item.setAmount(monthCapital.add(item.getInterest()));
            list.add(item);

            logger.debug("{}. capital: {}, interest: {}, repayment: {}",
                    item.getIndex(), item.getCapital(), item.getInterest(), item.getAmount());
        }

        CalcResultDTO result = new CalcResultDTO();
        result.setRepaymentItems(list);
        result.setTotalCapital(totalCapital);
        result.setTotalInterest(totalInterest);
        result.setTotalAmount(totalCapital.add(result.getTotalInterest()));
        result.setApr(apr);

        logger.debug("total capital: {}, total interest: {}, total repayment: {}",
                result.getTotalCapital(), result.getTotalInterest(), result.getTotalAmount());

        // 调整明细
        roundAndRevise(result);

        return result;
    }

}