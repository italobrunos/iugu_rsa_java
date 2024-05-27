package com.iugu.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author italobrunos
 */
public class RsaRequestInterceptor implements RequestInterceptor {

	private final String signature;
	private final String requestTime;

	public RsaRequestInterceptor(String signature,
								 String requestTime) {
		this.signature = signature;
		this.requestTime = requestTime;
	}

	@Override
	public void apply(RequestTemplate template) {
		template.header("Signature", String.format("signature=%s", signature));
		template.header("Request-Time", requestTime);
	}
}
