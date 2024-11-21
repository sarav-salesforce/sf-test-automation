package com.salesforce.pageObjectModel;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CommonPageObjects {

    WebDriver driver;

    public static WebDriverWait wait;

    public CommonPageObjects(WebDriver driver) {
        this.driver = driver;
    }

    By newButton = By.xpath("//a[@title='New']");
    By saveButton = By.xpath("//button[@name='SaveEdit']");
    By appLauncher = By.xpath("//button[@title='App Launcher']");
    By searchAppInputField = By.xpath("//input[@placeholder='Search apps and items...']");


    public void clickNewButton() {
        waitForElementToBePresent(newButton);
        waitForElementToBeVisible(driver.findElement(newButton));
        waitForElementToBeClickable(driver.findElement(newButton));
        driver.findElement(newButton).click();
    }

    public void clickSaveButton() {
        waitForElementToBePresent(saveButton);
        waitForElementToBeVisible(driver.findElement(saveButton));
        waitForElementToBeClickable(driver.findElement(saveButton));
        driver.findElement(saveButton).click();
    }

    /*public void clickAndOpenSfTabs(String tabName) {
        waitForPage();
        waitForElementToBePresent(By.xpath("//a[@title='" + tabName + "']"));
        waitForElementToBeVisible(driver.findElement(By.xpath("//a[@title='" + tabName + "']")));
        waitForElementToBeClickable(driver.findElement(By.xpath("//a[@title='" + tabName + "']")));
        WebElement element = driver.findElement(By.xpath("//a[@title='" + tabName + "']"));
        element.click();
    }*/

    public void clickAndOpenSfTabs (String tabName) {
        waitForPage();
        By tabNameLocator = By.xpath("//a[@title='" + tabName + "']");
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(tabNameLocator));
            element.click();
        } catch (Exception e) {
            System.out.println("Click failed, now attempting to click force click");
            WebElement element = driver.findElement(tabNameLocator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }


    public void chooseApp(String appName) throws InterruptedException {
        //Thread.sleep(5000);
        waitForElementToBePresent(appLauncher);
        waitForElementToBeVisible(driver.findElement(appLauncher));
        waitForElementToBeClickable(driver.findElement(appLauncher));
        clickByJs(driver,driver.findElement(appLauncher));
        //Thread.sleep(5000);
        waitForElementToBePresent(searchAppInputField);
        waitForElementToBeVisible(driver.findElement(searchAppInputField));
        waitForElementToBeClickable(driver.findElement(searchAppInputField));
        driver.findElement(searchAppInputField).sendKeys(appName);
        //Thread.sleep(5000);
        waitForElementToBePresent(By.xpath("//a[@data-label='"+appName+"']"));
        waitForElementToBeVisible(driver.findElement(By.xpath("//a[@data-label='"+appName+"']")));
        waitForElementToBeClickable(driver.findElement(By.xpath("//a[@data-label='"+appName+"']")));
        WebElement element = driver.findElement(By.xpath("//a[@data-label='"+appName+"']"));
        clickByJs(driver,element);
    }

    public void clickByJs(WebDriver driver, WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].click();", element);
    }

    public void waitForElementToBeVisible(WebElement element) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForElementToBeClickable(WebElement element) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void waitForElementToBePresent(By locator) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public void waitForPage() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

}
