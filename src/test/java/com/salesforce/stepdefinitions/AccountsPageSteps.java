package com.salesforce.stepdefinitions;

import com.salesforce.context.TestContext;
import com.salesforce.pageObjectModel.AccountsPage;
import io.cucumber.java.en.Then;

import java.util.Date;

public class AccountsPageSteps {

    public TestContext testContext;

    public AccountsPage accountsPage;
    public static String accountName;

    public AccountsPageSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Then("User creates new account by populating {string} and {string} fields")
    public void userCreatesNewAccountByPopulatingMandatoryFields(String name, String rating) {
        accountsPage = testContext.pageObjectManager.getAccountsPage();
        Date date = new Date();
        accountName = name+"_"+date.getTime();
        accountsPage.enterAccountName(accountName);
        accountsPage.chooseRatingPicklist(rating);
    }

    @Then("User verifies account is created successfully")
    public void userVerifiesAccountIsCreatedSuccessfully() {
        accountsPage = testContext.pageObjectManager.getAccountsPage();
        accountsPage.assertAccountName(accountName);
    }

    /*@Then("^User creates new account by populating (.+) and (.+) fields$")
    public void userCreatesNewAccount(String name, String rating) {
        accountsPage = testContext.pageObjectManager.getAccountsPage();
        accountsPage.enterAccountName(name);
        accountsPage.chooseRatingPicklist(rating);
    }*/

}
