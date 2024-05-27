package com.iugu.domain.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.iugu.domain.utils.BigDecimalUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;

/**
 * @author italobrunos
 */
public class BigDecimalDeserializer extends StdDeserializer<BigDecimal> {

	public BigDecimalDeserializer() {
		this(null);
	}

	protected BigDecimalDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
			throws IOException {
		final String source = jsonParser.getText();
		return parse(source);
	}

	private BigDecimal parse(String source) throws IOException {
		try {
			return BigDecimalUtils.parseUs(source);
		} catch (ParseException e) {
			try {
				return BigDecimalUtils.parsePtBr(source);
			} catch (ParseException ex) {
				throw new IOException(ex);
			}
		}
	}
}
