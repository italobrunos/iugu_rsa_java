# iugu_rsa_java

Client de exemplo em Java, utilizando Feign (https://github.com/OpenFeign/feign), para consumo das APIs da iugu.

## Métodos suportados

Esta é uma versão demonstrativa e tem suporte apenas para alguns métodos que utilizam a nova autenticação com chave RSA,
que são:

- /v1/signature/validate
- /v1/accounts/{accountId}/request_withdraw
- /v1/transfers

## Como utilizar

### Criação das chaves pública e privada

Para criação e configuração das chaves público e privada, siga a recomendações dos
links:

- https://dev.iugu.com/reference/autentica%C3%A7%C3%A3o
- https://www.youtube.com/watch?v=oY9-8cVQzvo

### Utilizando a chave privada no seu projeto

Existem várias abordagens para utilização de variáveis no ambiente num projeto Java. Como sugestão, podemos fazer
o encode, em Base64, do conteúdo do arquivo da chave privada e utilizar o resultado num "gerenciador de segredos", como
o Secrets Manager da AWS ou GCP, ou qualquer outro gerenciador que você já esteja utilizando. Essa abordagem facilitará
a troca do segredo caso necessária e facilitará sua utilização, tornando desnecessária a manipulação de arquivos em
tempo de execução.

Supondo que o valor está definido como uma variável de ambiente, podemos criar um método para recuperá-lo da seguinte
forma:

```java
public String getPrivateKey() {
	final String privateKeyInBase64 = System.getenv("IUGU_PRIVATE_KEY");
	return new String(Base64.getDecoder().decode(privateKeyInBase64));
}
```

Neste caso, o nome da variável de ambiente utilizada foi "IUGU_PRIVATE_KEY", mas, isso vai depender de como a variável
foi definida anteriormente.

### Utilizando o client Iugu

Com o valor da chave privada em mãos, agora podemos utilizar a implementação do client Iugu. Para tal, precisamos
instanciar o client:

```java
final IuguClient iuguClient = IuguClient.getProductionInstance();
```

Apenas para exemplo, vamos criar uma requisição de transferência de R$10 para a conta "12345":

```java
final TransferRequest transferRequest = new TransferRequest();
transferRequest.

setReceiverId("12345");
transferRequest.

setAmountCents(BigInteger.valueOf(10));
```

Como o objetivo aqui é testar a chave, vamos chamar o método "/v1/signature/validate" utilizando essa request:

```java
final Object response = iuguClient
		.withApiToken(apiToken)
		.withPrivateKey(privateKey)
		.validateSignature(transferRequest);
```

Perceba que aqui estamos utilizando 2 métodos de segurança:

- withApiToken: esse método inclui o "apiToken" no header da requisição;
- withPrivateKey: esse método inclui a assinatura, utilizando a chave RSA, no header da requisição.

Se tudo foi configurado corretamente, o método "/v1/signature/validate" vai retornar um 200.

### Exemplo completo

A classe "IuguClientTest" possui um exemplo funcional, sendo apenas necessário definir o valor do "apiToken" e do "
privateKey".

## Disclaimer

Criei esse projeto de exemplo apenas para facilitar a utilização da chave RSA num ambiente real, sem necessidade da
manipulação de arquivos e tentando abstrair o máximo possível da complexidade do fluxo, trazendo complexidade apenas
para o client.