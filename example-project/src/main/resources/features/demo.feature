@demo
Feature: Very first scenario

  Scenario: 1 scenario

    Given I got an Access Token
    And   I want to test URL "{{baseURL}}
    And   I provide headers as data Table:

      |Content-Type|application/json; charset=utf-8   |

    And  I send "GET" request
    Then Response has Status code: "200"
    And  I store Response header "X-Frame-Options" as "Frame-Options" in scenario scope

    




