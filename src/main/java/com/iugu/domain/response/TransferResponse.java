package com.iugu.domain.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.iugu.domain.Account;
import com.iugu.domain.CustomVariable;
import com.iugu.domain.deserializer.BigDecimalDeserializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @author italobrunos
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransferResponse {

	private String id;
	private String createdAt;
	private BigInteger amountCents;

	@JsonDeserialize(using = BigDecimalDeserializer.class)
	private BigDecimal amountLocalized;

	private String updatedAt;
	private Account receiver;
	private Account sender;
	private List<CustomVariable> customVariables;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public BigInteger getAmountCents() {
		return amountCents;
	}

	public void setAmountCents(BigInteger amountCents) {
		this.amountCents = amountCents;
	}

	public BigDecimal getAmountLocalized() {
		return amountLocalized;
	}

	public void setAmountLocalized(BigDecimal amountLocalized) {
		this.amountLocalized = amountLocalized;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Account getReceiver() {
		return receiver;
	}

	public void setReceiver(Account receiver) {
		this.receiver = receiver;
	}

	public Account getSender() {
		return sender;
	}

	public void setSender(Account sender) {
		this.sender = sender;
	}

	public List<CustomVariable> getCustomVariables() {
		return customVariables;
	}

	public void setCustomVariables(List<CustomVariable> customVariables) {
		this.customVariables = customVariables;
	}
}
