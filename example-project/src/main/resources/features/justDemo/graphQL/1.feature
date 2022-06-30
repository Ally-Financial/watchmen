@GraphQL
Feature: GraphQL

  Scenario: I provide body as GraphQL query

    Given I want to call API Endpoint "{{demoURL}}"
    And   I provide body as GraphQL query "query {\n  movies (id:"5ec2caaaa0f98451a25d1429") {\n    name\n  }\n}","variables":{}"
    When I send "GET" request
    Then Response has Status code: "200"


  Scenario: I provide body as GraphQL query with parameters as Data Table

    Given I want to call API Endpoint "{{demoURL}}"
    And   I provide body as GraphQL query "query {\n  movies (id:"${id}") {\n name\n }\n}","variables":{}" with parameters as Data Table:
      |id|%randomAlpha(5)%|
    When I send "GET" request
    Then Response has Status code: "200"

  Scenario: I provide body as GraphQL mutation

    Given I want to call API Endpoint "{{demoURL}}"
    And   I provide body as GraphQL mutation "mutation {\n  movies (id:"${id}") {\n    name\n  }\n}","variables":{}"
    When I send "GET" request
    Then Response has Status code: "200"


  Scenario: I provide body as GraphQL mutation with parameters as Data Table

    Given I want to call API Endpoint "{{demoURL}}"
    And   I provide body as GraphQL mutation "mutation {\n  movies (id:"${id}") {\n    name\n  }\n}","variables":{}" with parameters as Data Table:
      |id|%randomAlpha(5)%|
    When I send "GET" request
    Then Response has Status code: "200"