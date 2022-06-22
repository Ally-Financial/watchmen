@web
Feature: How to test Web UI

 # Please note, it is just example scenarios, the execution will fail
 # To fix scenario you need to add Chrome driver, create your own outlookUser and send email you want to validate

#-------------------------------------------------------------------------------------------

  Scenario: 1. Successfully launch Outlook and validate email Header

    # Expected: to be provided on the config.properties

               # spring.profiles.active=Chrome
               # waitForWebElementSeconds=10
               # driver.path=${path to the chrome driver}

               # outlookURL
               # outlookUser and outlookPwd

    Given I open Outlook mailbox using user "{{outlookUser}}" and password "{{outlookPwd}}" and navigate to the latest email
    Then  I navigate to the element by Id "x_greetingId" and verify Text equals to "Hi Test,"
    Then  I navigate to the element by Id "x_heroId" and verify Text contains "We need your VIN to continue."



