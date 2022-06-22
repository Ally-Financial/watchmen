@chain
Feature: How to chain few API calls in one scenario

   # Please note, it is a real scenario, the execution will get passed
#-------------------------------------------------------------------------------------------

  Scenario Outline: <id> - Get information on a single product and validate it matches with Get All products

    Given I want to call API Endpoint "{{coinBaseProductsURL}}"
    And   I provide headers as csv file "data/headers/coinBase.csv"
    And   I send "GET" request
    Then  Response has Status code: "200"
    And   Response body JSON matches schema "data/response_schemas/coinBase/getCoinBaseProducts_200.json"
    And   I store JSON Response body node "get(id=<id>).quote_currency" as "stored-quote_currency" in the scenario scope
    And   I store JSON Response body node "get(id=<id>).base_currency" as "stored-base_currency" in the scenario scope
    And   I store JSON Response body node "get(id=<id>).display_name" as "stored-display_name" in the scenario scope

    And I clear my previous API call

    Given I want to call API Endpoint "{{coinBaseProductsURL}}"
    And   I provide basePath as "<id>"
    And   I provide headers as csv file "data/headers/coinBase.csv"
    And   I send "GET" request
    Then  Response has Status code: "200"
    And   Response body JSON matches schema "data/response_schemas/coinBase/getCoinBaseSingleProduct_200.json"
    And   Response body JSON node equals to val:
      |quote_currency|<stored-quote_currency> |
      |base_currency |<stored-base_currency>  |
      |display_name  |<stored-display_name>   |

    Examples:
      |id          |
      |AUCTION-EUR |
      |AUCTION-USDT|
      |REN-USD     |


