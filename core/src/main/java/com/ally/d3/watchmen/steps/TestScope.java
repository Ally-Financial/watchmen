/*
 * Copyright 2022 Ally Financial, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ally.d3.watchmen.steps;

import com.ally.d3.watchmen.utilities.RequestHelper;
import com.ally.d3.watchmen.utilities.RestAssuredHelper;
import com.ally.d3.watchmen.utilities.dataDriven.PlaceholderResolve;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Component

//This object need to share state between steps on one scenario
//all variables will be reset for every new scenario on Before hook

public class TestScope {

    @Autowired
    RestAssuredHelper restAssuredHelper;

    @Value("${useProxy:false}")
    private Boolean useProxy;


    private ValidatableResponse responseToValidate;

    private ResponseOptions<Response> response;

    private RequestSpecification requestSpec;

    private StringBuilder requestBasePath =  new StringBuilder();

    private Map<String, String> container = new HashMap<>();

    private Map<String, JsonNode> jsonContainer = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(TestScope.class);


    public ValidatableResponse getResponseToValidate() { return responseToValidate; }

    public ResponseOptions<Response> getResponse() { return response; }

    public RequestSpecification getRequestSpec() { return requestSpec; }

    public StringBuilder getRequestBasePath() { return requestBasePath; }

    public Map<String, String> getContainer() { return container; }

    public Map<String, JsonNode> getJsonContainer() { return jsonContainer; }



    public void setResponseToValidate(ValidatableResponse rq)
    {
        this.responseToValidate = rq;
    }

    public void setResponse(ResponseOptions<Response> r)
    {
        this.response = r;
    }

    public void setRequestSpec(RequestSpecification rs)
    {
        this.requestSpec = rs;
    }

    public void setRequestBasePath(StringBuilder bp)
    {
        this.requestBasePath = bp;
    }

    public void setContainer(Map<String, String> c)
    {
        this.container = c;
    }

    public void setJsonContainer(Map<String, JsonNode> jn)
    {
        this.jsonContainer = jn;
    }



    //To avoid leaking between scenarios need to reset all variables
    public void reset (){

        setResponse(null);
        setResponseToValidate(null);
        setRequestBasePath(new StringBuilder());
        setContainer(new HashMap<>());
        setJsonContainer (new HashMap<>());
        setRequestSpec(restAssuredHelper.startNewRequestSpecification(useProxy));
    }

    //If you need to chain few API calls in one scenario
    public void resetPreviousAPICall(){

        setResponse(null);
        setRequestSpec(restAssuredHelper.startNewRequestSpecification(useProxy));
        setResponseToValidate(null);
        setRequestBasePath(new StringBuilder());
    }

    public void resetPreviousAPICallAndSwitchProxy(){

        setResponse(null);
        setRequestSpec(restAssuredHelper.startNewRequestSpecification(!useProxy));
        setResponseToValidate(null);
        setRequestBasePath (new StringBuilder());

    }

    public void saveInContainer(String key, String  val){

        getContainer().put(key,val);
        logger.debug("Save in container "+key+" = "+val);
        logger.trace(" \nSave in container "+key+" = "+val);
        logger.info(" \nSave in container "+key+" = "+val);
    }

    public String getFromContainer(String key){

        if (getContainer().containsKey(key)) return getContainer().get(key);
        else {
            logger.error("Was not able to get "+key+" from Scenario Scope");
            throw new RuntimeException("was not able to get "+key+" from Scenario Scope");
        }

    }

    public void saveInJsonContainer(String key, JsonNode  json){

        getJsonContainer().put(key,json);
        logger.debug("Save to JSON container "+key);
        logger.debug("Save to the JSON container "+key);
    }

    public JsonNode getFromJsonContainer(String key){

        if (getJsonContainer().containsKey(key)) return getJsonContainer().get(key);
        else {
            logger.error("Was not able to get "+key+" from Json Container");
            throw new RuntimeException("was not able to get "+key+" from Json Container");
        }

    }

}


