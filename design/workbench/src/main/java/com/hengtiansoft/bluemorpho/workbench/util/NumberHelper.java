package com.hengtiansoft.bluemorpho.workbench.util;

import java.math.BigDecimal;

/**
 * @Description: 目前主要是double类型数据的加减乘除
 * @author gaochaodeng
 * @date Aug 30, 2018
 */
public class NumberHelper {

	public static double add(double a1, double b1) {
		BigDecimal a2 = new BigDecimal(Double.toString(a1));
		BigDecimal b2 = new BigDecimal(Double.toString(b1));
		return a2.add(b2).doubleValue();
	}

	public static double sub(double a1, double b1) {
		BigDecimal a2 = new BigDecimal(Double.toString(a1));
		BigDecimal b2 = new BigDecimal(Double.toString(b1));
		return a2.subtract(b2).doubleValue();
	}

	public static double mul(double a1, double b1) {
		BigDecimal a2 = new BigDecimal(Double.toString(a1));
		BigDecimal b2 = new BigDecimal(Double.toString(b1));
		return a2.multiply(b2).doubleValue();
	}

	public static double div(double a1, double b1, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("error");
		}
		BigDecimal a2 = new BigDecimal(Double.toString(a1));
		BigDecimal b2 = new BigDecimal(Double.toString(b1));
		return a2.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static double div(double a1, double b1) {
		BigDecimal a2 = new BigDecimal(Double.toString(a1));
		BigDecimal b2 = new BigDecimal(Double.toString(b1));
		return a2.divide(b2).doubleValue();
	}
}
