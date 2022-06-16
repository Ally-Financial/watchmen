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

package com.ally.d3.watchmen.utilities;

import com.ally.d3.watchmen.steps.TestScope;
import com.ally.d3.watchmen.utilities.dataDriven.JsonHelper;
import com.ally.d3.watchmen.utilities.dataDriven.ReadFile;
import com.ally.d3.watchmen.utilities.dataDriven.XMLHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import io.restassured.module.jsv.JsonSchemaValidationException;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ResponseHelper {

    private static final Logger logger = LoggerFactory.getLogger(ResponseHelper.class);

    @Autowired
    RestAssuredHelper restAssuredHelper;

    //Inject dependency - to share state between steps in one scenario
    @Autowired
    TestScope testScope;

    @Autowired
    JsonHelper jsonHelper;

    @Autowired
    XMLHelper xmlHelper;

    @Autowired
    ReadFile readFile;


    public void validateResponseStatusCode(Integer expectedStatusCode) {

        logger.debug("Assert Response status code is: " + expectedStatusCode);
        Integer actualStatusCode = restAssuredHelper.getStatusCodeFromResponse(testScope.getResponse());
        Assert.assertTrue("Expected status-code = " + expectedStatusCode + " doesn't match actual status-code = " + actualStatusCode + ". Actual status-line is: " + restAssuredHelper.getStatusLineFromResponse(testScope.getResponse()), actualStatusCode.equals(expectedStatusCode));

    }

    public void validateResponseContentType(String expectedContentType) {

        logger.debug("Assert Response Content Type is: " + expectedContentType);
        String actualContentType = restAssuredHelper.getContentTypeFromResponse(testScope.getResponse());
        Assert.assertTrue("Expected content-type = " + expectedContentType + " doesn't match actual content-type = " + actualContentType, actualContentType.equals(expectedContentType));

    }

    public void validateResponseBodyNodeExist(String expectedNode) {

        logger.debug("Assert Response JSON Body Node: " + expectedNode + " exists");
        Assert.assertTrue("Expected Response node: " + expectedNode + " was not found ", isResponseNodeExist(expectedNode));

    }


    public void validateResponseXmlBodyExists(String expectedNode) {

        logger.debug("Assert Response Xml Body Node: " + expectedNode + " exists");
        Assert.assertTrue("Expected Xml Response node: " + expectedNode + " was not found ", isResponseXmlNodeExist(expectedNode));

    }

    public Boolean isResponseCookieExist(String cookie) {

        logger.debug("Check if Cookie: " + cookie + " exists");
        if (restAssuredHelper.getCookiesAsMap(testScope.getResponse()).get(cookie) != null) {
            logger.debug("Cookie exists");
            return true;
        } else {
            logger.debug("Cookie does not exist");
            return false;
        }


    }

    public void validateResponseCookieHasNextStringValue(String cookieName, String expectedCookieValue) {

        logger.debug("Assert Response Cookie: " + cookieName + " Has next String value: " + expectedCookieValue);

        //first check if cookie is present
        Assert.assertTrue("Response cookie " + cookieName + " was not found ", isResponseCookieExist(cookieName));

        //if cookie is present - get its value
        String actualCookieValue = restAssuredHelper.getCookieValFromResponse(testScope.getResponse(), cookieName);
        logger.debug("Assert expected cookie " + cookieName + " value match expectedCookeValue ignoring case");
        Assert.assertTrue("cookie: " + cookieName + " does not match expected value. Expected value is: " + expectedCookieValue + " Actual value is: " + actualCookieValue, actualCookieValue.equalsIgnoreCase(expectedCookieValue));

    }

    public void validateResponseHasNextCookie(String cookieName) {

        logger.debug("Assert Response has cookie: " + cookieName);
        Assert.assertTrue("Expected cookie " + cookieName + " was not found", isResponseCookieExist(cookieName));

    }


    public Boolean isResponseNodeExist(String expectedNode) {

        logger.debug("Check if Response node: " + expectedNode + " exists");
        JsonNode responseBodyJson = null;

        try {
            responseBodyJson = jsonHelper.readJsonAsTree(testScope.getResponse().getBody().asString());
        }
        catch (RuntimeException e) {
            logger.error("Not able to parse Response as a JSON "+e);
        Assert.assertTrue("Not able to parse Response as a JSON",false);
         }

        //if node not found - getJSONnodeValue returns "NodeNotFound"
        String actualNodeValue = jsonHelper.getJSONnodeValue(responseBodyJson, expectedNode);

        return !actualNodeValue.equals("NodeNotFound");


    }


    public Boolean isResponseXmlNodeExist(String expectedNode) {

        logger.debug("Check if XML Response node: " + expectedNode + " exists");

        Document responseBodyXML = null;

        try {
            responseBodyXML = xmlHelper.createDocumentFromString(testScope.getResponse().getBody().asString());
            logger.debug("Response is recognized as an XML and parsed to org.w3c.dom.Document");
        } catch (RuntimeException e) {
            logger.error("Not able to parse Response as an Xml Document "+e);
            Assert.assertTrue("Not able to parse Response as an Xml Document",false);
        }

        return xmlHelper.isXmlNodeExist(responseBodyXML, expectedNode);

    }



    public void validateResponseMatchesSchema(String jsonSchemaFile)  {

        logger.debug("Assert Response Matches Schema: " + jsonSchemaFile);
        try {
            testScope.getResponseToValidate().assertThat().body(matchesJsonSchemaInClasspath(jsonSchemaFile));

    } catch (Exception exc) {
            if (exc instanceof IllegalArgumentException) {

                logger.error("JSON schema file does not exist " + exc);
                throw new RuntimeException("Could NOT find JSON schema file: " + jsonSchemaFile);

            } else if (exc instanceof JsonSchemaValidationException) {
                logger.debug("JSON schema validation failed: " + exc.getLocalizedMessage());
                Assert.assertTrue("JSON schema validation failed. Please read report: \n " + exc,false);
            }
        }

    }




    public void validateResponseMatchesSchemaSwagger(String swaggerFile, String definitionPointer)  {

        logger.debug("Assert Response Matches Swagger: " + swaggerFile+ " definition "+definitionPointer);
        String jsonResponse = testScope.getResponse().getBody().asString();

        ProcessingReport processingReport = jsonHelper.getReportJsonMatchSchemaSwagger(jsonResponse,swaggerFile,definitionPointer);

        //If validation not successful - fail step
        Assert.assertTrue("Response body does not match swagger: " + swaggerFile + " definition "+definitionPointer+ " Please see Processing Report: " + processingReport.toString().replaceAll("\n", " | "), processingReport.isSuccess());

    }

    public void validateResponseMatchesJson(String jsonFile)  {

        logger.debug("Assert Response Matches Json: " + jsonFile);
        String jsonResponse = testScope.getResponse().getBody().asString();

        String json2 =readFile.readFileAsString(jsonFile);
        Assert.assertTrue("Response body does not match json file: " + jsonFile+".The difference is: "+ jsonHelper.printJsonDifference(jsonResponse,json2), jsonHelper.isJsonEqualsJson(jsonResponse,json2));

    }


    public void validateResponseBodyStringNodeEqualToVal(String node, String expectedNodeVal) {

        logger.debug("Assert Response Body Node: " + node + " Equals to String: " + expectedNodeVal);
        //first check if Node is presents

        logger.debug("Assert requested node: " + node + " was found");
        Assert.assertTrue("Response node: " + node + " was not found ", isResponseNodeExist(node));

        //If node presents

        JsonNode responseBodyJson = jsonHelper.readJsonAsTree(testScope.getResponse().getBody().asString());
        String actualNodeValue = jsonHelper.getJSONnodeValue(responseBodyJson, node);
        logger.debug("Assert node: " + node + " value matches expected value ignoring case");
        Assert.assertTrue("Response body Node: " + node + " does not match expected value. Expected value is: " + expectedNodeVal + "; Actual value is: " + actualNodeValue, actualNodeValue.equalsIgnoreCase(expectedNodeVal));


    }

    public void validateResponseBodyNodeContainsString(String node, String expectedNodeVal) {

        logger.debug("Assert Response Body node: " + node + " Contains String: " + expectedNodeVal);
        String expectedNodeValUpperCase = expectedNodeVal.toUpperCase();

        //first check if Node presents

        logger.debug("Assert requested node: " + node + " was found");
        Assert.assertTrue("Response node: " + node + " was not found ", isResponseNodeExist( node));

        //If node presents

        JsonNode responseBodyJson = jsonHelper.readJsonAsTree(testScope.getResponse().getBody().asString());
        String actualNodeValue = jsonHelper.getJSONnodeValue(responseBodyJson, node);
        ;
        logger.debug("Assert node: " + node + " contains string ignoring case");
        Assert.assertTrue("Response body Node: " + node + " does not contain String: " + expectedNodeVal + "; Actual node value is: " + actualNodeValue, actualNodeValue.toUpperCase().contains(expectedNodeValUpperCase));


    }

    public void storeInScenarioScopeJSONResponseBody (String key) {

        JsonNode responseBodyJson = null;

        try {
            responseBodyJson = jsonHelper.readJsonAsTree(testScope.getResponse().getBody().asString());
            testScope.saveInJsonContainer(key,responseBodyJson);
        }
        catch (RuntimeException e) {
            logger.error("Not able to parse Response as a JSON "+e);
            throw new RuntimeException("Not able to parse Response as a JSON "+e);

        }

    }


    public void validateResponseXmlNodeEqualsToVal(String path, String expectedVal) {

        logger.debug("Assert Response XML node: " + path + " Equals To Val ignoring case: " + expectedVal);
        Assert.assertTrue("Response XML node = " + path + " does not match expected", getValueFromResponseXmlBody( path).equalsIgnoreCase(expectedVal));

    }

    public void validateResponseXmlNodeContainsVal(String path, String expectedVal) {

        logger.debug("Assert Response XML node: " + path + " Contains Val ignoring case: " + expectedVal);
        String expectedValUC = expectedVal.toUpperCase();
        Assert.assertTrue("Response XML node = " + path + " does not contain expected", getValueFromResponseXmlBody(path).toUpperCase().contains(expectedValUC));

    }

    public void validateResponseTime(long expectedTime) {

        logger.debug("Assert Response time is less then: " + expectedTime);
        long actualTime = getResponseTime();
        logger.debug("Response time: " + actualTime);
        Assert.assertTrue("Expected response time = " + expectedTime + " not less then actual time =  " + actualTime, actualTime < expectedTime);

    }

    public long getResponseTime() {

        logger.debug("Get Response time");
        return restAssuredHelper.getResponseTime(testScope.getResponse());

    }

    public Boolean isResponseHeaderExist(String expectedHeader) {
        logger.debug("Check if the Response Header: " + expectedHeader + " Exists");
        if (restAssuredHelper.getHeadersNamesAsListFromResponse(testScope.getResponse()).contains(expectedHeader)) {
            logger.debug("Header exists");
            return true;
        } else {
            logger.debug("Header does not exist");
            return false;
        }


    }

    public void validateResponseHasNextHeaders(List<String> expectedHeadersList) {

        logger.debug("Assert Response has all the expected headers from the list");
        Boolean isAllExpectedHeadersFound = true;
        List<String> notFoundList = new ArrayList<String>();

        //Get actual response headers
        List<String> foundList = restAssuredHelper.getHeadersNamesAsListFromResponse(testScope.getResponse());


        //Check if all expected headers are presented among actual headers
        //if some expected headers not found - save them to  notFoundList for Error message
        for (int i = 0; i < expectedHeadersList.size(); i++) {
            if (!foundList.contains(expectedHeadersList.get(i))) {
                isAllExpectedHeadersFound = false;
                logger.debug("Header " + expectedHeadersList.get(i) + " Not found");
                notFoundList.add(expectedHeadersList.get(i));
            }
        }

        //Assert isAllExpectedHeadersFound = true;
        //If fail - provide error message
        Assert.assertTrue(notFoundList.size() + " Expected header(s) was not found: " + notFoundList.toString(), isAllExpectedHeadersFound.equals(true));

    }

    public void validateResponseHasOnlyAllowedHeaders(ResponseOptions<Response> response, List<String> allowedHeadersList) {


        //Validate if only allowed headers found
        //If found not allowed headers - save them to the notAllowedFoundList to print on Error message


        logger.debug("Assert that Response's headers are from the allowed list");
        Boolean isOnlyAllowedHeadersFound = true;
        List<String> notAllowedFoundList = new ArrayList<String>();

        //Actual response headers
        List<String> foundList = restAssuredHelper.getHeadersNamesAsListFromResponse(response);


        for (int i = 0; i < foundList.size(); i++) {
            if (!allowedHeadersList.contains(foundList.get(i))) {
                isOnlyAllowedHeadersFound = false;
                notAllowedFoundList.add(foundList.get(i));
                logger.debug("Not allowed header found: " + foundList.get(i));
            }
        }

        // Assert isOnlyAllowedHeadersFound.equals(true), if not - provide Error message
        Assert.assertTrue(notAllowedFoundList.size() + " NOT allowed header(s) was  found: " + notAllowedFoundList.toString(), isOnlyAllowedHeadersFound.equals(true));
    }


    public void validateResponseHasALLHeadersONLY(List<String> expectedHeadersList) {

        logger.debug("Assert if Response has only expected headers and only that");
        Boolean isAllExpectedHeadersFound = true;
        Boolean isOnlyExpectedHeadersFound = true;
        List<String> notFoundList = new ArrayList<String>();
        List<String> notExpectedList = new ArrayList<String>();

        //Get actual response headers
        List<String> foundList = restAssuredHelper.getHeadersNamesAsListFromResponse(testScope.getResponse());


        //Check if all expected headers were found
        for (int i = 0; i < expectedHeadersList.size(); i++) {
            if (!foundList.contains(expectedHeadersList.get(i))) {
                isAllExpectedHeadersFound = false;
                notFoundList.add(expectedHeadersList.get(i));
                logger.debug("Expected header: " + expectedHeadersList.get(i) + " Not found");
            }

        }

        //Check if only expected headers were found and only that
        if (isAllExpectedHeadersFound & expectedHeadersList.size() == foundList.size()) {
        } else isOnlyExpectedHeadersFound = false;
        for (int i = 0; i < foundList.size(); i++) {
            if (!expectedHeadersList.contains(foundList.get(i))) {
                notExpectedList.add(foundList.get(i));
                logger.debug("Not Expected header found: " + foundList.get(i));
            }
        }


        //assert that isAllExpectedHeadersFound = true  AND isOnlyExpectedHeadersFound = true;
        //If fail - provide error message
        Assert.assertTrue(notFoundList.size() + " Expected header(s) was not found: " + notFoundList.toString() + " and " + notExpectedList.size() + " NOT expected headers found : " + notExpectedList.toString(), isOnlyExpectedHeadersFound.equals(true) & isAllExpectedHeadersFound.equals(true));


    }


    public void validateResponseHasNextHeaderName(String headerName) {

        logger.debug("Assert Response has header: " + headerName);
        Assert.assertTrue("Expected Response header: " + headerName + " was not found", restAssuredHelper.isHeaderPresentedOnResponse(testScope.getResponse(), headerName));

    }

    public void validateResponseHeaderHasNextStringValue(String headerName, String expectedHeaderValue) {

        logger.debug("Assert header: " + headerName + " has expected value: " + expectedHeaderValue);

        //first check if header exists
        logger.debug("Assert requested header: " + headerName + " was found");
        Assert.assertTrue("Response header was not found ", isResponseHeaderExist(headerName));

        //if header was found - get its value
        String actualHeaderValue = restAssuredHelper.getHeaderValueFromResponse(testScope.getResponse(), headerName);
        logger.debug("Assert requested header: " + headerName + " matches expected value ignoring case");
        Assert.assertTrue("Response header " + headerName + " does not match expected value: " + expectedHeaderValue + " Actual value is: " + actualHeaderValue, actualHeaderValue.equalsIgnoreCase(expectedHeaderValue));


    }


    public void validateResponseBodyJSONNodeIsAnArrayOfSize(String node, Integer expectedSize) {

        logger.debug("Assert Response Body node: " + node + " Is an Array of Size = " + expectedSize);
        //first assert if Node exists

        logger.debug("Assert requested node " + node + " was found");
        Assert.assertTrue("Response node: " + node + " was not found ", isResponseNodeExist(node));

        //if node was found
        logger.debug("Assert expected size of an array node" + node + " equal " + expectedSize);
        JsonNode responseBodyJson = jsonHelper.readJsonAsTree(testScope.getResponse().getBody().asString());
        Integer size = jsonHelper.getJSONnodeArraySize(responseBodyJson, node);
        Assert.assertTrue("Expected array size = " + expectedSize + " of a response node: " + node + " doesn't match actual size = " + size, size == expectedSize);

    }

    public void storeValueFromResponseBody(String node, String key) {

        logger.debug("Store Value From Response Body node: " + node + " as: " + key);

        //First check if node found
        logger.debug("Assert requested node: " + node + " was found");
        Assert.assertTrue("Response does not have body node requested to save: " + node, (isResponseNodeExist(node)));

        //If node found
        logger.debug("Node was found, so retrieve its value");
        JsonNode responseBodyJson = jsonHelper.readJsonAsTree(testScope.getResponse().getBody().asString());
        String nodeValue = jsonHelper.getJSONnodeValue(responseBodyJson, node);
        logger.debug(key + " = " + nodeValue);
        testScope.saveInContainer(key, nodeValue);

    }

    public String getValueFromResponseXmlBody(String path) {


        String nodeValue = null;
        Document responseBodyXML = null;

        logger.debug("Parse Response as Xml Document");
        try {
            responseBodyXML = xmlHelper.createDocumentFromString(testScope.getResponse().getBody().asString());
        } catch (RuntimeException e) {
            Assert.assertTrue("Not able to parse Response as an Xml Document", (false));
        }

        logger.debug("Get Value From the Xml Response Body node: " + path);
        try {
            nodeValue = xmlHelper.getXmlNodeValue(responseBodyXML, path).trim();
        } catch (RuntimeException e) {
            Assert.assertTrue("Was not able to proceed to the xml node: " + path, (false));
        }
        return nodeValue;

    }


    public void storeValueFromResponseXmlBody(String path, String key) {

        logger.debug("Store Value From the Xml Response Body node: " + path+", as: " + key);

        String nodeValue = getValueFromResponseXmlBody(path);
        logger.debug("XML path " + path + ", = " + nodeValue);
        testScope.saveInContainer(key, nodeValue);
    }

    //

    public void storeValueFromResponseHeader(String header, String key) {
        logger.debug("Store Value From the Response Header: " + header + " as: " + key);

        //First check if header found
        logger.debug("Assert requested header: " + header + " was found");
        Assert.assertTrue("Response does not have header requested to save: "+header, (restAssuredHelper.isHeaderPresentedOnResponse(testScope.getResponse(), header)));

        //If header found
            String headerValue = restAssuredHelper.getHeaderValueFromResponse(testScope.getResponse(),header);
            logger.debug("header " + header + " = " + headerValue);
            testScope.saveInContainer(key, headerValue);

    }

    public void storeValueFromResponseCookie(String cookie, String key) {
        logger.debug("Store Value From the Response Cookie: " + cookie + " as: " + key);

        //First check if cookie found
        logger.debug("Assert requested cookie: " + cookie + " was found");
        Assert.assertTrue("Response does not have cookie requested to save: "+cookie, (isResponseCookieExist(cookie)));

        //If cookie found
        String cookieValue;
        logger.debug("Store Value From the Response Cookie: " + cookie + " as: " + key);
        cookieValue = restAssuredHelper.getCookieValFromResponse(testScope.getResponse(), cookie);
        testScope.saveInContainer(key, cookieValue);

    }
}






