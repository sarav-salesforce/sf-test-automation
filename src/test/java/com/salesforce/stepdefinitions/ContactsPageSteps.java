package com.salesforce.stepdefinitions;

import com.salesforce.context.TestContext;
import com.salesforce.pageObjectModel.ContactsPage;
import io.cucumber.java.en.Then;

public class ContactsPageSteps {

    public TestContext testContext;

    public ContactsPage contactsPage;

    public ContactsPageSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Then("User creates new contact by populating mandatory fields")
    public void userCreatesNewContactByPopulatingMandatoryFields() {
        contactsPage = testContext.pageObjectManager.getContactsPage();
        contactsPage.chooseSalutation("Mr.");
        contactsPage.chooseAccount("sForce");
        contactsPage.enterFirstName("Test Automation");
        contactsPage.enterLastName("Testing");
    }
}
