package com.ally.demo.steps;

import com.ally.d3.watchmen.steps.CommonApiStepsDefinition;
import com.ally.d3.watchmen.utilities.RequestHelper;
import cucumber.api.java.en.And;
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
    private String oauth_URL;

    @Value("${consumer_key}")
    private String consumer_key;

    @Value("${consumer_secret}")
    private String consumer_secret;





    private static final Logger logger = LoggerFactory.getLogger(Authentication.class);

    @And("^I requested an access_token$")
    public void getAccessToken() {
        logger.info("Step: I requested an access_token");

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

//if response is 200 - retrieve and store in scope access_token
        watchmen.responseStatusCode(200);
        watchmen.storeBodyNodeInScenarioScope("access_token", "access_token");

        watchmen.clearMyPreviousAPICall();

    }


}

