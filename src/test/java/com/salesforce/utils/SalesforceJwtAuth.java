package com.salesforce.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Authenticates to Salesforce with the OAuth JWT bearer flow and converts the
 * returned API access token into a Selenium browser session via frontdoor.jsp.
 */
public final class SalesforceJwtAuth {

    private static final String JWT_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer";
    private static final String DEFAULT_REDIRECT_PATH = "/lightning/page/home";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private SalesforceJwtAuth() {
    }

    public static void login(WebDriver driver) {
        login(driver, DEFAULT_REDIRECT_PATH);
    }

    public static void login(WebDriver driver, String redirectPath) {
        AuthSession session = authenticate();
        String frontdoorUrl = createFrontdoorUrl(session, redirectPath);

        try {
            driver.get(frontdoorUrl);
            new WebDriverWait(driver, Duration.ofSeconds(60)).until(webDriver -> {
                String currentUrl = webDriver.getCurrentUrl();
                return currentUrl != null
                        && currentUrl.contains("/lightning/")
                        && !currentUrl.contains("/login");
            });
        } catch (TimeoutException e) {
            throw new IllegalStateException(
                    "Salesforce JWT frontdoor login did not reach a Lightning page. Current URL: "
                            + driver.getCurrentUrl(), e);
        }
    }

    public static String createFrontdoorUrl(AuthSession session, String redirectPath) {
        if (session == null || session.accessToken == null) {
            throw new IllegalArgumentException("A Salesforce JWT auth session is required");
        }
        String destination = redirectPath.startsWith("/") ? redirectPath : "/" + redirectPath;
        try {
            return new URIBuilder(trimTrailingSlash(session.instanceUrl) + "/secur/frontdoor.jsp")
                    .addParameter("sid", session.accessToken)
                    .addParameter("retURL", destination)
                    .build()
                    .toASCIIString();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create the Salesforce frontdoor URL", e);
        }
    }

    public static AuthSession authenticate() {
        String consumerKey = requireSetting("SF_CONSUMER_KEY", "consumer_key");
        String username = requireSetting("SF_USERNAME", "salesforce_username");
        String loginUrl = resolveLoginUrl();
        String privateKeyPath = setting("SF_PRIVATE_KEY_PATH", "salesforce_private_key_path");
        if (privateKeyPath == null) {
            privateKeyPath = "cert/server.key";
        }

        String assertion = createJwtAssertion(consumerKey, username, loginUrl, Paths.get(privateKeyPath));
        Map<String, Object> tokenResponse = exchangeAssertion(loginUrl, assertion);
        AuthSession session = new AuthSession(
                requiredResponseValue(tokenResponse, "access_token"),
                requiredResponseValue(tokenResponse, "id"),
                requiredResponseValue(tokenResponse, "instance_url"));
        verifyAuthenticatedUser(session, username);
        return session;
    }

    static String createJwtAssertion(String consumerKey, String username, String audience, Path privateKeyPath) {
        String header = base64Url("{\"alg\":\"RS256\",\"typ\":\"JWT\"}");
        long expiry = Instant.now().getEpochSecond() + 180;
        String claims = String.format(
                "{\"iss\":%s,\"sub\":%s,\"aud\":%s,\"exp\":%d}",
                jsonString(consumerKey), jsonString(username), jsonString(audience), expiry);
        String unsignedJwt = header + "." + base64Url(claims);

        try {
            PrivateKey privateKey = readPrivateKey(privateKeyPath);
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(privateKey);
            signer.update(unsignedJwt.getBytes(StandardCharsets.US_ASCII));
            return unsignedJwt + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(signer.sign());
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException("Unable to sign the Salesforce JWT using " + privateKeyPath, e);
        }
    }

    private static Map<String, Object> exchangeAssertion(String loginUrl, String assertion) {
        HttpPost request = new HttpPost(trimTrailingSlash(loginUrl) + "/services/oauth2/token");
        request.setConfig(requestConfig());
        List<NameValuePair> form = Arrays.asList(
                new BasicNameValuePair("grant_type", JWT_GRANT_TYPE),
                new BasicNameValuePair("assertion", assertion));
        request.setEntity(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8));

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {
            Map<String, Object> body = readJson(response.getEntity());
            if (response.getStatusLine().getStatusCode() >= 300) {
                throw new IllegalStateException("Salesforce JWT exchange failed: "
                        + errorMessage(body, response.getStatusLine().getReasonPhrase()));
            }
            return body;
        } catch (IOException e) {
            throw new IllegalStateException("Salesforce JWT exchange request failed", e);
        }
    }

    private static void verifyAuthenticatedUser(AuthSession session, String expectedUsername) {
        HttpGet request = new HttpGet(session.identityUrl);
        request.setConfig(requestConfig());
        request.setHeader("Authorization", "Bearer " + session.accessToken);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {
            Map<String, Object> identity = readJson(response.getEntity());
            if (response.getStatusLine().getStatusCode() >= 300) {
                throw new IllegalStateException("Salesforce identity verification failed: "
                        + errorMessage(identity, response.getStatusLine().getReasonPhrase()));
            }

            String actualUsername = String.valueOf(identity.get("username"));
            if (!expectedUsername.equals(actualUsername)) {
                throw new IllegalStateException("JWT authenticated the wrong Salesforce user: expected "
                        + expectedUsername + ", got " + actualUsername);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Salesforce identity verification request failed", e);
        }
    }

    private static PrivateKey readPrivateKey(Path path) throws IOException, GeneralSecurityException {
        String pem = new String(Files.readAllBytes(path.toAbsolutePath().normalize()), StandardCharsets.UTF_8);
        if (pem.contains("BEGIN RSA PRIVATE KEY")) {
            throw new IllegalArgumentException("The key must use PKCS#8 PEM format (BEGIN PRIVATE KEY). "
                    + "Convert it with: openssl pkcs8 -topk8 -nocrypt -in server.key -out server-pkcs8.key");
        }
        String encoded = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(encoded);
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    private static Map<String, Object> readJson(HttpEntity entity) throws IOException {
        String json = entity == null ? "{}" : EntityUtils.toString(entity, StandardCharsets.UTF_8);
        return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() { });
    }

    private static RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(10_000)
                .setConnectionRequestTimeout(10_000)
                .setSocketTimeout(10_000)
                .build();
    }

    private static String requiredResponseValue(Map<String, Object> response, String key) {
        Object value = response.get(key);
        if (value == null || String.valueOf(value).trim().isEmpty()) {
            throw new IllegalStateException("Salesforce JWT response did not include " + key);
        }
        return String.valueOf(value);
    }

    private static String errorMessage(Map<String, Object> response, String fallback) {
        Object description = response.get("error_description");
        if (description != null) {
            return String.valueOf(description);
        }
        Object error = response.get("error");
        return error == null ? fallback : String.valueOf(error);
    }

    private static String resolveLoginUrl() {
        String configured = setting("SF_LOGIN_URL", "salesforce_login_url");
        if (configured != null) {
            return trimTrailingSlash(configured);
        }
        return Boolean.parseBoolean(setting("SF_SANDBOX"))
                ? "https://test.salesforce.com"
                : "https://login.salesforce.com";
    }

    private static String requireSetting(String... names) {
        String value = setting(names);
        if (value == null) {
            throw new IllegalStateException("Missing configuration: " + String.join(" or ", names));
        }
        return value;
    }

    private static String setting(String... names) {
        for (String name : names) {
            String value = System.getProperty(name);
            if (value == null || value.trim().isEmpty()) {
                value = System.getenv(name);
            }
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return null;
    }

    private static String trimTrailingSlash(String value) {
        return value.replaceFirst("/+$", "");
    }

    private static String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String jsonString(String value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to encode JWT claim", e);
        }
    }

    public static final class AuthSession {
        private final String accessToken;
        private final String identityUrl;
        private final String instanceUrl;

        private AuthSession(String accessToken, String identityUrl, String instanceUrl) {
            this.accessToken = accessToken;
            this.identityUrl = identityUrl;
            this.instanceUrl = instanceUrl;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getIdentityUrl() {
            return identityUrl;
        }

        public String getInstanceUrl() {
            return instanceUrl;
        }
    }
}
