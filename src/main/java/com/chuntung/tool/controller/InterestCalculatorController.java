package com.chuntung.tool.controller;

import com.chuntung.tool.dto.CalcReq;
import com.chuntung.tool.dto.Response;
import com.chuntung.tool.util.calculator.CalcResultDTO;
import com.chuntung.tool.util.calculator.Calculator;
import com.chuntung.tool.util.calculator.CalculatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class InterestCalculatorController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping(value = "/calculate", method = RequestMethod.POST)
	@Cacheable(cacheNames = "calcCache")
	public Response<CalcResultDTO> calculate(@Valid CalcReq req) {
		logger.info("req {}", req);

		Calculator calculator = CalculatorFactory.getCalculator(req.getMethod());
		CalcResultDTO result = calculator.calculateInterest(req.getCapital(), req.getApr(), req.getDuration());

		return Response.success(result);
	}

}