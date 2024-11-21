package com.salesforce.pageObjectModel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ContactsPage {

    WebDriver driver;
    CommonPageObjects commonPageObjects;

    public ContactsPage(WebDriver driver) {
        this.driver = driver;
    }

    By salutationPicklist = By.xpath("(//label[text()='Salutation']/following::button)[1]");
    By firstNameInputField = By.xpath("//input[@name='firstName']");
    By lastNameInputField = By.xpath("//input[@name='lastName']");
    By accountLookUpField = By.xpath("(//label[text()='Account Name']/following::input)[1]");

    public void chooseSalutation(String value) {
        commonPageObjects = new CommonPageObjects(driver);
        commonPageObjects.waitForElementToBePresent(salutationPicklist);
        driver.findElement(salutationPicklist).click();
        commonPageObjects.waitForElementToBePresent(By.xpath("//span[text()='"+value+"']"));
        commonPageObjects.waitForElementToBeClickable(driver.findElement(By.xpath("//span[text()='"+value+"']")));
        WebElement element = driver.findElement(By.xpath("//span[text()='"+value+"']"));
        commonPageObjects.clickByJs(driver,element);
    }

    public void enterFirstName(String firstName) {
        commonPageObjects = new CommonPageObjects(driver);
        commonPageObjects.waitForElementToBePresent(firstNameInputField);
        driver.findElement(firstNameInputField).sendKeys(firstName);
    }

    public void enterLastName(String lastName) {
        commonPageObjects = new CommonPageObjects(driver);
        commonPageObjects.waitForElementToBePresent(lastNameInputField);
        driver.findElement(lastNameInputField).sendKeys(lastName);
    }

    public void chooseAccount(String value) {
        commonPageObjects = new CommonPageObjects(driver);
        commonPageObjects.waitForElementToBePresent(accountLookUpField);
        driver.findElement(accountLookUpField).sendKeys(value);
        commonPageObjects.waitForElementToBePresent(By.xpath("//strong[text()='"+value+"']"));
        WebElement element = driver.findElement(By.xpath("//strong[text()='"+value+"']"));
        element.click();
    }

}
