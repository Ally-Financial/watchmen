@coinBase
Feature: CoinBase Currency

  Scenario: Get Currency codes and names

    Given I want to call API Endpoint "{{coinBaseCurrencyURL}}"
    And   I provide headers as csv file "data/headers/coinBase.csv"
    And  I send "GET" request
    Then Response has Status code: "200"
    And Response has ALL the headers from data Table:
    |Content-Type           |
    |Connection             |
    |Content-Security-Policy|
    And Response header "X-Frame-Options" has next value: "DENY"
    And Response body JSON matches schema "data/response_schemas/coinBase/getCoinBaseCurrency_200.json"
    And Response body JSON node equals to val:
    |data.get(id=AED).name|United Arab Emirates Dirham|
    |data.get(id=ALL).name|Albanian Lek               |
    |data.get(id=AMD).name|Armenian Dram              |
    |data.get(id=BYN).name|Belarusian Ruble           |
    |data.get(id=EGP).name|Egyptian Pound             |
    |data.get(id=HKD).name|Hong Kong Dollar           |
    |data.get(id=USD).name|US Dollar                  |



