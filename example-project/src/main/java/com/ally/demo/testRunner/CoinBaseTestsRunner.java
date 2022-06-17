package com.ally.demo.testRunner;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

//test test runner

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/main/resources/features",
        glue = {"com.ally.d3.watchmen.steps", "com/ally/demo/steps"},
        tags ={"@CoinBase","not @skip"},
        plugin = {"pretty", "html:target/cucumber/bagbasics",
        "junit: target/cucumber/bagbasics/cucumber.xml", "json:target/cucumber-report/cucumber.json"}
        )

public class CoinBaseTestsRunner {
}
