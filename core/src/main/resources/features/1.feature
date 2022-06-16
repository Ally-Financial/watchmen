
Feature: First Test Set
@first
#-----------------------------------------------------------------------------------------------------------------------
  Scenario: Google

    Given I want to call API Endpoint "{{deployment_url}}"
    And I provide headers as csv file "data/headers.csv"
    When I send "GET" request
    Then Response has Status code: "200"
    And Response has response time < 5000 milliseconds
    And Response has header with the name: "X-Frame-Options"
    And Response body JSON matches schema "data/response.json"



