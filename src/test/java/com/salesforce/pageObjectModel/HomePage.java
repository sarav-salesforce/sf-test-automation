package com.salesforce.pageObjectModel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePage {

    public WebDriver driver;
    CommonPageObjects commonPageObjects;

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    By loginAvatar = By.xpath("//span[contains(@class,'forceSocialPhoto')]");

    public void assertLoginAvatar() {
        commonPageObjects = new CommonPageObjects(driver);
        commonPageObjects.waitForElementToBePresent(loginAvatar);
        WebElement element = driver.findElement(loginAvatar);
        if(element.isDisplayed()) {
            System.out.println("Logged in successfully");
        } else {
            System.out.println("Login error");
        }
    }

}
