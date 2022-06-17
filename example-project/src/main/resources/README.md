# Watchmen demo project



### Execute demo tests for open public API CoinBase "Currency codes and names"



* Check out feature file "coinBase" under resources/features
* Scenarios you see are just demo scenarios in order to show different Watchmen steps and capabilities
* To run tests execute CoinBaseTestsRunner class under java/com.ally.demo.testRunner
* Or run command mvn test  -Dtest=CoinBaseTestsRunner
* Check out Cucumber report "index.html" under target/cucumber/bagbasics
* Check out logs under logs/
* To get Cluecumber report run command mvn cluecumber-report:reporting
* Check out Cluecumber report index.html under target/generated-report


### Add new tests



* Create new Cucumber feature file under resources/features.api_name/
* Create new Scenarios reusing Watchmen Gherkin steps. Provide @tags.
* Check config.properties file under resources and provide your variables if needed
* Update CoinBaseTestsRunner class under java/com.ally.demo.testRunner (provide @tag you just have created)
* Execute CoinBaseTestsRunner class
* Or run command mvn test  -Dtest=CoinBaseTestsRunner
* Check out Cucumber report "index.html" under target/cucumber/bagbasics
* Check out logs under logs/
* To get Cluecumber report run command mvn cluecumber-report:reporting
* Check out Cluecumber report index.html under target/generated-report



