@json
Feature: How to modify json on run time


   # Please note, it is a real scenario, the execution will get passed
#-------------------------------------------------------------------------------------------

  Scenario: Successfully modify JSON payload on a runtime

    # To see resulting JSON please check logs - the payload we sent
    # Please note that data/payloads/demo.json remains the same - all modification happens on runtime only


    And   I want to call API Endpoint "{{demoURL}}"
    And   I provide body as raw JSON file "data/payloads/demo.json"
    And   I set JSON body node to value:
      |applicant.firstName |"%randomString(10)%"         |
      |appDate             |"%yearsAgo(25,MM-dd-yyyy)%"|

    And   I add JSON node "email" to Parent node "" with value ""%randomEmail%""
    And   I set JSON body node "applicant.lastName" to ""%randomAlpha(3)%""
    And   I remove JSON body node "applicant.suffix"
    And   I copy JSON tree from "applicant" and add it under Parent node "" as new node "joint_applicant"
    And   I send "POST" request
    Then  Response has Status code: "405"






