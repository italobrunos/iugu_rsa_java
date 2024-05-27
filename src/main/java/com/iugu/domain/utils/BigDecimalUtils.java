package com.iugu.domain.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

/**
 * @author italobrunos
 */
public final class BigDecimalUtils {

	private static final Locale PT_BR = new Locale("pt", "BR");
	private static final DecimalFormat BRAZILIAN_DECIMAL_FORMAT =
			new DecimalFormat("###,###.00", new DecimalFormatSymbols(PT_BR));

	private static final DecimalFormat US_DECIMAL_FORMAT =
			(DecimalFormat) DecimalFormat.getNumberInstance(Locale.US);

	static {
		BRAZILIAN_DECIMAL_FORMAT.setParseBigDecimal(true);
		US_DECIMAL_FORMAT.setParseBigDecimal(true);
	}

	private BigDecimalUtils() {
	}

	public static BigDecimal parseUs(String source) throws ParseException {
		return (BigDecimal) US_DECIMAL_FORMAT.parse(source);
	}

	public static BigDecimal parsePtBr(String source) throws ParseException {
		final String cleanedSource = source.replaceAll("[^\\d.,-]", "");
		return (BigDecimal) BRAZILIAN_DECIMAL_FORMAT.parse(cleanedSource);
	}
}
