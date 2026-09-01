package com.salesforce.pageObjectModel;

import com.salesforce.utils.SalesforceJwtAuth;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class LoginPage {

    public WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    By userName = By.xpath("//input[@id='username']");
    By passWord = By.xpath("//input[@id='password']");
    By loginButton = By.xpath("//input[@id='Login']");
    By loginError = By.xpath("//div[@id='error' and @class='loginError']");

    public void enterUserName(String username) {
        driver.findElement(userName).sendKeys(username);
    }

    public void enterPassword(String password) {
        driver.findElement(passWord).sendKeys(password);
    }

    public void clickLoginButton() {
        driver.findElement(loginButton).click();
    }

    /** Log in without entering a username or password in the browser. */
    public void loginWithJwt() {
        SalesforceJwtAuth.login(driver);
    }

    public void loginWithJwt(String redirectPath) {
        SalesforceJwtAuth.login(driver, redirectPath);
    }

    public boolean assertLoginError(String errorMessage) {
        WebElement element = driver.findElement(loginError);
        Assert.assertEquals(element.getText(), errorMessage);
        return true;
    }


}
