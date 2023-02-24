// - O formato do arquivo de chave primária precisa estar em PKCS#8, para isso execute o seguinte comando no terminal:
// openssl pkcs8 -topk8 -inform PEM -outform PEM -in private.pem -out private_pkcs8.pem -nocrypt

// para executar:
// - Altere a linha iuru_rsa.api_token, informando seu token
// - Compile o arquivo com o comando abaixo:
// javac iugu_rsa_sample_main.java
// - Execute o arquivo com o comando abaixo:
// java iugu_rsa_sample_main

// #####################################################################################################
// #####################################################################################################
// #####################################################################################################

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

// #####################################################################################################
//                                           IUGU_RSA_SAMPLE
class IUGU_RSA_SAMPLE {

  public boolean print_vars = false;
  public String api_token = "TOKEN CREATED ON IUGU PANEL"; // Link de referência: https://dev.iugu.com/reference/autentica%C3%A7%C3%A3o#criando-chave-api-com-assinatura
  public String file_private_key = "/file_path/private_key.pem"; // Link de referência: https://dev.iugu.com/reference/autentica%C3%A7%C3%A3o#segundo-passo
  public Integer ZoneOffsetofHours = -3;

  private String get_request_time() { // Link de referência: https://dev.iugu.com/reference/autentica%C3%A7%C3%A3o#quinto-passo
    return DateTimeFormatter
      .ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
      .format(ZonedDateTime.now(ZoneOffset.ofHours(this.ZoneOffsetofHours)));
  }

  private RSAPrivateKey get_private_key() {
    String text_key = "";
    RSAPrivateKey private_key = null;
    try {
      InputStream is = new FileInputStream(this.file_private_key);
      @SuppressWarnings("resource")
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      StringBuilder sb = new StringBuilder();
      boolean inKey = false;
      String tipoChave = "PRIVATE";
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        if (!inKey) {
          if (
            line.startsWith("-----BEGIN ") &&
            line.endsWith(" " + tipoChave + " KEY-----")
          ) {
            inKey = true;
          }
        } else {
          if (
            line.startsWith("-----END ") &&
            line.endsWith(" " + tipoChave + " KEY-----")
          ) {
            inKey = false;
            break;
          }
          sb.append(line);
        }
      }
      text_key = sb.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(
        Base64.getDecoder().decode(text_key.toString())
      );
      KeyFactory kf = KeyFactory.getInstance("RSA");
      private_key = (RSAPrivateKey) kf.generatePrivate(spec);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return private_key;
  }

  private String sign_body(
    String method,
    String endpoint,
    String request_time,
    String body,
    RSAPrivateKey private_key
  ) { // Link de referência: https://dev.iugu.com/reference/autentica%C3%A7%C3%A3o#sexto-passo
    String ret_sign = "";
    try {
      String pattern =
        method +
        "|" +
        endpoint +
        "\n" +
        this.api_token +
        '|' +
        request_time +
        "\n" +
        body;
      Signature privateSignature = Signature.getInstance("SHA256withRSA");
      privateSignature.initSign(private_key);
      privateSignature.update(pattern.getBytes("UTF-8"));
      byte[] s = privateSignature.sign();
      ret_sign = Base64.getEncoder().encodeToString(s);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return ret_sign;
  }

  private String last_response = "";

  public String getLastResponse() {
    return this.last_response;
  }

  private int last_response_code = 0;

  public int getLastResponseCode() {
    return this.last_response_code;
  }

  private boolean send_data(String method, String endpoint, String data, int response_code_ok) { // Link de referência: https://dev.iugu.com/reference/autentica%C3%A7%C3%A3o#d%C3%A9cimo-primeiro-passo
    this.last_response = "";
    this.last_response_code = 0;
    String request_time = this.get_request_time();
    String body = data;
    String signature =
      this.sign_body(
          method,
          endpoint,
          request_time,
          body,
          this.get_private_key()
        );

    if (this.print_vars) {
      System.out.println("endpoint: " + method + " - " + endpoint);
      System.out.println("request_time: " + request_time);
      System.out.println("api_token: " + this.api_token);
      System.out.println("body: " + body);
      System.out.println("signature: " + signature);
    }

    boolean ret = false;
    try {
      URL url = new URL("https://api.iugu.com" + endpoint);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod(method.toUpperCase());
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("Accept", "application/json");
      con.setRequestProperty("Signature", "signature=" + signature);
      con.setRequestProperty("Request-Time", request_time);
      con.setDoOutput(true);

      try (OutputStream os = con.getOutputStream()) {
        byte[] input = body.getBytes("utf-8");
        os.write(input, 0, input.length);
      }
      this.last_response_code = con.getResponseCode();
      ret = this.last_response_code == response_code_ok;

      InputStream _is;
      if (this.last_response_code < 400) {
        _is = con.getInputStream();
      } else {
        _is = con.getErrorStream();
      }

      BufferedReader br = new BufferedReader(
        new InputStreamReader(_is, "utf-8")
      );
      StringBuilder response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
      this.last_response = response.toString();
    } catch (Exception e) {
      this.last_response = e.getMessage();
    }

    return ret;
  }

  public boolean signature_validate(String data) { // Link de referência: https://dev.iugu.com/reference/validate-signature
    String method = "POST";
    String endpoint = "/v1/signature/validate";
    return this.send_data(method, endpoint, data, 200);
  }

  public boolean transfer_requests(String data) {
    String method = "POST";
    String endpoint = "/v1/transfer_requests";
    return this.send_data(method, endpoint, data, 202);
  }
}

// #####################################################################################################

public class iugu_rsa_sample_main {

  public static void main(String[] args) {
    // #####################################################################################################
    //                                    Example of use IUGU_RSA_SAMPLE
    // #####################################################################################################
    IUGU_RSA_SAMPLE iuru_rsa = new IUGU_RSA_SAMPLE();
    iuru_rsa.api_token = "";
    iuru_rsa.print_vars = true;
    iuru_rsa.file_private_key = "./private_pkcs8.pem";

    // #####################################################################################################
    //                                          signature_validate
    // Link de referência: https://dev.iugu.com/reference/validate-signature
    String json =
      "{" +
      "\"api_token\": \"" +
      iuru_rsa.api_token +
      "\"," +
      "\"mensagem\": \"qualquer coisa\"" +
      "}";

    if (iuru_rsa.signature_validate(json)) {
      System.out.println("Response: " + iuru_rsa.getLastResponseCode() + iuru_rsa.getLastResponse());
    } else {
      System.out.println("Error: " + iuru_rsa.getLastResponseCode() + iuru_rsa.getLastResponse());
    }
    // #####################################################################################################

    // #####################################################################################################
    //                                           transfer_requests
    String json2 =
      "{" +
      "\"api_token\" : \"" +
      iuru_rsa.api_token +
      "\"," +
      "\"transfer_type\" : \"pix\"," +
      "\"amount_cents\" : 1," +
      "\"receiver\": {" +
      "\"pix\": {" +
      "\"key\" : \"000000000\", " +
      "\"type\" : \"cpf\"" +
      "}" +
      "}" +
      "}";

    if (iuru_rsa.transfer_requests(json2)) {
      System.out.println("Response: " + iuru_rsa.getLastResponseCode() + iuru_rsa.getLastResponse());
    } else {
      System.out.println("Error: " + iuru_rsa.getLastResponseCode() + iuru_rsa.getLastResponse());
    }
    // #####################################################################################################

  }
}
