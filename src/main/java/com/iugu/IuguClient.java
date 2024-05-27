package com.iugu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.iugu.domain.request.RequestWithdrawRequest;
import com.iugu.domain.request.TransferRequest;
import com.iugu.domain.response.RequestWithdrawResponse;
import com.iugu.domain.response.TransferResponse;
import com.iugu.interceptor.BearerRequestInterceptor;
import com.iugu.interceptor.RsaRequestInterceptor;
import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Objects;

/**
 * @author italobrunos
 */
public class IuguClient implements Iugu {

	private final String url;
	private Retryer retryer = Retryer.NEVER_RETRY;
	private Logger.Level logLevel = Logger.Level.BASIC;
	private String apiToken;
	private RSAPrivateKey privateKey;
	private final Encoder encoder;
	private final Decoder decoder;
	private final ObjectMapper objectMapper;

	public IuguClient(String url) {
		this.url = url;
		this.encoder = new JacksonEncoder();
		this.decoder = new JacksonDecoder();
		this.objectMapper = new ObjectMapper()
				.setSerializationInclusion(JsonInclude.Include.NON_NULL)
				.configure(SerializationFeature.INDENT_OUTPUT, true);
	}

	public static IuguClient getProductionInstance() {
		return new IuguClient("https://api.iugu.com");
	}

	private Iugu getClient(RequestInterceptor... requestInterceptors) {
		final Feign.Builder builder = Feign.builder()
				.encoder(encoder)
				.decoder(decoder)
				.retryer(retryer)
				.logLevel(logLevel);

		final RequestInterceptor bearerRequestInterceptor = createBearerRequestInterceptor();
		if (Objects.nonNull(bearerRequestInterceptor)) {
			builder.requestInterceptor(bearerRequestInterceptor);
		}

		for (RequestInterceptor requestInterceptor : requestInterceptors) {
			if (Objects.nonNull(requestInterceptor)) {
				builder.requestInterceptor(requestInterceptor);
			}
		}

		return builder.target(Iugu.class, url);
	}

	public IuguClient withRetryer(Retryer retryer) {
		this.retryer = retryer;
		return this;
	}

	public IuguClient withLogLevel(Logger.Level logLevel) {
		this.logLevel = logLevel;
		return this;
	}

	public IuguClient withApiToken(String apiToken) {
		this.apiToken = apiToken;
		return this;
	}

	public IuguClient withPrivateKey(String privateKey) throws InvalidKeySpecException {
		try {
			final String privateKeyInline = privateKey
					.replaceAll("-----BEGIN PRIVATE KEY-----", "")
					.replaceAll("-----END PRIVATE KEY-----", "")
					.replaceAll("\r", "")
					.replaceAll("\n", "");
			final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyInline));
			final KeyFactory kf = KeyFactory.getInstance("RSA");
			this.privateKey = (RSAPrivateKey) kf.generatePrivate(spec);
			return this;
		} catch (NoSuchAlgorithmException e) {
			throw new InvalidKeySpecException(e);
		}
	}

	private void deleteApiKey() {
		this.apiToken = null;
	}

	/**
	 * Uma vez que o requestInterceptor é criado, a apiKey é deletada para evitar uma segunda chamada errada,
	 * utilizando a mesma instância do client. Isso força a utilização do método @withApiKey sempre que uma
	 * chamada for feita.
	 *
	 * @return RequestInterceptor
	 */
	private RequestInterceptor createBearerRequestInterceptor() {
		BearerRequestInterceptor bearerRequestInterceptor = null;
		if (Objects.nonNull(this.apiToken)) {
			bearerRequestInterceptor = new BearerRequestInterceptor(this.apiToken);
			deleteApiKey();
		}
		return bearerRequestInterceptor;
	}

	private RequestInterceptor createRsaRequestInterceptor(Request.HttpMethod method,
														   String endpoint,
														   Object body)
			throws SignatureException {
		RsaRequestInterceptor rsaRequestInterceptor = null;
		if (Objects.nonNull(apiToken) && Objects.nonNull(privateKey)) {
			final String requestTimeAsString = createRequestTimeAsString();
			final String bodySigned = sign(method, endpoint, requestTimeAsString, body);
			rsaRequestInterceptor = new RsaRequestInterceptor(bodySigned, requestTimeAsString);
		}
		return rsaRequestInterceptor;
	}

	private String createRequestTimeAsString() {
		return DateTimeFormatter
				.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
				.format(ZonedDateTime.now(ZoneOffset.ofHours(-3)));
	}

	private String sign(Request.HttpMethod method,
						String endpoint,
						String requestTime,
						Object body)
			throws SignatureException {

		try {
			final String pattern = String.format("%s|%s\n%s|%s\n%s",
					method,
					endpoint,
					apiToken,
					requestTime,
					objectMapper.writeValueAsString(body)
			);

			final Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initSign(privateKey);
			signature.update(pattern.getBytes(StandardCharsets.UTF_8));

			return Base64.getEncoder().encodeToString(signature.sign());

		} catch (NoSuchAlgorithmException | InvalidKeyException | JsonProcessingException e) {
			throw new SignatureException(e);
		}
	}

	@Override
	public Object validateSignature(Object body) throws SignatureException {
		final String endpoint = "/v1/signature/validate";
		final RequestInterceptor rsaRequestInterceptor = createRsaRequestInterceptor(
				Request.HttpMethod.POST,
				endpoint,
				body
		);
		final Iugu iugu = getClient(rsaRequestInterceptor);
		return iugu.validateSignature(body);
	}

	@Override
	public RequestWithdrawResponse requestWithdraw(RequestWithdrawRequest request,
												   String accountId) throws SignatureException {
		final String endpoint = String.format("/v1/accounts/%s/request_withdraw", accountId);
		final RequestInterceptor rsaRequestInterceptor = createRsaRequestInterceptor(
				Request.HttpMethod.POST,
				endpoint,
				request
		);
		final Iugu iugu = getClient(rsaRequestInterceptor);
		return iugu.requestWithdraw(request, accountId);
	}

	@Override
	public TransferResponse transfer(TransferRequest transferRequest) throws SignatureException {
		final String endpoint = "/v1/transfers";
		final RequestInterceptor rsaRequestInterceptor = createRsaRequestInterceptor(
				Request.HttpMethod.POST,
				endpoint,
				transferRequest
		);
		final Iugu iugu = getClient(rsaRequestInterceptor);
		return iugu.transfer(transferRequest);
	}
}
