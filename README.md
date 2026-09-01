# Salesforce Selenium test automation

This Java 11, Selenium, Cucumber, and JUnit suite signs valid test users into
the Salesforce UI with the OAuth JWT bearer flow. No username or password is
typed into the login page.

## Prerequisites

- Java 11 or newer (`java -version`)
- Maven 3.9 or newer (`mvn -version`)
- Google Chrome for Selenium UI execution
- A Salesforce Connected App configured for the OAuth JWT bearer flow

The project has been validated locally with Java 11, Maven 3.9, and headless
Chrome on macOS Apple Silicon.

## JWT UI login

The `User logs in with JWT` Cucumber step:

1. Signs an RS256 JWT using the Connected App consumer key and private key.
2. Exchanges it at `/services/oauth2/token` for an access token.
3. Calls the returned identity URL and verifies the Salesforce username.
4. Opens `/secur/frontdoor.jsp?sid=...&retURL=...` in Selenium and waits for
   the Lightning page.

The invalid-credentials scenario still uses the normal username/password login
screen so its error validation remains meaningful.

## Configuration

Create a local environment file from the example:

```shell
cp .env.example .env
```

Populate `.env` with the Connected App and Salesforce user details:

```dotenv
SF_CONSUMER_KEY=your-connected-app-consumer-key
SF_USERNAME=automation.user@example.com
SF_LOGIN_URL=https://login.salesforce.com
SF_SANDBOX=false
SF_PRIVATE_KEY_PATH=cert/server-pkcs8.key
```

For a sandbox, use `SF_LOGIN_URL=https://test.salesforce.com`. Alternatively,
omit `SF_LOGIN_URL` and set `SF_SANDBOX=true`.

The private key must be an unencrypted PKCS#8 PEM key beginning with
`-----BEGIN PRIVATE KEY-----`. Its matching certificate must be uploaded to
the Salesforce Connected App. Convert an existing PKCS#1 key if necessary:

```shell
openssl pkcs8 -topk8 -nocrypt \
  -in cert/server.key \
  -out cert/server-pkcs8.key
```

The `.env` and `cert/` paths are ignored by Git. Never commit the consumer key,
private key, access token, or other credentials.

## Run the tests

The Java project does not load `.env` automatically. Export its entries into
the current terminal before running Maven:

```shell
set -a
source .env
set +a
```

Run only the validated JWT UI login scenario:

```shell
mvn -Dbrowser=chrome_headless \
  -Dcucumber.filter.name='^Verify whether user is able to login to Salesforce with valid credentials$' \
  test
```

A successful run ends with:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Run the configured Cucumber suite with:

```shell
mvn -Dbrowser=chrome_headless test
```

The full configured suite selects `@SmokeTest` and `@ErrorValidation`
scenarios, so it runs more than the JWT login check. The legacy invalid-login
scenario also requires `sit_salesforce_username` to be exported.

To use another supported browser, change the existing browser property, for
example:

```shell
mvn -Dbrowser=chrome test
```

JWT settings may also be supplied as Maven system properties, for example
`-DSF_USERNAME=...`, although environment variables are recommended to avoid
placing credentials in shell history.

The implementation is in
`src/test/java/com/salesforce/utils/SalesforceJwtAuth.java`. Page objects can
also land directly on a specific Lightning route:

```java
loginPage.loginWithJwt("/lightning/o/Account/list?filterName=__Recent");
```
