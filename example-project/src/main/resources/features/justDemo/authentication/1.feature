@auth
Feature: How to authenticate request

 # Please note, it is just example scenarios, the execution will fail
 # To fix scenario you need to implement your own step to request access token

#-------------------------------------------------------------------------------------------

  Scenario: 1. Successfully authenticate request using Bearer token and new implemented step

          # Expected: demoURL, consumer_key, consumer_secret and oauth_URL are provided on the config.properties
          # Example step "I requested an access_token" is implemented under steps/Authentication
          # Step "I requested an access_token" requests access_token from oauth_URL and store token as "access_token" in the Scenario scope
          # Please note, it is just an example scenario, the execution will fail
          # To fix scenario you need to implement your own step to request access token

    Given I requested an access_token
    And   I want to call API Endpoint "{{demoURL}}"
    And   I provide my token "<access_token>" as a Bearer token on Authorization header
    And   I provide headers as data Table:

      |Content-Type|application/json; charset=utf-8|

    And   I send "GET" request
    Then  Response has Status code: "200"


 #-------------------------------------------------------------------------------------------

  Scenario: 2. Successfully authenticate request using Basic Auth

          # Expected: demoURL, user and pwd are provided on the config.properties
          # Please note, it is a real scenario, the execution will get passed

    And   I want to call API Endpoint "{{demoURL}}"
    And   I provide user name "{{user}}" and password "{{pwd}}" as a Basic authentication on Authorization header
    And   I provide headers as data Table:

      |Content-Type|application/json; charset=utf-8|

    And    I send "GET" request
    Then   Response has Status code: "200"

    Then   Response has Status code: "200"