@web
Feature: How to test Web UI

 # Please note, it is just example scenarios, the execution will fail
 # To fix scenario you need to implement your own step to request access token

#-------------------------------------------------------------------------------------------

  Scenario: 1. Successfully launch Outlook

    # Expected: to be provided on the config.properties

               # spring.profiles.active=Chrome
               # waitForWebElementSeconds=10
               # driver.path=${path to the chrome driver}

               # outlookURL
               # outlookUser and outlookPwd

    Given I open Web Page "{{outlookURL}}"