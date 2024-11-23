package com.salesforce.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class SalesforceApi {

//    authenticate -> Bearer Token
    // create account / contact

    public static String login_Url = "https://login.salesforce.com/services/oauth2/token";
    public static String api_version = "v62.0";

    public static String accessToken;
    public static String instanceUrl;
    public static String accountId;
    public static String contactId;
    public static String oppId;


    public static void authenticate(String consumerKey, String consumerSecret, String username, String password) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(login_Url);

            String payload = String.format(
                    "grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s",
                   consumerKey, consumerSecret, username, password
            );

            post.setEntity(new StringEntity(payload));
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            try (CloseableHttpResponse response = client.execute(post)) {
                if(response.getStatusLine().getStatusCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.out.println(responseBody);
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    accessToken = jsonNode.get("access_token").asText();
                    instanceUrl = jsonNode.get("instance_url").asText();
                    System.out.println("Bearer token is: " + accessToken);
                    System.out.println("Instance url is: " + instanceUrl);
                } else {
                    System.out.println(response.getStatusLine().getStatusCode());
                }
            }
        }
    }

    public static void accountCreation(String accountName, String rating, String active) throws IOException {

        String url = String.format("%s/services/data/%s/sobjects/Account",instanceUrl,api_version);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);

            String jsonPayload = String.format("{\"Name\":\"%s\",\"Rating\":\"%s\",\"Active__c\":\"%s\"}",accountName,rating,active);
            post.setEntity(new StringEntity(jsonPayload));

            post.setHeader("Authorization", "Bearer "+accessToken);
            post.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println(responseBody);
                if(response.getStatusLine().getStatusCode() == 201) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    accountId = jsonNode.get("id").asText();
                    System.out.println("Account created successfully and the account Id is: "+ accountId);
                } else {
                    System.out.println("Account creation failed");
                }
            }
        }
    }

    public static void contactCreation(String salutation, String firstName, String lastName, String emailAddress, String accountId) throws IOException {

        String url = String.format("%s/services/data/%s/sobjects/Contact",instanceUrl,api_version);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);

            String jsonPayload = String.format("{\"Salutation\":\"%s\",\"FirstName\":\"%s\",\"LastName\":\"%s\",\"Email\":\"%s\",\"AccountId\":\"%s\"}",salutation,firstName,lastName,emailAddress,accountId);
            post.setEntity(new StringEntity(jsonPayload));

            post.setHeader("Authorization", "Bearer "+accessToken);
            post.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println(responseBody);
                if(response.getStatusLine().getStatusCode() == 201) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    contactId = jsonNode.get("id").asText();
                    System.out.println("Contact created successfully and the contact Id is: "+contactId);
                } else {
                    System.out.println("Contact creation failed");
                }
            }
        }
    }

    public static void opportunityCreation(String oppName, String amount, String closeDate, String stageName, String accountId) throws IOException {

        String url = String.format("%s/services/data/%s/sobjects/Opportunity",instanceUrl,api_version);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
//Name, AccountId, Amount, CloseDate, StageName
            String jsonPayload = String.format("{\"Name\":\"%s\",\"Amount\":\"%s\",\"CloseDate\":\"%s\",\"StageName\":\"%s\",\"AccountId\":\"%s\"}",oppName,amount,closeDate,stageName,accountId);
            post.setEntity(new StringEntity(jsonPayload));

            post.setHeader("Authorization", "Bearer "+accessToken);
            post.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println(responseBody);
                if(response.getStatusLine().getStatusCode() == 201) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    oppId = jsonNode.get("id").asText();
                    System.out.println("Opportunity created successfully and the Opportunity Id is: "+oppId);
                } else {
                    System.out.println("Opportunity creation failed");
                }
            }
        }
    }





    public static void main(String[] args) throws IOException {
        authenticate(System.getenv("consumer_key"),System.getenv("consumer_secret"),System.getenv("sit_salesforce_username"),System.getenv("sit_salesforce_password"));
        accountCreation("Account multiple fields via api", "Hot", "Yes");
        //contactCreation("Mr", "Api","Contact", "jhuhkj@uiyiu.com", accountId);
        opportunityCreation("Api Opp", "100000", "2024-11-30", "Prospecting", accountId);
    }

}
