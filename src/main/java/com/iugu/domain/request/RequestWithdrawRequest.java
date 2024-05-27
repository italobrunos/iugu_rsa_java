package com.iugu.domain.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.iugu.domain.CustomVariable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author italobrunos
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RequestWithdrawRequest {

	private BigDecimal amount;
	private List<CustomVariable> customVariables;

	public RequestWithdrawRequest() {
		this.customVariables = new ArrayList<>();
	}

	public RequestWithdrawRequest addCustomVariable(CustomVariable customVariable) {
		if (Objects.isNull(this.customVariables)) {
			this.customVariables = new ArrayList<>();
		}
		this.customVariables.add(customVariable);
		return this;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public List<CustomVariable> getCustomVariables() {
		return customVariables;
	}

	public void setCustomVariables(List<CustomVariable> customVariables) {
		this.customVariables = customVariables;
	}
}
