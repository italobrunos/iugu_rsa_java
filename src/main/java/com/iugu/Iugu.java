package com.iugu;

import com.iugu.domain.request.RequestWithdrawRequest;
import com.iugu.domain.request.TransferRequest;
import com.iugu.domain.response.RequestWithdrawResponse;
import com.iugu.domain.response.TransferResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.security.SignatureException;

/**
 * @author italobrunos
 */
@Headers("Content-Type: application/json")
public interface Iugu {

	@RequestLine("POST /v1/signature/validate")
	Object validateSignature(Object body) throws SignatureException;

	@RequestLine("POST /v1/accounts/{accountId}/request_withdraw")
	RequestWithdrawResponse requestWithdraw(RequestWithdrawRequest request,
											@Param("accountId") String accountId) throws SignatureException;

	@RequestLine("POST /v1/transfers")
	TransferResponse transfer(TransferRequest transferRequest) throws SignatureException;
}
