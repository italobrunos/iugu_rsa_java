package com.iugu.domain.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.iugu.domain.CustomVariable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author italobrunos
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransferRequest {

	private String receiverId;
	private BigInteger amountCents;
	private List<CustomVariable> customVariables;

	public TransferRequest() {
		this.customVariables = new ArrayList<>();
	}

	public TransferRequest addCustomVariable(CustomVariable customVariable) {
		if (Objects.isNull(this.customVariables)) {
			this.customVariables = new ArrayList<>();
		}
		this.customVariables.add(customVariable);
		return this;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public BigInteger getAmountCents() {
		return amountCents;
	}

	public void setAmountCents(BigInteger amountCents) {
		this.amountCents = amountCents;
	}

	public List<CustomVariable> getCustomVariables() {
		return customVariables;
	}

	public void setCustomVariables(List<CustomVariable> customVariables) {
		this.customVariables = customVariables;
	}
}
