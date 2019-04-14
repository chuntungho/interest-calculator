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
 * 等额本息：每期还款金额相同，利息逐渐减少，本金逐渐增加。<br>
 * 该方法还款总额是确定的，因每期四舍五入后可能导致与总数不一致，在最后一期作调整。<br>
 * </p>
 * 
 * <p>
 * 每期本金及利息有两种计算方案：<br>
 * 1. 根据已知剩余本金及利率计算当月利息（四舍五入保留2位小数）<br>
 * 2. 根据本金公式算出应还本金（四舍五入保留2位小数），减后得到利息，可能会与实际应还利息不一致<br>
 * </p>
 * <p>
 * 每期计算公式: <br>
 * （本息）BX = a * i * (1 + i)^N / [(1 + i)^N - 1] <br>
 * （利息）X = a * 剩余本金 （本金）B = BX - B
 * </p>
 * 
 * @author Tony Ho
 *
 */
public class AverageCapitalPlusInterestCalculator extends AbstractCalculator {
	private final static BigDecimal ONE = BigDecimal.valueOf(1);
	protected final static BigDecimal TWELVE = BigDecimal.valueOf(12);

	@Override
	public CalcResultDTO calculateInterest(BigDecimal capital, BigDecimal apr, int duration) {
		if (!checkArguments(capital, apr, duration)) {
			return null;
		}

		BigDecimal totalCapital = capital.setScale(2, RoundingMode.HALF_UP);
		// 月利率使用Double构造，保留精度
		BigDecimal monthRate = new BigDecimal(apr.doubleValue() / (12 * 100));

		// 根据公式先算出每期平均金额，保留4位小数
		// 分子：a * i * (1 + i)^N
		BigDecimal numerator = totalCapital.multiply(monthRate).multiply(ONE.add(monthRate).pow(duration));
		// 分母：[(1 + i)^N - 1]
		BigDecimal denominator = ONE.add(monthRate).pow(duration).subtract(ONE);
		BigDecimal averageRepayment = numerator.divide(denominator, 4, RoundingMode.HALF_UP);

		logger.debug("capital: {}, apr: {}, month rate: {}, average repayment: {}",
				new Object[] { capital, apr, monthRate, averageRepayment });

		// 总还款，保留2位小数
		BigDecimal totalRepayment = averageRepayment.multiply(BigDecimal.valueOf(duration)).setScale(2,
				RoundingMode.HALF_UP);
		BigDecimal totalInterest = totalRepayment.subtract(totalCapital);

		List<CalcItemDTO> list = new ArrayList<CalcItemDTO>(duration);
		BigDecimal remainingRepayment = totalRepayment;
		BigDecimal remainingCapital = totalCapital;
		for (int i = 1; i <= duration; i++) {
			BigDecimal monthCapital = null;
			BigDecimal monthInterest = null;
			// 默认每期还款相同，保留2位小数
			BigDecimal monthRepayment = averageRepayment.setScale(2, RoundingMode.HALF_UP);

			// 最后一期摊销还款
			if (i == duration) {
				monthRepayment = remainingRepayment;
				monthCapital = remainingCapital;
				monthInterest = monthRepayment.subtract(monthCapital);
			} else {
				// 方案1，先算已知利息（四舍五入保留2位小数）
				monthInterest = remainingCapital.multiply(monthRate).setScale(2, RoundingMode.HALF_UP);
				// 再算本金，应还本金 = 应还金额 - 当期利息
				monthCapital = monthRepayment.subtract(monthInterest);

				// 方案2，根据公式算本金（期数多会有性能问题，不推荐），四舍五入保留2位小数
				// BigDecimal monthNumerator = totalCapital.multiply(monthRate)
				// .multiply(ONE.add(monthRate).pow(i - 1));
				// monthCapital = monthNumerator.divide(denominator, 2,
				// RoundingMode.HALF_UP);
			}

			// 剩余应还款/本金
			remainingRepayment = remainingRepayment.subtract(monthRepayment);
			remainingCapital = remainingCapital.subtract(monthCapital);

			// 校验数据：还款金额 = 应还本金 + 当期利息
			assert (monthRepayment.subtract(monthInterest).compareTo(monthCapital) == 0);

			CalcItemDTO item = new CalcItemDTO();
			item.setIndex(i);
			item.setAmount(monthRepayment);
			item.setCapital(monthCapital);
			item.setInterest(monthInterest);

			list.add(item);

			logger.debug("{}. capital: {}, interest: {}, repayment: {}, remaining capital: {}", new Object[] {
					item.getIndex(), item.getCapital(), item.getInterest(), item.getAmount(), remainingCapital });
		}

		logger.debug("total capital: {}, total interest: {}, total repayment: {}",
				new Object[] { totalCapital, totalInterest, totalRepayment });

		CalcResultDTO result = new CalcResultDTO();
		result.setRepaymentItems(list);
		result.setTotalCapital(totalCapital);
		result.setTotalInterest(totalInterest);
		result.setTotalAmount(totalRepayment);
		result.setApr(apr);

		return result;
	}

}