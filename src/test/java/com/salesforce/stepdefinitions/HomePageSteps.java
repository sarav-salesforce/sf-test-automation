package com.salesforce.stepdefinitions;

import com.salesforce.context.TestContext;
import com.salesforce.pageObjectModel.HomePage;
import io.cucumber.java.en.Then;

public class HomePageSteps {
    public TestContext testContext;
    public HomePage homePage;

    public HomePageSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Then("User should be logged in successfully")
    public void user_should_be_logged_in_successfully() throws InterruptedException {
        //Thread.sleep(10000);
        homePage = testContext.pageObjectManager.getHomePage();
        homePage.assertLoginAvatar();
    }


}
