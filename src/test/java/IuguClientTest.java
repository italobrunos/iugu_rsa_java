import com.iugu.IuguClient;
import com.iugu.domain.request.TransferRequest;

import java.math.BigInteger;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author italobrunos
 */
public class IuguClientTest {

	public static void main(String[] args) throws InvalidKeySpecException, SignatureException {
		final String apiToken = "";
		final String privateKey = """
				
				""";

		final TransferRequest transferRequest = new TransferRequest();
		transferRequest.setReceiverId("12345");
		transferRequest.setAmountCents(BigInteger.valueOf(10));

		final IuguClient iuguClient = IuguClient.getProductionInstance();
		final Object response = iuguClient
				.withApiToken(apiToken)
				.withPrivateKey(privateKey)
				.validateSignature(transferRequest);

		System.out.println(response);
	}
}