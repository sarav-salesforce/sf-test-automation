package com.salesforce.pageObjectModel;

import org.openqa.selenium.WebDriver;

public class PageObjectManager {

    public WebDriver driver;
    public LoginPage loginPage;
    public HomePage homePage;
    public AccountsPage accountsPage;
    public ContactsPage contactsPage;
    public OpportunitiesPage opportunitiesPage;
    public CasesPage casesPage;
    public CommonPageObjects commonPageObjects;

    public PageObjectManager(WebDriver driver) {
        this.driver = driver;
    }

    public LoginPage getLoginPage() {
        loginPage = new LoginPage(driver);
        return loginPage;
    }

    public HomePage getHomePage() {
        homePage = new HomePage(driver);
        return homePage;
    }

    public AccountsPage getAccountsPage() {
        accountsPage = new AccountsPage(driver);
        return accountsPage;
    }

    public ContactsPage getContactsPage() {
        contactsPage = new ContactsPage(driver);
        return contactsPage;
    }

    public OpportunitiesPage getOpportunitiesPage() {
        opportunitiesPage = new OpportunitiesPage(driver);
        return opportunitiesPage;
    }

    public CasesPage getCasesPage() {
        casesPage = new CasesPage(driver);
        return casesPage;
    }

    public CommonPageObjects getCommonPageObjects() {
        commonPageObjects = new CommonPageObjects(driver);
        return commonPageObjects;
    }

}
