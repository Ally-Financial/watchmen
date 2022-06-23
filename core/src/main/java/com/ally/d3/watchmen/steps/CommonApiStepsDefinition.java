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
import com.ally.d3.watchmen.utilities.*;
import com.ally.d3.watchmen.utilities.dataDriven.*;
import com.ally.d3.watchmen.utilities.DataBaseHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.jayway.awaitility.Duration;
import com.jayway.awaitility.core.ConditionTimeoutException;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.NumberUtils;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;


public class CommonApiStepsDefinition {

    private static final Logger logger = LoggerFactory.getLogger(CommonApiStepsDefinition.class);

    //Inject dependency - to create an instance of class RestAssuredMagic
    @Autowired
    RestAssuredHelper restAssuredHelper;


    //Inject dependency - to create an instance of class  ContractValidateHelper
    @Autowired
    ResponseHelper responseHelper;

    //Inject dependency - to create an instance of class  RequestHelper
    @Autowired
    RequestHelper requestHelper;

    @Autowired
    JsonHelper jsonHelper;

    @Autowired
    XMLHelper xmlHelper;

    //Inject dependency - to share state between steps in one scenario
    @Autowired
    TestScope testScope;

    @Autowired
    ReadFile readFile;

    @Autowired
    DataBaseHelper dataBaseHelper;

    @Autowired
    DefaultDataHelper defaultDataHelper;

    @Autowired
    AuthorizationHelper authorizationHelper;

    @Value("${useRelaxedHTTPSValidation:false}")
    private boolean useRelaxedHTTPSValidation;

    @Value("${waitForResponseSeconds:5}")
    private Integer waitForResponseSeconds;



    @Before
    public void beforeScenario(Scenario scenario) {

        logger.info("\n ---------------------------------------------------------------------------------"
                +" \n ------------ Scenario ID   : " + scenario.getId()
                +" \n ------------ Scenario Name : " + scenario.getName()
                + "\n ---------------------------------------------------------------------------------");

        logger.trace(" \n"
                +" \n ******************************************************************************************************************************************************************"
                +" \n"
                +" \n -------------> Scenario Tags:   " + scenario.getSourceTagNames()
                +" \n -------------> Scenario Name:   " + scenario.getName()
                +" \n -------------> Scenario Id  :   " + scenario.getId()
                +" \n"
                        +" \n ******************************************************************************************************************************************************************");

        //To avoid leaking between scenarios need to reset all variables
        testScope.reset();

        if (useRelaxedHTTPSValidation)
        RestAssured.useRelaxedHTTPSValidation();

        //If you want to have certs validated - create Java keystore file and use it
        //RestAssured.keystore("/pathToJksInClassPath", "<password>"
    }


    @After
    public void afterScenario(Scenario scenario) {

        logger.info("SCENARIO PASSED: " + !scenario.isFailed());
    }


//--------------------------------------------------------------------------------------------------------
    //Request specification
//--------------------------------------------------------------------------------------------------------

    //Start request specification for hardcoded URL

    @Given("^I want to call API Endpoint \"([^\"]*)\"$")
    public void iWantToTestURL(String url) {

        logger.debug("Check if there are any placeholder for URL and replace it");
        String newURL = requestHelper.resolveAllPlaceholdersURL(url);

        logger.info("Step: I want to call API Endpoint " + newURL);
        //specify requestSpecification
        requestHelper.startBuildingRequestForAPI(newURL);
    }

    @And("^I provide basePath as \"([^\"]*)\"$")
    public void provideBasePathAs(String basePathToAdd) {
        logger.info("Step: I provide basePath as " + basePathToAdd);

        //Check if there are any placeholder for property and replace it
        //String newBasePathToAdd= placeholderResolve.replacePropertiesString(basePathToAdd);

        String newBasePathToAdd = requestHelper.resolveAllPlaceholdersURL(basePathToAdd);

        requestHelper.addBasePathToRequest( newBasePathToAdd);
    }

    //Header Authorization = Bearer followed by a space and token
    @And("^I provide token \"([^\"]*)\" as a Bearer token on Authorization header$")
    public void provideAuthToken(String token) {

        logger.info("Step: I provide token "+token+" as a Bearer token on Authorization header");

        String newToken = requestHelper.resolveAllPlaceholdersURL(token);
        authorizationHelper.setAuthorizationHeaderWithBearerToken(newToken);
    }

    //Header Authorization = "Basic" followed by a space and a base64-encoded string user:password
    @And("^I provide user name \"([^\"]*)\" and password \"([^\"]*)\" as a Basic authentication on Authorization header$")
    public void provideBasicAuthenticationOnAuthorizationHeader(String user, String password) {
        logger.info("Step: I provide user name "+user+" and password "+password+"+ as a Basic authentication on Authorization header");
        String newUser = requestHelper.resolveAllPlaceholdersURL(user);
        String newPassword = requestHelper.resolveAllPlaceholdersURL(password);

        authorizationHelper.setAuthorizationHeaderWithBasicAuth(newUser,newPassword);
    }


    //Add headers to the request

    @And("^I provide headers as data Table:$")
    public void provideHeadersAs(DataTable headersTable) {
        //log
        logger.info("Step: I provide headers as data Table");

        //Read Cucumber Data Table
        Map<String, String> headers = headersTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newHeaders = requestHelper.resolveAllPlaceholders(headers);
        requestHelper.addHeadersToRequest(newHeaders);
    }


    @And("^I provide headers as csv file \"([^\"]*)\"$")
    public void provideHeadersAsCsvFile(String file) {


        logger.debug("Check if there are placeholder {{ }} for file name and replace it");
        String newFile = requestHelper.resolveAllPlaceholdersURL(file);

        logger.info("Step: I provide headers as csv file " + newFile);

        //Read headers from file - first two columns
        Map<String, String> headers = readFile.readFirstTwoColumnsCSVtoMap(newFile);

        //Resolve placeholders
        Map<String, String> newHeaders = requestHelper.resolveAllPlaceholders(headers);
        requestHelper.addHeadersToRequest(newHeaders);
    }


    //add cookies to Request
    @And("^I provide cookies as data Table:$")
    public void provideCookiesAs(DataTable cookiesTable) {

        logger.info("Step: I provide cookies as data Table");

        //Read Cucumber Data Table
        Map<String, String> cookies = cookiesTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newCookies = requestHelper.resolveAllPlaceholders(cookies);
        requestHelper.addCookiesToRequest(newCookies);
    }

    @And("^I provide cookies as csv file \"([^\"]*)\"$")
    public void provideCookiesAsCsvFile(String file) {


        logger.debug("Check if there are placeholder {{ }} for file name and replace it");
        String newFile = requestHelper.resolveAllPlaceholdersURL(file);

        logger.info("Step: I provide cookies as csv file " + newFile);

        //Read headers from file - first two columns
        Map<String, String> cookies = readFile.readFirstTwoColumnsCSVtoMap(newFile);

        //Resolve placeholders
        Map<String, String> newCookies = requestHelper.resolveAllPlaceholders(cookies);
        requestHelper.addCookiesToRequest(cookies);
    }


    //Add body to the request

    @And("^I provide body as String: \"(.*)\"$")
    public void provideBodyAsString(String body) {
        logger.info("Step: I provide body as String");

        //Check if there are placeholder {{ }} for property and replace it
        String newBody = requestHelper.resolveAllPlaceholdersURL(body);

        requestHelper.addBodyAsStringToRequest(newBody);
    }

    //Add body to the request

    @And("^I provide body as file \"([^\"]*)\" converted to Byte Array$")
    public void provideBodyAsFileToByte(String file) {

        //Check if there are placeholder {{ }} for property and replace it
        String newFile = requestHelper.resolveAllPlaceholdersURL(file);

        logger.info("Step: I provide body as file "+newFile+" converted to Byte array");

        requestHelper.addBodyAsFileToByteArray(newFile);
    }


    @And("^I provide body as raw JSON file \"([^\"]*)\"$")
    public void provideBodyAsRawJSON(String file) {

        logger.debug("Check if there are placeholder {{ }} for file name and replace it");
        String newFile = requestHelper.resolveAllPlaceholdersURL(file);

        logger.info("Step: I provide body as raw JSON file " + newFile);
        String stringBody = readFile.readFileAsString(newFile);

        requestHelper.addJSONBodyAsStringToRequest(stringBody);
    }


    //Set Json Body field with path to new value
    //In case of error bodyJsonTree and Body remain the same and step does not fail
    //To make sure body got updated - check the logs

    @And("^I set JSON body node \"([^\"]*)\" to \"(.*)\"$")
    public void setJsonNodeToString(String path, String value) {
        logger.info("Step: I set JSON body node " + path + " to " + value);

        //Resolve placeholders
        String newValue = requestHelper.resolveAllPlaceholders(value);
        requestHelper.setBodyNodeToString(path, newValue);
    }


    @And("^I set JSON body node to value:$")
    public void setJsonNodeToString(DataTable dataTable) {
        logger.info("Step: I set JSON body node to value:");

        //Read Cucumber Data Table
        Map<String, String> nodes = dataTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newNodes = requestHelper.resolveAllPlaceholders(nodes);

        //Set node
        Set<String> keys = newNodes.keySet();
        for (String node : keys) {
            String newNodeVal = newNodes.get(node);
            requestHelper.setBodyNodeToString(node, newNodeVal);
        }
    }



    @And("^I add JSON node \"([^\"]*)\" to Parent node \"([^\"]*)\" with value \"(.*)\"$")
    public void addNodeToJSONbody(String node, String parentPath, String value) {
        logger.info("Step: I add JSON node " + node + " to Parent node " + parentPath + " with value " + value);

        //Resolve placeholders
        String newValue = requestHelper.resolveAllPlaceholders(value);
        requestHelper.addNodeToJSON(parentPath, node, newValue);
    }

    @And("^I add JSON node \"([^\"]*)\" to Parent node \"([^\"]*)\" with value as raw JSON \"(.*)\"$")
    public void addNodeToJSONbodyFromJson(String node, String parentPath, String jsonFile) {
        logger.info("Step: I add JSON node " + node + " to Parent node " + parentPath + " with value as raw JSON " + jsonFile);

        //Resolve placeholders
        String newJsonFile = requestHelper.resolveAllPlaceholdersURL(jsonFile);
        String newNodeValue = jsonHelper.readJSONPayloadFromFile(newJsonFile);
        requestHelper.addNodeToJSON(parentPath, node, newNodeValue);
    }

    @And("^I add new item to JSON Array node \"([^\"]*)\" with value as raw JSON \"(.*)\"$")
    public void addNodeToArrayJSONbodyFromJson(String parentPath, String jsonFile) {
        logger.info("Step: I add new item to JSON Array node " + parentPath + " with value as raw JSON " + jsonFile);

        //Resolve placeholders
        String newJsonFile = requestHelper.resolveAllPlaceholdersURL(jsonFile);
        String newNodeValue = jsonHelper.readJSONPayloadFromFile(newJsonFile);
        requestHelper.addNodeToArrayJSON(parentPath, newNodeValue);
    }


    @And("^I copy JSON tree from \"([^\"]*)\" and add it under Parent node \"([^\"]*)\" as new node \"([^\"]*)\"$")
    public void copyJSONNodeAddToParentNode(String parentNodePath, String newParentNodePath, String newNode)  {
        logger.info("Step: I copy JSON tree from " + parentNodePath + " and add it under Parent node " + newParentNodePath+" as new node: "+newNode);
        requestHelper.copyNodeAddAsNewNode(parentNodePath,newParentNodePath,newNode);
    }


    @And("^I copy JSON tree from \"([^\"]*)\" and add it under Array node \"([^\"]*)\"$")
    public void copyJSONNodeAddToArray(String parentNodePath, String newParentNodePath)  {
        logger.info("Step: I copy JSON tree from " + parentNodePath + " and add it under Array node " + newParentNodePath);
        requestHelper.copyNodeAddAsArray(parentNodePath,newParentNodePath);
    }



    @And("^I remove JSON body node \"([^\"]*)\"$")
    public void removeNodeFromJSONBody(String path) {
        logger.info("Step: I remove JSON body node " + path);

        requestHelper.removeNodeFromJSON(path);
    }


    //For SOAP request - xml body
    @And("^I provide body as raw XML file \"([^\"]*)\"$")
    public void provideBodyAsRawXMLFile(String file) {

        logger.debug("Check if there are placeholder {{ }} for the file name and replace it");
        String newFile = requestHelper.resolveAllPlaceholdersURL(file);

        logger.info("Step: I provide body as raw XML file " + newFile);
        //read XML file as String
        String stringBody = readFile.readFileAsString(newFile);

        requestHelper.addXMLBodyAsStringToRequest(stringBody);
    }


    @And("^I set XML body node \"([^\"]*)\" to \"(.*)\"$")
    public void setXmlNodeToString(String path, String value) {
        logger.info("Step: I set XML body node: " + path + " to " + value);

        //Resolve placeholders
        String newValue = requestHelper.resolveAllPlaceholders(value);
        requestHelper.setXmlBodyNodeToString(path, newValue);
    }

    @And("^I set XML body node to value:$")
    public void setXmlNodeToString(DataTable dataTable) {
        logger.info("Step: I set XML body node to value:");

        //Read Cucumber Data Table
        Map<String, String> nodes = dataTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newNodes = requestHelper.resolveAllPlaceholders(nodes);

        //Set node
        Set<String> keys = newNodes.keySet();
        for (String node : keys) {
            String newNodeVal = newNodes.get(node);
            requestHelper.setXmlBodyNodeToString(node, newNodeVal);
        }
    }


    @And("^I add XML node \"([^\"]*)\" to Parent node \"([^\"]*)\" with value \"(.*)\"$")
    public void addNodeToXmlBody(String node, String parentPath, String value) {
        logger.info("Step: I add XML node " + node + " to Parent node " + parentPath + " with value " + value);

        //Resolve placeholders
        String newValue = requestHelper.resolveAllPlaceholders(value);

        requestHelper.addNodeToXml(parentPath, node, newValue);
    }

    @And("^I add attribute \"([^\"]*)\" to XML node \"([^\"]*)\" with value \"(.*)\"$")
    public void addNodeAttrToXmlBody(String attr, String node, String value) {
        logger.info("Step: I add attribute " + attr + " to XML node " + node + " with value " + value);

        //Resolve placeholders
        String newValue = requestHelper.resolveAllPlaceholders(value);

        requestHelper.addNodeAttrToXml(attr, node, newValue);
    }


    @And("^I remove XML body node \"([^\"]*)\"$")
    public void removeNodeFromXmlBody(String path) {
        logger.info("Step: I remove XML body node " + path);

        requestHelper.removeNodeFromXML(path);
    }

    @And("^I remove attribute \"([^\"]*)\" from XML body node \"([^\"]*)\"$")
    public void removeNodeAttribureFromXmlBody(String attribute, String path) {
        logger.info("Step: I remove attribute: " + attribute + "from XML body node " + path);

        requestHelper.removeNodeAttrFromXML(path, attribute);
    }


    @And("^I provide form data as data Table:$")
    public void provideFormDataAs(DataTable dataTable) {
        //log
        logger.info("Step: I provide form data as data Table");
        Map<String, String> formdata = dataTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newFormData = requestHelper.resolveAllPlaceholders(formdata);
        requestHelper.addFormDataToRequest(newFormData);
    }


    @And("^I provide form data as csv file \"([^\"]*)\"$")
    public void provideFormDataAsCsvFile(String file) {

        logger.debug("Check if there are placeholder {{ }} for file name and replace it");
        String newFile = requestHelper.resolveAllPlaceholdersURL(file);

        logger.info("Step: I provide form data as csv file " + newFile);

        //Read form data from file - first two columns
        Map<String, String> formdata = readFile.readFirstTwoColumnsCSVtoMap(newFile);

        //Resolve placeholders
        Map<String, String> newFormData = requestHelper.resolveAllPlaceholders(formdata);

        requestHelper.addFormDataToRequest(newFormData);
    }


    @And("^I provide path variables as data Table:$")
    public void providePathVariablesAs(DataTable variablesTable) {

        logger.info("Step: I provide path variables as data Table");

        //LinkedHashMap will return the elements in the order they were inserted into the map
        Map<String, String> variables = new LinkedHashMap<>();
        variables = variablesTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newVariables = requestHelper.resolveAllPlaceholders(variables);

        requestHelper.addPathVariablesToRequest(newVariables);
    }


    // Add Query parameters to the request

    @And("^I provide query parameters as data Table:$")
    public void provideQueryParametersAs(DataTable paramTable) {

        logger.info("Step: I provide query parameters as data Table");
        Map<String, String> params = paramTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newParams = requestHelper.resolveAllPlaceholders(params);

        requestHelper.addQueryParamToRequest(newParams);
    }


    @And("^I provide query parameter \"([^\"]*)\" as \"([^\"]*)\"$")

    public void provideQueryParametersAs(String key, String listVal) {

        logger.info("Step: I provide query parameter: "+key+" as: "+listVal);

        String[] paramValues = null;
        paramValues = listVal.split(",");

        for (int i = 0; i < paramValues.length; i++) {

            String newParamValue = requestHelper.resolveAllPlaceholders(paramValues[i]);
            logger.debug("RequestSpec add queryParam " + key + " = " + newParamValue);
            requestHelper.addQueryParamToRequest(key, newParamValue);
        }
    }



//--------------------------------------------------------------------------------------------------------
    //Send Request
//--------------------------------------------------------------------------------------------------------

    //Execure Request

    @When("^I send \"([^\"]*)\" request$")
    public void sendRequest(String requestType) {

        logger.info("Step: I send " + requestType + " request");
        testScope.setResponseToValidate(requestHelper.executeRequestToValidate(requestType));
        testScope.setResponse(testScope.getResponseToValidate().extract().response());
        logger.trace(" \nResponse time: " + responseHelper.getResponseTime()+" milliseconds");
    }

    @When("^I send \"([^\"]*)\" request and wait for the Response: \"([^\"]*)\"$")
    public void sendRequestAndWait(String requestType, Integer statusCode) {
        logger.info("Step: I send " + requestType + " request and wait for the Response: "+statusCode);
        try {
            await()
                    .atMost(waitForResponseSeconds, SECONDS)
                    .with()
                    .pollInterval(Duration.ONE_HUNDRED_MILLISECONDS)
                    .until(() -> executeRequestValidateStatusCode(requestType, statusCode).equals(true));
        } catch (ConditionTimeoutException e) {
            Assert.assertTrue("Timeout exception ", false);
        }

    }


    private Boolean executeRequestValidateStatusCode(String requestType, Integer statusCode) {

        testScope.setResponseToValidate(requestHelper.executeRequestToValidate(requestType));
        testScope.setResponse(testScope.getResponseToValidate().extract().response());
        logger.debug("Response status code is " + testScope.getResponse().getStatusCode());
        logger.trace(" \nResponse time: " +responseHelper.getResponseTime()+" milliseconds");
        return testScope.getResponse().getStatusCode() == statusCode;
    }


//--------------------------------------------------------------------------------------------------------
    //Response
//--------------------------------------------------------------------------------------------------------

    //Verify Status Code of Response

    @Then("^Response has Status code: \"([^\"]*)\"$")
    public void responseStatusCode(Integer statusCode) {

        logger.info("Step: Response has Status code " + statusCode.toString());
        responseHelper.validateResponseStatusCode(statusCode);
    }

    //Verify Content-Type as expected

    @And("^Response has Content Type \"([^\"]*)\"$")
    public void responseTypeIs(String contType) {

        logger.info("Step: Response has Content Type " + contType);
        responseHelper.validateResponseContentType(contType);
    }


    //Check if Json has Node
    @And("^Response body JSON has node: \"([^\"]*)\"$")
    public void responseBodyHasNode(String expectedNode) {

        logger.info("Step: Response body JSON has node " + expectedNode);
        responseHelper.validateResponseBodyNodeExist(expectedNode);
    }

    //Check if Json has Node
    @And("^Response body XML has node: \"([^\"]*)\"$")
    public void responseBodyHasXmlNode(String expectedNode) {

        logger.info("Step: Response body XML has node " + expectedNode);
        responseHelper.validateResponseXmlBodyExists(expectedNode);
    }


    //JSON Schema Validation using Jakson

    @And("^Response body JSON matches schema \"([^\"]*)\"$")
    public void responseStructureOfAJSONMatches(String jsonShemaFile)  {

        String newJsonShemaFile = requestHelper.resolveAllPlaceholdersURL(jsonShemaFile);
        logger.info("Step: Response body JSON matches schema " +  newJsonShemaFile);
        responseHelper.validateResponseMatchesSchema(newJsonShemaFile);
    }


    @And("^Response body JSON matches swagger file \"([^\"]*)\" schema \"([^\"]*)\"$")
    public void responseStructureOfAJsonMatchesSwagger(String swaggerFile, String schemaPointer) {


        String newSwaggerFile = requestHelper.resolveAllPlaceholdersURL(swaggerFile);
        logger.info("Step: Response body JSON matches swagger file "+ newSwaggerFile+ " schema " + schemaPointer);
        responseHelper.validateResponseMatchesSchemaSwagger(newSwaggerFile, schemaPointer);
    }

    @And("^Response body JSON matches JSON file \"([^\"]*)\"$")
    public void responseBodyJsonMatchesJSONFile(String jsonFile) {

        String newJsonFile = requestHelper.resolveAllPlaceholdersURL(jsonFile);
        logger.info("Step: Response body JSON matches JSON file  " + newJsonFile);
        responseHelper.validateResponseMatchesJson(newJsonFile);
    }


    @And("^Response body JSON node equals to val:$")
    public void responseBodyNodeIdEqualToVal(DataTable nodesTable) {

        logger.info("Step: Response body JSON node equals to val:");

        Map<String, String> nodes = nodesTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newNodes = requestHelper.resolveAllPlaceholders(nodes);

        Set<String> keys = newNodes.keySet();
        for (String n : keys) {
            String expectedNodeVal = newNodes.get(n);
            responseHelper.validateResponseBodyStringNodeEqualToVal(n, expectedNodeVal);
        }
    }


    @And("^Response body JSON node contains String:$")
    public void responseBodyNodeContainsString(DataTable nodesTable) {

        logger.info("Step: Response body JSON node contains String:");
        Map<String, String> nodes = nodesTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newNodes = requestHelper.resolveAllPlaceholders(nodes);

        Set<String> keys = newNodes.keySet();
        for (String n : keys) {
            String expectedNodeVal = newNodes.get(n);
            responseHelper.validateResponseBodyNodeContainsString(n, expectedNodeVal);
        }
    }

    @And("^Response body JSON node \"([^\"]*)\" is an array of size =(\\d+)$")
    public void responseBodyJSONNodeIsAnArrayOfSize(String node, Integer expectedSize) {
        logger.info("Step: Response body JSON node " + node + " is an array of size =" + expectedSize);
        responseHelper.validateResponseBodyJSONNodeIsAnArrayOfSize(node, expectedSize);
    }


    @And("^Response body XML node equals to val:$")
    public void responseBodyXMLNodeEqualToVal(DataTable fieldsTable) {
        logger.info("Step: Response body XML node equals to val:");
        Map<String, String> nodes = fieldsTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newNodes = requestHelper.resolveAllPlaceholders(nodes);

        Set<String> keys = newNodes.keySet();
        for (String n : keys) {
            String expectedVal = newNodes.get(n);
            responseHelper.validateResponseXmlNodeEqualsToVal(n, expectedVal);
        }
    }


    @And("^Response body XML node contains val:$")
    public void responseBodyXMLNodeContainsVal(DataTable fieldsTable) {
        logger.info("Step: Response body XML node contains val:");
        Map<String, String> nodes = fieldsTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newNodes = requestHelper.resolveAllPlaceholders(nodes);

        Set<String> keys = newNodes.keySet();
        for (String n : keys) {
            String expectedVal = newNodes.get(n);
            responseHelper.validateResponseXmlNodeContainsVal(n, expectedVal);
        }
    }


    @And("^Response has response time < (\\d+) milliseconds$")
    public void responseHasResponseTime(long time) {
        logger.info("Step: Response has response time less than "+time+" milliseconds");
        responseHelper.validateResponseTime(time);
    }


    @And("^Response has ALL the headers from data Table:$")
    //Response should have all this headers and might have some addition headers
    //Fail if some header from list not found

    public void responseHasNextHeaders(List<String> expectedHeadersList) {

        logger.info("Step: Response has ALL the headers from data Table:");
        responseHelper.validateResponseHasNextHeaders(expectedHeadersList);
    }

    @And("^Response has ALL the headers from csv file: \"([^\"]*)\"$")
    //Response should have all this headers and might have some addition headers
    //Fail if some header from csv not found

    public void responseHasNextHeadersFromCsvFile(String file) {

        //Check if there are placeholder {{ }} for property and replace it
        String newFile = requestHelper.resolveAllPlaceholdersURL(file);

        logger.info("Step: Response has ALL the headers from csv file " + newFile);
        //Get expected headers
        List<String> expectedHeadersList = readFile.readFirstColumnCSVtoList(newFile);

        responseHelper.validateResponseHasNextHeaders(expectedHeadersList);
    }


    @And("^Response has ALL the headers from data Table and ONLY those:$")
    //Response should have all this headers and should NOT have any addition headers
    //Fail if some expected header not found
    //Fail if some unexpected header found

    public void responseHasALLHeadersONLY(List<String> expectedHeadersList) {

        logger.info("Step: Response has ALL the headers from data Table and ONLY those");
        responseHelper.validateResponseHasALLHeadersONLY(expectedHeadersList);

    }

    @And("^Response has ALL the headers from csv file \"([^\"]*)\" and ONLY those$")
    //Response should have all this headers and should NOT have any addition headers
    //Fail if some expected header not found
    //Fail if some unexpected header found

    public void responseHasALLTheHeadersFromCsvFileAndONLYThat(String file) {

        //Check if there are placeholder {{ }} for property and replace it
        String newFile = requestHelper.resolveAllPlaceholdersURL(file);
        logger.info("Step: Response has ALL the headers from csv file " + newFile + " and ONLY those");

        //Get expected headers from csv file
        List<String> expectedHeadersList = readFile.readFirstColumnCSVtoList(newFile);

        responseHelper.validateResponseHasALLHeadersONLY(expectedHeadersList);
    }


    @And("^Response has header with the name: \"([^\"]*)\"$")
    //Fail id header not found

    public void responseHasNextHeaderNameString(String headerName) {

        logger.info("Step: Response has header with the name " + headerName);
        responseHelper.validateResponseHasNextHeaderName(headerName);
    }


    @And("^Response header \"([^\"]*)\" has next value: \"([^\"]*)\"$")
    //Fail if header not found
    //Fail if header value unexpected

    public void responseHeaderHasNextHeaderValue(String headerName, String expectedHeaderValue) {

        logger.info("Step: Response header " + headerName + " has next value: " + expectedHeaderValue);

        //Resolve placeholders
        String newExpectedHeaderValue = requestHelper.resolveAllPlaceholders(expectedHeaderValue);

        responseHelper.validateResponseHeaderHasNextStringValue(headerName, newExpectedHeaderValue);
    }

    @And("^Response has cookie with the name: \"([^\"]*)\"$")
    //Fail id cookie not found

    public void responseHasNextCookie(String cookieName) {

        logger.info("Step: Response has cookie with the name " + cookieName);
        responseHelper.validateResponseHasNextCookie(cookieName);
    }


    @And("^Response cookie \"([^\"]*)\" has next value: \"([^\"]*)\"$")
    //Fail if cookie not found
    //Fail if cookie value unexpected

    public void responseCookieHasNextCookieValue(String cookieName, String expectedCookieValue) {

        //Resolve placeholders
        String newExpectedCookieValue = requestHelper.resolveAllPlaceholders(expectedCookieValue);
        logger.info("Step: Response cookie " + cookieName + " has next value: " + newExpectedCookieValue);

        responseHelper.validateResponseCookieHasNextStringValue(cookieName, newExpectedCookieValue);
    }


    //use this step if you have to chain few API calls in one scenario
    @And("^I clear my previous API call$")
    public void clearMyPreviousAPICall() {

        logger.info("Step: I clear my previous API call");
        testScope.resetPreviousAPICall();
    }

    //use this step if you have to chain few API calls in one scenario
    @And("^I clear my previous API call and switch Proxy$")
    public void clearMyPreviousAPICallTurnProxyOff() {

        logger.info("Step: I clear my previous API call and switch Proxy");
        testScope.resetPreviousAPICallAndSwitchProxy();
    }


    @And("^I store JSON Response body node \"([^\"]*)\" as \"([^\"]*)\" in the scenario scope$")
    public void storeBodyNodeInScenarioScope(String node, String key) {

        logger.info("Step: I store Response body node " + node + " as " + key + " in the scenario scope");
        responseHelper.storeValueFromResponseBody(node, key);
    }


    @And("^I store XML Response body node \"([^\"]*)\" as \"([^\"]*)\" in the scenario scope$")
    public void storeXmlBodyNodeInScenarioScope(String node, String key) {

        logger.info("Step: I store XML Response body node " + node + " as " + key + " in the scenario scope");
        responseHelper.storeValueFromResponseXmlBody(node, key);
    }


    @And("^I store Response header \"([^\"]*)\" as \"([^\"]*)\" in the scenario scope$")
    public void storeHeaderInScenarioScope(String header, String key) {

        logger.info("Step: I store Response header " + header + " as " + key + " in the scenario scope");
        responseHelper.storeValueFromResponseHeader(header, key);
    }


    @And("^I store Cookie \"([^\"]*)\" as \"([^\"]*)\" in the scenario scope$")
    public void storeCookieInScenarioScope(String cookie, String key) {

        logger.info("Step: I store Cookie " + cookie + " as " + key + " in the scenario scope");
        responseHelper.storeValueFromResponseCookie(cookie, key);
    }

    @And("^I establish connection with Database \"([^\"]*)\"$")
    public void iEstablishConnectionToDataBase(String db) {
        logger.info("Step: I establish connection with Database " + db);
        if (db.toLowerCase().contains("oracle") || db.toLowerCase().contains("aurora"))
            dataBaseHelper.establishConnection();
        else {
            logger.error("Watchmen does not have configurations for the DB: " + db);
            Assert.assertTrue("Watchmen does not have configurations for the DB: " + db + ".Please connect to Watchmen team", false);
        }
    }


    @And("^I query for String \"([^\"]*)\" and store result as \"([^\"]*)\" in the scenario scope$")
    public void queryForStringSave(String sql, String key) {

        logger.info("Step: I query for String: "+sql);
        String finalSql;

        logger.debug("check and replace all {{property_name}} variables with corresponding properties from properties file");
        String newSql = requestHelper.resolveAllPlaceholdersURL(sql);

        if (newSql.toLowerCase().endsWith(".sql")) {
            finalSql = readFile.readFileAsString(newSql);
        } else finalSql = newSql;


        try {
            String sqlResponse = dataBaseHelper.queryForString(finalSql);
            testScope.saveInContainer(key, sqlResponse);
        } catch (RuntimeException e) {
            Assert.assertTrue("Was NOT able to query for String " + e, (false));
        }
    }

    @And("^I query for Integer \"([^\"]*)\" and store result as \"([^\"]*)\" in the scenario scope$")
    public void queryForIntegerSave(String sql, String key) {

        logger.info("Step: I query for Integer: "+sql);
        String finalSql;

        logger.debug("check and replace all {{property_name}} variables with corresponding properties from properties file");
        String newSql = requestHelper.resolveAllPlaceholdersURL(sql);

        if (newSql.toLowerCase().endsWith(".sql")) {
            finalSql = readFile.readFileAsString(newSql);
        } else finalSql = newSql;

        try {
            Integer sqlResponse = dataBaseHelper.queryForInteger(finalSql);
            testScope.saveInContainer(key, sqlResponse.toString());
        } catch (RuntimeException e) {
            Assert.assertTrue("Was NOT able to query for Integer " + e, (false));
        }
    }

    @And("^I query for String \"([^\"]*)\" with parameters as Data Table and store result as \"([^\"]*)\" in the scenario scope:$")
    public void queryForStringWithParametersSave(String sql, String key, DataTable paramTable) {

        logger.info("Step: I query for String: " + sql+" with parameters as Data Table");
        Map<String, String> namedParam = paramTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newNamedParam = requestHelper.resolveAllPlaceholders(namedParam);

        String finalSql;

        logger.debug("check and replace all {{property_name}} variables with corresponding properties from properties file");
        String newSql = requestHelper.resolveAllPlaceholdersURL(sql);

        if (newSql.toLowerCase().endsWith(".sql")) {
            finalSql = readFile.readFileAsString(newSql);
        } else finalSql = newSql;

        try {
            String sqlResponse = dataBaseHelper.queryForStringWithParam(finalSql, newNamedParam);
            testScope.saveInContainer(key, sqlResponse);
        } catch (RuntimeException e) {
            Assert.assertTrue("Was NOT able to query for String " + e, (false));
        }

    }

    @And("^I query for Integer \"([^\"]*)\" with parameters as Data Table and store result as \"([^\"]*)\" in the scenario scope:$")
    public void queryForIntegerWithParametersSave(String sql, String key, DataTable paramTable) {

        logger.info("Step: I query for Integer: "+sql+" with parameters as Data Table");
        Map<String, String> namedParam = paramTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newNamedParam = requestHelper.resolveAllPlaceholders(namedParam);

        String finalSql;

        logger.debug("check and replace all {{property_name}} variables with corresponding properties from properties file");
        String newSql = requestHelper.resolveAllPlaceholdersURL(sql);

        if (newSql.toLowerCase().endsWith(".sql")) {
            finalSql = readFile.readFileAsString(newSql);
        } else finalSql = newSql;

        try {
            Integer sqlResponse = dataBaseHelper.queryForIntegerWithParam(finalSql, newNamedParam);
            testScope.saveInContainer(key, sqlResponse.toString());
        } catch (RuntimeException e) {
            Assert.assertTrue("Was NOT able to query for Integer " + e, (false));
        }

    }


    @And("^I wait for (\\d+) seconds$")
    public void waitForSeconds(int sec) {
        logger.info("Step: I wait for " + sec + "seconds");
        try {
            Thread.sleep(sec * 1000);
        } catch (Exception e) {
            logger.error("Error to wait for " + sec + "seconds. " + e.getMessage());

        }
    }

    @And("^I assert that numeric \"([^\"]*)\" is equal to \"([^\"]*)\"$")
    public void assertNumericEquals(String value1, String value2) {

        //Resolve placeholders
        String newValue1 = requestHelper.resolveAllPlaceholders(value1);
        String newValue2 = requestHelper.resolveAllPlaceholders(value2);

        logger.info("Step: I assert that numeric value1 "+newValue1+" is equal to value2 "+newValue2);

        Assert.assertTrue(newValue1 + " DOES NOT equal to " + newValue2, NumberUtils.parseNumber(newValue1, BigDecimal.class).compareTo(NumberUtils.parseNumber(newValue2, BigDecimal.class))==0);

    }

    @And("^I assert that numeric \"([^\"]*)\" is not equal to \"([^\"]*)\"$")
    public void assertNumericNotEquals(String value1, String value2) {


        //Resolve placeholders
        String newValue1 = requestHelper.resolveAllPlaceholders(value1);
        String newValue2 = requestHelper.resolveAllPlaceholders(value2);
        logger.info("Step: I assert that numeric value1 "+newValue1+" does not equal to value2 "+newValue2);

        Assert.assertTrue(newValue1 + " equals to " + newValue2, NumberUtils.parseNumber(newValue1, BigDecimal.class).compareTo(NumberUtils.parseNumber(newValue2, BigDecimal.class))!=0);

    }

    @And("^I assert that numeric \"([^\"]*)\" is bigger than \"([^\"]*)\"$")
    public void assertNumericBigger(String value1, String value2) {

        //Resolve placeholders
        String newValue1 = requestHelper.resolveAllPlaceholders(value1);
        String newValue2 = requestHelper.resolveAllPlaceholders(value2);
        logger.info("Step: I assert that numeric value1 "+newValue1+" is bigger than value2 "+newValue2);

        Assert.assertTrue(newValue1 + " is NOT bigger then " + newValue2, NumberUtils.parseNumber(newValue1, BigDecimal.class).compareTo(NumberUtils.parseNumber(newValue2, BigDecimal.class))==1);

    }

    @And("^I assert that string \"([^\"]*)\" is equal to \"([^\"]*)\"$")
    public void assertStringEquals(String value1, String value2) {

        //Resolve placeholders
        String newValue1 = requestHelper.resolveAllPlaceholders(value1);
        String newValue2 = requestHelper.resolveAllPlaceholders(value2);
        logger.info("Step: I assert that string1 "+newValue1+" equals to string2 "+newValue2);

        Assert.assertTrue(newValue1 + " DOES NOT equal to " + newValue2, newValue1.equalsIgnoreCase((newValue2)));

    }

    @And("^I assert that string \"([^\"]*)\" is not equal to \"([^\"]*)\"$")
    public void assertStringNotEquals(String value1, String value2) {

        //Resolve placeholders
        String newValue1 = requestHelper.resolveAllPlaceholders(value1);
        String newValue2 = requestHelper.resolveAllPlaceholders(value2);
        logger.info("Step: I assert that string1 "+newValue1+" is not equal to string2 "+newValue1);

        Assert.assertTrue(newValue1 + " equals to " + newValue2, !newValue1.equalsIgnoreCase((newValue2)));

    }

    @And("^I assert that string \"([^\"]*)\" contains \"([^\"]*)\"$")
    public void assertStringContains(String value1, String value2) {

        //Resolve placeholders
        String newValue1 = requestHelper.resolveAllPlaceholders(value1).toLowerCase();
        String newValue2 = requestHelper.resolveAllPlaceholders(value2).toLowerCase();
        logger.info("Step: I assert that string1 "+newValue1+" contains string2 "+newValue2);

        Assert.assertTrue(newValue1 + " DOES NOT contain " + newValue2, newValue1.contains(newValue2));

    }

    @And("^I assert that string \"([^\"]*)\" does not contain \"([^\"]*)\"$")
    public void assertStringNotContains(String value1, String value2) {

        //Resolve placeholders
        String newValue1 = requestHelper.resolveAllPlaceholders(value1).toLowerCase();
        String newValue2 = requestHelper.resolveAllPlaceholders(value2).toLowerCase();
        logger.info("Step: I assert that string1 "+newValue1+" does not contain string2 "+newValue2);

        Assert.assertTrue(newValue1 + " DOES NOT contain " + newValue2, !newValue1.contains(newValue2));

    }

    //add cookies to Request
    @And("^I provide test data as data Table:$")
    public void provideTestDataAs(DataTable cookiesTable) {

        logger.info("Step: I provide test data as data Table");

        //Read Cucumber Data Table
        Map<String, String> data = cookiesTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newData = requestHelper.resolveAllPlaceholders(data);
        for (Map.Entry<String, String> entry : newData.entrySet())
            requestHelper.storeInScenarioScopeMyVal(entry.getKey(), entry.getValue());

    }
}

















