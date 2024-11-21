package com.salesforce.stepdefinitions;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.salesforce.context.TestContext;
import io.cucumber.java.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;


public class Hooks {

    public TestContext testContext;
    WebDriver driver;

    public Hooks(TestContext testContext) {
        this.testContext = testContext;
    }

    @BeforeAll
    public static void beforeAll() {
        ExtentSparkReporter extentSparkReporter = new ExtentSparkReporter("target/ExtentReports/ExtentReport.html");
        TestContext.extentReports = new ExtentReports();
        TestContext.extentReports.attachReporter(extentSparkReporter);
    }

    @Before
    public void initiateTest(io.cucumber.java.Scenario scenario) {
        TestContext.extentTest = TestContext.extentReports.createTest(scenario.getName());
        TestContext.extentTest.log(Status.INFO, "Starting the scenario: "+ scenario.getName());
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        try {
            String screenshotPath = captureScreenshot(scenario.getName());
            TestContext.extentTest.addScreenCaptureFromPath(screenshotPath,"Step Screenshot");
        } catch (IOException e) {
            TestContext.extentTest.log(Status.WARNING, "Failed to capture screenshot: "+e.getMessage());
        }
    }

    @After
    public void cleanUp(Scenario scenario) {
        if(!scenario.isFailed()) {
            TestContext.extentTest.log(Status.PASS, "Scenario passed successfully");
        } else {
            TestContext.extentTest.log(Status.FAIL, "Scenario failed");
        }
        testContext.browserFactory.getDriver().quit();
    }

    @AfterAll
    public static void afterAll() {
        TestContext.extentReports.flush();
    }

    private String captureScreenshot(String stepName) throws IOException {
        WebDriver driver = testContext.browserFactory.getDriver();
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        // Define a location to save the screenshot
        String screenshotDir = "target/ExtentReports/screenshots/";
        String screenshotPath = screenshotDir + stepName + "_" + System.currentTimeMillis() + ".png";
        File screenshotFile = new File(screenshotPath);

        // Create the directory if it doesn't exist
        new File(screenshotDir).mkdirs();

        // Save the screenshot
        FileUtils.copyFile(screenshot, screenshotFile);

        // Return the relative path for the report
        return "./screenshots/" + screenshotFile.getName();
    }


}
