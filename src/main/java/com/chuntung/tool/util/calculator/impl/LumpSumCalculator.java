package com.chuntung.tool.util.calculator.impl;

import com.chuntung.tool.util.calculator.AbstractCalculator;
import com.chuntung.tool.util.calculator.CalcItemDTO;
import com.chuntung.tool.util.calculator.CalcResultDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 一次性还款：到期连本带息还款。<br>
 *
 * @author Tony Ho
 *
 */
public class LumpSumCalculator extends AbstractCalculator {

	@Override
	public CalcResultDTO calculateInterest(BigDecimal capital, BigDecimal apr, int duration) {
		if (!checkArguments(capital, apr, duration)) {
			return null;
		}

		BigDecimal totalCapital = capital.setScale(2, RoundingMode.HALF_UP);
		BigDecimal monthRate = new BigDecimal(apr.doubleValue() / (12 * 100));
		BigDecimal totalInterest = totalCapital.multiply(monthRate).multiply(BigDecimal.valueOf(duration));
		totalInterest = totalInterest.setScale(2, RoundingMode.HALF_UP);

		// 只有一条记录
		CalcItemDTO item = new CalcItemDTO();
		item.setIndex(1);
		item.setCapital(totalCapital);
		item.setInterest(totalInterest);
		item.setAmount(totalCapital.add(totalInterest));

		List<CalcItemDTO> list = new ArrayList<CalcItemDTO>();
		list.add(item);

		CalcResultDTO result = new CalcResultDTO();
		result.setRepaymentItems(list);
		result.setTotalCapital(totalCapital);
		result.setTotalInterest(totalInterest);
		result.setTotalAmount(totalCapital.add(totalInterest));
		result.setApr(apr);

		logger.debug("total capital: {}, apr: {}, total interest: {}, total repayment: {}",
				new Object[] { result.getTotalCapital(), apr, result.getTotalInterest(), result.getTotalAmount() });

		return result;
	}

}
