package com.chuntung.tool.controller;

import com.chuntung.tool.dto.CalcReq;
import com.chuntung.tool.dto.Response;
import com.chuntung.tool.util.calculator.CalcResultDTO;
import com.chuntung.tool.util.calculator.CalculationMethodEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InterestCalculatorControllerTest {

    @Resource
    InterestCalculatorController interestCalculatorController;

    @Test
    public void testCalc(){
        CalcReq req = new CalcReq();
        req.setMethod(CalculationMethodEnum.AVERAGE_CAPITAL);
        req.setCapital(BigDecimal.valueOf(10000));
        req.setApr(new BigDecimal("12.5"));
        req.setDuration(12);

        Response<CalcResultDTO> resp = interestCalculatorController.calculate(req);
        Assert.assertNotNull(resp.getData());
    }
}