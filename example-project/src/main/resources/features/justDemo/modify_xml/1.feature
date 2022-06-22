@xml
Feature: How to modify xml on run time


   # Please note, it is a real scenario, the execution will get passed
#-------------------------------------------------------------------------------------------

  Scenario: Successfully modify XML payload on a runtime

    # To see resulting xml please check logs - the payload we sent
    # Please note that data/payloads/demo.xml remains the same - all modification happens on runtime only


    And   I want to call API Endpoint "{{demoURL}}"
    And   I provide headers as data Table:
       | Content-Type | text/xml;charset=UTF-8            |
       | SOAPAction   | "http://ally.com/ws/AddCustomers" |
    And  I provide body as raw XML file "data/payloads/demo.xml"
    And  I set XML body node "//CustomerInfo/Party/TaxIdentificationNumber" to "%uniqueSSN()%"
    And  I set XML body node "//CustomerInfo/Party/BirthDate" to "%yearsAgo(40)%"
    And  I remove XML body node "(//CustomerInfo/Party/Contact)[1]"
    And  I remove XML body node "(//CustomerInfo/Party/Contact)[2]"
    And  I remove XML body node "//CustomerInfo/Party/PersonName/NameSuffix"
    And  I remove XML body node "//CustomerInfo/Party/MothersMaidenName"
    When I send "POST" request
    Then Response has Status code: "405"

