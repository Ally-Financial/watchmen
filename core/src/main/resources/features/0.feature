Feature: Very first scenario
  @first
  Scenario: very first scenario

    Given I want to call API Endpoint "https://www.google.com"
    And   I provide headers as data Table:

      |Content-Type|application/json; charset=utf-8|

    And I send "GET" request
    Then Response has Status code: "200"