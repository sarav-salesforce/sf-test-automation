package com.salesforce.stepdefinitions;

import com.salesforce.context.TestContext;
import com.salesforce.pageObjectModel.CommonPageObjects;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class CommonPageSteps {

    public TestContext testContext;

    public CommonPageObjects commonPageObjects;

    public CommonPageSteps(TestContext testContext) {
        this.testContext =  testContext;
    }

    @Given("User clicks on {string} tab")
    public void userClicksOnSfTab(String tabName) {
        commonPageObjects = testContext.pageObjectManager.getCommonPageObjects();
        commonPageObjects.clickAndOpenSfTabs(tabName);
    }

    @When("User clicks on new button in {string} tab")
    public void userClicksOnNewButton(String tabName) {
        commonPageObjects = testContext.pageObjectManager.getCommonPageObjects();
        commonPageObjects.clickNewButton();
    }

    @And("User clicks on Save button in {string} tab")
    public void userClicksOnSaveButtonInTab(String tabName) {
        commonPageObjects = testContext.pageObjectManager.getCommonPageObjects();
        commonPageObjects.clickSaveButton();
    }

    @And("User chooses {string} from App Launcher")
    public void userChoosesFromAppLauncher(String appName) throws InterruptedException {
        commonPageObjects = testContext.pageObjectManager.getCommonPageObjects();
        commonPageObjects.chooseApp(appName);
    }
}
