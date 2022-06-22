@oracle
Feature:How to connect to Oracle DB


   # Please note, it is not a real scenario, the execution will fail
   # To fix scenario you need to provide OracleDB connection and valid sql query
#-------------------------------------------------------------------------------------------

  Scenario: Successfully modify JSON on runtime

          # Expected: to be provided on the config.properties

               # spring.profiles.active=OracleDB
               # max.attempts.query.db=5

               # oracle.datasource.url=XXXXXXXXXXX
               # oracle.datasource.username=XXXXXX
               # oracle.datasource.password=${oracle.password}

               # demoURL

    And   I want to call API Endpoint "{{demoURL}}"
    And   I provide headers as data Table:

      |Content-Type|application/json; charset=utf-8|

    When  I send "GET" request
    Then  Response has Status code: "200"
    And   I store JSON Response body node "status" as "status_received" in the scenario scope
    And   I store Response header "CF-RAY" as "CorrelationId" in the scenario scope

    And   I establish connection with Database "OracleDB"
    And   I query for String "SELECT STATUS_CD FROM ALERT_FILE WHERE FILE_UPLOAD_REQUEST_ID = 1234567 AND STATUS_CD = 2000" and store result as "result_1" in the scenario scope
    And   I query for Integer "data/sql/select_customer.sql" with parameters as Data Table and store result as "result_2" in the scenario scope:
      | corrId     | <CorrelationId>  |
      | customerId | 235455676        |
      | status     | <status_received>|

    And   I assert that string "<result_1>" is equal to "Approved"
    And   I assert that numeric "result_2" is equal to "2000"