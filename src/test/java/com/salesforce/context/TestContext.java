package com.salesforce.context;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.salesforce.pageObjectModel.PageObjectManager;
import org.openqa.selenium.WebDriver;

public class TestContext {

    public WebDriver driver;
    public PageObjectManager pageObjectManager;
    public BrowserFactory browserFactory;
    public static ExtentReports extentReports;
    public static ExtentTest extentTest;

    public TestContext() {
        browserFactory = new BrowserFactory();
        pageObjectManager = new PageObjectManager(browserFactory.getDriver());
    }

}
