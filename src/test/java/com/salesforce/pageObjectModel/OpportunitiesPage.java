package com.salesforce.pageObjectModel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class OpportunitiesPage {

    WebDriver driver;

    public OpportunitiesPage(WebDriver driver) {
        this.driver = driver;
    }

    By opportunityNameInputField = By.xpath("//input[@name='Name']");
    //(//label[text()='Opportunity Name']/following::input)[1]
    By amountInputField = By.xpath("//label[text()='Amount']/following::input)[1]");
    By closeDateInputField = By.xpath("//input[@name='CloseDate']");
    By stagePicklistField = By.xpath("(//label[text()='Stage']/following::button)[1]");


    public void enterOpportunityName(String oppName) {
        driver.findElement(opportunityNameInputField).sendKeys(oppName);
    }

    public void enterAmount(int amount) {
        driver.findElement(amountInputField).sendKeys(String.valueOf(amount));
    }

    public void enterAmount(String amount) {
        driver.findElement(amountInputField).sendKeys(amount);
    }

    public void enterCloseDate(String date) {
        driver.findElement(closeDateInputField).sendKeys(date);
    }

    public void chooseStageInOpportunity(String stageName) {
        driver.findElement(stagePicklistField).click();
        WebElement element = driver.findElement(By.xpath("//span[text()='"+stageName+"']"));
        element.click();
    }

}
