package com.ally.d3.steps;

import com.ally.d3.watchmen.steps.CommonApiStepsDefinition;
import com.ally.d3.watchmen.steps.TestScope;
import com.ally.d3.watchmen.utilities.RequestHelper;
import com.ally.d3.watchmen.utilities.dataDriven.PlaceholderResolve;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;


public class Authentication {


    @Autowired
    private CommonApiStepsDefinition watchmen;

    @Autowired
    private RequestHelper requestHelper;


    @Value("${oauth_URL}")
    private String acs_oauth_URL;

    @Value("${consumer_key}")
    private String consumer_key;

    @Value("${consumer_secret}")
    private String consumer_secret;





    private static final Logger logger = LoggerFactory.getLogger(Authentication.class);

    @And("^I got an Access Token$")
    public void getAccessToken() {
        logger.info("Step: I Got an Access Token");

        watchmen.iWantToTestURL(oauth_URL);
        watchmen.provideQueryParametersAs("grant_type","client_credentials");

//add headers
        Map<String, String> authHeaders = new HashMap<>();
        authHeaders.put("Content-Type", "application/x-www-form-urlencoded");
        requestHelper.addHeadersToRequest(authHeaders);


//add form data
        Map<String, String> body = new HashMap<>();
        body.put("client_id", consumer_key);
        body.put("client_secret", consumer_secret);

        requestHelper.addFormDataToRequest(body);

        watchmen.sendRequest("POST");
        watchmen.responseStatusCode(200);
        watchmen.storeBodyNodeInScenarioScope("access_token", "access_token");

        watchmen.clearMyPreviousAPICall();

    }


}

