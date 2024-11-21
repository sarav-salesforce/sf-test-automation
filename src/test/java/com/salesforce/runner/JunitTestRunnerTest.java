package com.salesforce.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/java/com/salesforce/features", glue = "com.salesforce.stepdefinitions",
        monochrome = true, tags = "@ErrorValidation or @SmokeTest", plugin = {"pretty","html:target/cucumber-reports.html", "json:target/cucumber-reports.json"})
public class JunitTestRunnerTest {

}