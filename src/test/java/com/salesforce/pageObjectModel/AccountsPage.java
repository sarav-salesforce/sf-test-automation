package com.salesforce.pageObjectModel;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class AccountsPage {

    public WebDriver driver;
    public CommonPageObjects commonPageObjects;

    public AccountsPage(WebDriver driver) {
        this.driver = driver;
    }

    By accountNameInputField = By.xpath("//input[@name='Name']");
    //By accountNameInputField = By.xpath("(//label[text()='Account Name']/following::input)[1]");
    By ratingPicklist = By.xpath("(//label[text()='Rating']/following::button)[1]");
    By ratingPicklistValueCold = By.xpath("//span[text()='Cold']");
    By ratingPicklistValueWarm = By.xpath("//span[text()='Warm']");
    By ratingPicklistValueHot = By.xpath("//span[text()='Hot']");


    public void enterAccountName(String accountName) {
        commonPageObjects = new CommonPageObjects(driver);
        commonPageObjects.waitForElementToBePresent(accountNameInputField);
        commonPageObjects.waitForElementToBeVisible(driver.findElement(accountNameInputField));
        commonPageObjects.waitForElementToBeClickable(driver.findElement(accountNameInputField));
        driver.findElement(accountNameInputField).sendKeys(accountName);
    }

    public void chooseRatingPicklist(String value) {
        commonPageObjects = new CommonPageObjects(driver);
        commonPageObjects.waitForElementToBePresent(ratingPicklist);
        commonPageObjects.waitForElementToBeVisible(driver.findElement(ratingPicklist));
        commonPageObjects.waitForElementToBeClickable(driver.findElement(ratingPicklist));
        driver.findElement(ratingPicklist).click();
        commonPageObjects.waitForElementToBePresent(By.xpath("//span[text()='"+value+"']"));
        commonPageObjects.waitForElementToBeClickable(driver.findElement(By.xpath("//span[text()='"+value+"']")));
        WebElement element = driver.findElement(By.xpath("//span[text()='"+value+"']"));
        element.click();
    }

    //lightning-formatted-text[text()='assert testing']


    public void assertAccountName(String accountName) {
        commonPageObjects = new CommonPageObjects(driver);
        By locator = By.xpath("//lightning-formatted-text[text()='"+accountName+"']");
        commonPageObjects.waitForElementToBePresent(locator);
        WebElement element = driver.findElement(locator);
        Assert.assertEquals(element.getText(),accountName);
        Assert.assertTrue(element.isDisplayed());
        /*if(element.isDisplayed()) {
            System.out.println("Account created successfully");
        } else {
            System.out.println("Account creation failed");
        }
        try {
            element.isDisplayed();
            System.out.println("Account created successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
    }


}
