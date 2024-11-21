Feature: Account Creation

  Background:
    Given User opens salesforce login url
    When User enters valid credentials
    Then User should be logged in successfully
    And User chooses "Sales" from App Launcher

  @Regression @SanityTest
  Scenario Outline: Creation of new Account in Salesforce
    Given User clicks on "Accounts" tab
    When User clicks on new button in "Accounts" tab
    Then User creates new account by populating "<Account Name>" and "<Rating>" fields
    And User clicks on Save button in "Accounts" tab
    Then User verifies account is created successfully

    Examples:
      | Account Name      | Rating |
      | Test Automation 1 | Hot    |
      | Test Automation 2 | Cold   |
      | Test Automation 3 | Warm   |