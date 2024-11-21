Feature: Test Salesforce login feature

  @SmokeTest @Regression
  Scenario: Verify whether user is able to login to Salesforce with valid credentials
    Given User opens salesforce login url
    When User enters valid credentials
    Then User should be logged in successfully

  @Regression @ErrorValidation
  Scenario:  Verify whether error is displayed when invalid credentials are entered
    Given User opens salesforce login url
    When User enters invalid credentials
    Then User should see an error message