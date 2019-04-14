package com.chuntung.tool.util.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CalculatorFactory {
    private final static Logger logger = LoggerFactory.getLogger(CalculatorFactory.class);

    private static Object lock = new Object();
    private static Map<String, Calculator> cache = new HashMap<>();

    public static Calculator getCalculator(CalculationMethodEnum method) {
        Calculator calculator = null;

        if (cache.containsKey(method.toString())) {
            calculator = cache.get(method.toString());
        } else {
            synchronized (lock) {
                // check again
                calculator = cache.get(method.toString());
                if (calculator == null) {
                    try {
                        calculator = method.getCalculatorClass().newInstance();
                        cache.put(method.toString(), calculator);
                    } catch (InstantiationException e) {
                        logger.error("Failed to get calculator", e);
                    } catch (IllegalAccessException e) {
                        logger.error("Failed to get calculator", e);
                    }
                }
            }
        }

        return calculator;
    }
}