package com.iugu.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.Base64;

/**
 * @author italobrunos
 */
public class BearerRequestInterceptor implements RequestInterceptor {

	private final String apiToken;

	public BearerRequestInterceptor(String apiToken) {
		this.apiToken = apiToken;
	}

	@Override
	public void apply(RequestTemplate template) {
		final String apiTokenBase64 = Base64.getEncoder().encodeToString(apiToken.getBytes());
		template.header("Authorization", String.format("Bearer %s", apiTokenBase64));
	}
}
