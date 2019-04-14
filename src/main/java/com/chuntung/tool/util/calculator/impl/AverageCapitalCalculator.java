package com.chuntung.tool.util.calculator.impl;

import com.chuntung.tool.util.calculator.AbstractCalculator;
import com.chuntung.tool.util.calculator.CalcItemDTO;
import com.chuntung.tool.util.calculator.CalcResultDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 等额本金：每期还款本金相同，利息逐渐减少。<br>
 * 该方法每期本金四舍五入后可能导致与总数不一致，在最后一期作调整。
 * </p>
 * <p>
 * 计算方法：先算每期本金（保留2位小数），从而得出剩余本金利息（保留2位小数）
 * </p>
 * 
 * @author Tony Ho
 *
 */
public class AverageCapitalCalculator extends AbstractCalculator {
	@Override
	public CalcResultDTO calculateInterest(BigDecimal capital, BigDecimal apr, int duration) {
		if (!checkArguments(capital, apr, duration)) {
			return null;
		}

		BigDecimal totalCapital = capital.setScale(2, RoundingMode.HALF_UP);

		// 月利率使用Double构造，保留精度
		BigDecimal monthRate = new BigDecimal(apr.doubleValue() / (12 * 100));
		BigDecimal averageCapital = totalCapital.divide(BigDecimal.valueOf(duration), 2, RoundingMode.HALF_UP);

		logger.debug("capital: {}, apr: {}, month rate: {}, average capital: {}", new Object[] { capital, apr,
				monthRate, averageCapital });

		BigDecimal remainingCapital = totalCapital;
		BigDecimal totalInterest = BigDecimal.ZERO;
		List<CalcItemDTO> list = new ArrayList<CalcItemDTO>(duration);
		for (int i = 1; i <= duration; i++) {
			BigDecimal monthCapital = averageCapital;
			// 最后一期摊销本金
			if (i == duration) {
				monthCapital = remainingCapital;
			}
			BigDecimal monthInterest = remainingCapital.multiply(monthRate).setScale(4, RoundingMode.HALF_UP);

			CalcItemDTO item = new CalcItemDTO();
			item.setIndex(i);
			item.setCapital(monthCapital);
			// 每期利息保留2位小数用于显示，后面再调整
			item.setInterest(monthInterest.setScale(2, RoundingMode.HALF_UP));
			item.setAmount(monthCapital.add(item.getInterest()));

			list.add(item);

			remainingCapital = remainingCapital.subtract(monthCapital);
			totalInterest = totalInterest.add(monthInterest);

			logger.debug("{}. capital: {}, interest: {}, repayment: {}, remaining capital: {}",
					new Object[] { item.getIndex(), item.getCapital(), monthInterest, item.getAmount(),
							remainingCapital });
		}

		CalcResultDTO result = new CalcResultDTO();
		result.setRepaymentItems(list);
		result.setTotalCapital(totalCapital);
		result.setTotalInterest(totalInterest.setScale(2, RoundingMode.HALF_UP));
		result.setTotalAmount(totalCapital.add(result.getTotalInterest()));
		result.setApr(apr);

		logger.debug("total capital: {}, total interest: {}, total repayment: {}",
				new Object[] { result.getTotalCapital(), result.getTotalInterest(), result.getTotalAmount() });

		// 调整明细
		roundAndRevise(result);

		return result;
	}

}