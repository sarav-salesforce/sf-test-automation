package com.salesforce.pageObjectModel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CasesPage {
    WebDriver driver;

    public CasesPage(WebDriver driver) {
        this.driver = driver;
    }

    By statusPicklistField = By.xpath("(//label[text()='Status']/following::button)[1]");
    By caseOriginPicklistField = By.xpath("(//label[text()='Case Origin']/following::button)[1]");
    By descriptionTextAreaField = By.xpath("(//label[text()='Description']/following::textarea)[1]");


    public void chooseStatus(String status) {
        driver.findElement(statusPicklistField).click();
        WebElement element = driver.findElement(By.xpath("//span[text()='" + status + "']"));
        element.click();
    }

    public void chooseCaseOrigin(String caseOrigin) {
        driver.findElement(caseOriginPicklistField).click();
        WebElement element = driver.findElement(By.xpath("//span[text()='" + caseOrigin + "']"));
        element.click();
    }

    public void enterDescription(String descriptionText) {
        driver.findElement(descriptionTextAreaField).sendKeys(descriptionText);
    }

}
