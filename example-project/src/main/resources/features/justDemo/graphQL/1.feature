@GraphQL
Feature: Countries GraphQL

  https://studio.apollographql.com/public/countries/home?variant=current

  Scenario: Request all continents, countries and languages
    Given I want to call API Endpoint "{{countriesGraphQL}}"
    And   I provide headers as data Table:
      |content-type|application/json|
    And   I provide body as GraphQL query "query ExampleQuery {continents {code} countries {code} languages {code}}"
    When  I send "POST" request
    Then  Response has Status code: "200"



  Scenario Outline: Request <code> country information
    Given I want to call API Endpoint "{{countriesGraphQL}}"
    And   I provide headers as data Table:
      |content-type|application/json|
    And   I provide body as GraphQL query "query Query {country(code:"${code}"){name native capital emoji currency languages {code name}}}" with parameters as Data Table:
      |code|<code>|
    When  I send "POST" request
    Then  Response has Status code: "200"
    And   Response body JSON node equals to val:
      |data.country.name|<name>|
      |data.country.capital|<capital>|
      |data.country.currency|<currency>|


    Examples:
      |code|name         |capital        |currency   |
      |BR  |Brazil       |Brasília       |BRL        |
      |US  |United States|Washington D.C.|USD,USN,USS|
      |AM  |Armenia      |Yerevan        |AMD        |

  @ql
  Scenario: Request country information for non existing country
    Given I want to call API Endpoint "{{countriesGraphQL}}"
    And   I provide headers as data Table:
      |content-type|application/json|
    And   I provide body as GraphQL query "query Query {country(code:"${code}"){name native capital emoji currency languages {code name}}}" with parameters as Data Table:
      |code|%randomAlpha(3)%|
    When  I send "POST" request
    Then  Response has Status code: "200"
    And   Response body JSON node equals to val:
      |data.country|null|

  @ql
  Scenario: Invalid request for country information
    Given I want to call API Endpoint "{{countriesGraphQL}}"
    And   I provide headers as data Table:
      |content-type|application/json|
    And   I provide body as GraphQL query "query Query {country(code:"${code}"){name456 native capital emoji currency languages {code name}}}" with parameters as Data Table:
      |code|US|
    When  I send "POST" request
    Then  Response has Status code: "400"
    And   Response body JSON node equals to val:
      |errors.get(1).extensions.code|GRAPHQL_VALIDATION_FAILED|
    And Response body JSON node contains String:
      |errors.get(1).message|Cannot query field \"name456\"