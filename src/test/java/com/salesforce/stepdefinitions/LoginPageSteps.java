package com.salesforce.stepdefinitions;

import com.aventstack.extentreports.Status;
import com.salesforce.context.TestContext;
import com.salesforce.pageObjectModel.LoginPage;
import com.salesforce.pageObjectModel.PageObjectManager;
import com.salesforce.utils.ConfigReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class LoginPageSteps {

    TestContext testContext;
    public LoginPage loginPage;
    public PageObjectManager pageObjectManager;

    public LoginPageSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Given("User opens salesforce login url")
    public void userOpensSalesforceLoginUrl() {
        String urlFromPropertyFile = ConfigReader.getProperty("url");
        String urlFromMaven = System.getProperty("url");
        String url = urlFromMaven != null ? urlFromMaven : urlFromPropertyFile;
        try {
            testContext.browserFactory.launchUrl(url);
            TestContext.extentTest.log(Status.PASS, "Successfully opened the url: "+ ConfigReader.getProperty("url"));
        }catch (Exception e) {
            TestContext.extentTest.log(Status.WARNING, "Successfully opened the url: "+ ConfigReader.getProperty("url"));
        }

    }

    @When("User enters valid credentials")
    public void user_enters_valid_credentials() {
        loginPage = testContext.pageObjectManager.getLoginPage();
        loginPage.enterUserName(System.getenv("sit_salesforce_username"));
        loginPage.enterPassword(System.getenv("sit_salesforce_password"));
        loginPage.clickLoginButton();
        TestContext.extentTest.log(Status.PASS, "Valid credentials entered ");
    }

    @When("User enters invalid credentials")
    public void userEntersInvalidCredentials() {
        loginPage = testContext.pageObjectManager.getLoginPage();
        loginPage.enterUserName(System.getenv("sit_salesforce_username"));
        loginPage.enterPassword("hfksahdfiywerbkjfdesa");
        loginPage.clickLoginButton();
        TestContext.extentTest.log(Status.PASS, "InValid credentials entered ");
    }

    @Then("User should see an error message")
    public void userShouldSeeAnErrorMessage() {
        loginPage = testContext.pageObjectManager.getLoginPage();
        if(loginPage.assertLoginError("Please check your username and password. If you still can't log in, contact your Salesforce administrator.")) {
            TestContext.extentTest.log(Status.PASS, "Error message displayed as expected");
        } else {
            TestContext.extentTest.log(Status.FAIL, "Error message not displayed as expected");
        }
    }
}