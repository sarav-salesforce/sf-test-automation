Feature: Contact Creation

  Background:
    Given User opens salesforce login url
    When User enters valid credentials
    Then User should be logged in successfully
    And User chooses "Sales" from App Launcher

  @Regression
  Scenario: Creation of new Contact in Salesforce
    Given User clicks on "Contacts" tab
    When User clicks on new button in "Contacts" tab
    Then User creates new contact by populating mandatory fields
    And User clicks on Save button in "Contacts" tab
