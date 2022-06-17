@CoinBase
Feature: CoinBase Products

  https://docs.cloud.coinbase.com/exchange/reference/exchangerestapi_getproducts

#------------------------------------------------------------------------------

  Scenario: Get a list of available currency pairs for trading.

    Given I want to call API Endpoint "{{coinBaseProductsURL}}"
    And   I provide headers as csv file "data/headers/coinBase.csv"
    And   I send "GET" request
    Then  Response has Status code: "200"
    And   Response has ALL the headers from data Table:
    |Content-Type           |
    |Connection             |
    |Transfer-Encoding      |
    And Response header "X-Frame-Options" has next value: "SAMEORIGIN"
    And Response body JSON matches schema "data/response_schemas/getCoinBaseProducts_200.json"
    And Response body JSON node equals to val:
    |get(id=AUCTION-EUR).quote_currency|EUR        |
    |get(id=AUCTION-EUR).base_currency |AUCTION    |
    |get(id=AUCTION-EUR).display_name  |AUCTION/EUR|

#------------------------------------------------------------------------------

  Scenario Outline: <id> - Get information on a single product.

    Given I want to call API Endpoint "{{coinBaseProductsURL}}"
    And   I provide basePath as "<id>"
    And   I provide headers as csv file "data/headers/coinBase.csv"
    And   I send "GET" request
    Then  Response has Status code: "200"
    And   Response has ALL the headers from data Table:
      |Content-Type           |
      |Connection             |
      |Transfer-Encoding      |
    And Response header "X-Frame-Options" has next value: "SAMEORIGIN"
    And Response body JSON matches schema "data/response_schemas/getCoinBaseSingleProduct_200.json"
    And Response body JSON node equals to val:
      |quote_currency|<quote_currency> |
      |base_currency |<base_currency>  |
      |display_name  |<display_name>   |

    Examples:
    |id          |quote_currency|base_currency|display_name|
    |AUCTION-EUR |EUR           |AUCTION      |AUCTION/EUR |
    |AUCTION-USDT|USDT          |AUCTION      |AUCTION/USDT|
    |REN-USD     |USD           |REN          |REN/USD     |

 #------------------------------------------------------------------------------


  Scenario Outline: <id> - Get information on a single product and validate it matches with Get All products

    Given I want to call API Endpoint "{{coinBaseProductsURL}}"
    And   I provide headers as csv file "data/headers/coinBase.csv"
    And   I send "GET" request
    Then  Response has Status code: "200"
    And   Response body JSON matches schema "data/response_schemas/getCoinBaseProducts_200.json"
    And   I store JSON Response body node "get(id=<id>).quote_currency" as "stored-quote_currency" in the scenario scope
    And   I store JSON Response body node "get(id=<id>).base_currency" as "stored-base_currency" in the scenario scope
    And   I store JSON Response body node "get(id=<id>).display_name" as "stored-display_name" in the scenario scope

    And I clear my previous API call

    Given I want to call API Endpoint "{{coinBaseProductsURL}}"
    And   I provide basePath as "<id>"
    And   I provide headers as csv file "data/headers/coinBase.csv"
    And   I send "GET" request
    Then  Response has Status code: "200"
    And   Response body JSON matches schema "data/response_schemas/getCoinBaseSingleProduct_200.json"
    And   Response body JSON node equals to val:
      |quote_currency|<stored-quote_currency> |
      |base_currency |<stored-base_currency>  |
      |display_name  |<stored-display_name>   |

    Examples:
      |id          |
      |AUCTION-EUR |
      |AUCTION-USDT|
      |REN-USD     |

 #------------------------------------------------------------------------------

  Scenario: Error to get information on a single product.

    Given I want to call API Endpoint "{{coinBaseProductsURL}}"
    And   I provide basePath as "%randomString(25)%"
    And   I provide headers as csv file "data/headers/coinBase.csv"
    And   I send "GET" request
    Then  Response has Status code: "404"
    And Response body JSON node equals to val:
      |message|NotFound|


