package com.chuntung.tool.util.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Iterator;

public abstract class AbstractCalculator implements Calculator {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final static BigDecimal TWELVE = BigDecimal.valueOf(12);

	// 校验参数
	protected boolean checkArguments(BigDecimal investor_capital, BigDecimal year_apr, int duration) {
		if (investor_capital == null || investor_capital.compareTo(BigDecimal.ZERO) <= 0) {
			logger.warn("equalInterest investor_capital is null.");
			return false;
		}
		if (year_apr == null || year_apr.compareTo(BigDecimal.ZERO) <= 0) {
			logger.warn("equalInterest year_apr is null.");
			return false;
		}

		if (duration < 1) {
			logger.warn("equalInterest duration is null.");
			return false;
		}

		return true;
	}

	/**
	 * 以总数为基准修正明细：还款=本金+利息，总数=每期汇总
	 * 
	 * @param result
	 *            利息计算结果
	 */
	public void roundAndRevise(CalcResultDTO result) {
		BigDecimal remainingInterest = result.getTotalInterest();
		BigDecimal remainingCapital = result.getTotalCapital();
		Iterator<CalcItemDTO> iterator = result.getRepaymentItems().iterator();
		logger.debug("Revise for repayment {} = {} + {}",
				new Object[] { result.getTotalAmount(), result.getTotalCapital(), result.getTotalInterest() });
		for (; iterator.hasNext();) {
			CalcItemDTO item = iterator.next();

			// 最后一期（或该期本金/利息大于剩余值），则使用剩余值
			BigDecimal stageInterest = item.getInterest();
			if (!iterator.hasNext() || stageInterest.compareTo(remainingInterest) > 0) {
				stageInterest = remainingInterest;
			}
			BigDecimal stageCapital = item.getCapital();
			if (!iterator.hasNext() || stageCapital.compareTo(remainingCapital) > 0) {
				stageCapital = remainingCapital;
			}

			// 修正明细
			BigDecimal stageAmount = stageCapital.add(stageInterest);
			item.setCapital(stageCapital);
			item.setInterest(stageInterest);
			item.setAmount(stageAmount);

			remainingInterest = remainingInterest.subtract(stageInterest);
			remainingCapital = remainingCapital.subtract(stageCapital);

			logger.debug("{}. capital: {}, interest: {}, repayment: {}",
					new Object[] { item.getIndex(), item.getCapital(), item.getInterest(), item.getAmount() });
		}
	}
}
