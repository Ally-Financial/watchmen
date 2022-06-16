# Watchmen demo project



### Execute demo tests



* Check out feature file "demoTest" under resources/features.api_name/
* Scenarios you see are just demo scenarios in order to show different Watchmen steps and capabilities
* To run tests execute TestRunnerTemplate class under java/com.ally.d3.testRunner
* Or run command mvn test  -Dtest=TestRun
* Check out Cucumber report "index.html" under target/cucumber/bagbasics
* Check out request-response logs under targer for see real requests Watchmen sent and responses.
* Check out logs under logs/
* To get Cluecumber report run command mvn cluecumber-report:reporting
* Check out Cluecumber report index.html under target/generated-report


### Add new tests



* Create new Cucumber feature file under resources/features.api_name/
* Create new Scenarios reusing Watchmen Gherkin steps. Provide @tags.
* Check config.properties file under resources and provide your variables if needed
* Update TestRunnerTemplate class under java/com.ally.d3.testRunner (provide @tag you just have created)
* Execute TestRunnerTemplate class
* Or run command mvn test  -Dtest=TestRun
* Check out Cucumber report "index.html" under target/cucumber/bagbasics
* Check out request-response logs under targer for see real requests Watchmen sent and responses.
* Check out logs under logs/
* To get Cluecumber report run command mvn cluecumber-report:reporting
* Check out Cluecumber report index.html under target/generated-report


### Need help?



* Please see video tutorial: https://confluence.int.ally.com/download/attachments/66175140/FirstScenario.mp4?api=v2
* Please see Confluence page: https://confluence.int.ally.com/display/WAPIA/Watchmen+-+You+First+Scenario

