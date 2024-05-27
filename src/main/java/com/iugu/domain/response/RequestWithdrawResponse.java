package com.iugu.domain.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.iugu.domain.deserializer.BigDecimalDeserializer;

import java.math.BigDecimal;

/**
 * @author italobrunos
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RequestWithdrawResponse {

	private String id;

	@JsonDeserialize(using = BigDecimalDeserializer.class)
	private BigDecimal amount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
