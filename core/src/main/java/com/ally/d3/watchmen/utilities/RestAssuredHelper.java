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
import com.ally.d3.watchmen.utilities.dataDriven.DefaultDataHelper;
import com.ally.d3.watchmen.utilities.dataDriven.JsonHelper;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.Header;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.exception.XmlPathException;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.response.ResponseOptions;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.ally.d3.watchmen.steps.TestScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static io.restassured.RestAssured.given;


public class RestAssuredHelper {

    private static final Logger logger = LoggerFactory.getLogger(RestAssuredHelper.class);


    //Inject dependency - to share state between steps in one scenario
    @Autowired
    private TestScope testScope;

    @Autowired
    private JsonHelper jsonHelper;

    @Autowired
    DefaultDataHelper defaultDataHelper;


    //inject properties from properties file
    @Value("${userName}")
    private String userName;

    @Value("${host}")
    private String host;

    @Value("${port}")
    private String port;


    //----------------------------------------------------------------------------------------------------------------
    //    Specify request
    //----------------------------------------------------------------------------------------------------------------


    public RequestSpecification startNewRequestSpecification(Boolean useProxy) {

        //Create New request specification
        //This is very first step to build request

        //initialize RequestSpecBuilder
        RequestSpecBuilder builder = new RequestSpecBuilder();

        //setProxy if useProxy=true
        if (useProxy){
            logger.debug("Set Proxy = "+host+":"+port);
            builder.setProxy(host, Integer.parseInt(port));}

        RequestSpecification requestSpec = builder.build();
        return requestSpec;

    }

    //Add  URL to request specification

    public RequestSpecification specifyRequestForURL(RequestSpecification requestSpec, String url) {

        logger.debug("Set URL = "+url);
        requestSpec.baseUri(url);
        return requestSpec;

    }

    //----------------------------------------------------------------------------------------------------------------
    //     Validate response
    //----------------------------------------------------------------------------------------------------------------

    //Get Response status code

    public Integer getStatusCodeFromResponse(ResponseOptions<Response> response) {

        logger.debug("Response Status Code = "+response.statusCode());
        return response.statusCode();
    }

    //Get response status line

    public String getStatusLineFromResponse(ResponseOptions<Response> response) {

        logger.debug("Response Status Line = "+response.getStatusLine());
        return response.getStatusLine();
    }

    //Get content type

    public String getContentTypeFromResponse(ResponseOptions<Response> response) {

        logger.debug("Response Content Type = "+response.getContentType());
        return response.getContentType();
    }

    //Get response body

    public ResponseBody getBodyFromResponse(ResponseOptions<Response> response) {
        logger.debug("Response Body = "+response.body().asString());
        return (response.body());
    }


    //Get response headers

    public List<Header> getHeadersAsListFromResponse(ResponseOptions<Response> response) {

        logger.debug("Headers As a List from the Response = "+response.getHeaders().asList());
        return (response.getHeaders().asList());
    }

    //Get response headers name

    public List<String> getHeadersNamesAsListFromResponse(ResponseOptions<Response> response) {

        List<Header> headers = response.getHeaders().asList();
        List<String> headersNames = new ArrayList<String>();
        //for each loop
        for (Header header:headers) {
            headersNames.add(header.getName());
        }

        logger.debug("Response Headers Names As a List = "+headersNames);
        return headersNames;


    }

    //Get cookies as Map

    public Map <String,String> getCookiesAsMap(ResponseOptions<Response> response) {

        logger.debug("Cookies As Map From Response = "+response.cookies());
        return (response.cookies());
    }

    //Get cookie value

    public String getCookieValFromResponse(ResponseOptions<Response> response, String cookie) {

        logger.debug("Response Cookie: "+cookie+" value = "+response.cookie(cookie));
        return (response.cookie(cookie));
    }

    //Get detailed cookie value

    public String getDetailedCookieValFromResponse(ResponseOptions<Response> response, String cookie) {

        logger.debug("Response Cookie: "+cookie+" detailed value = "+response.getDetailedCookie(cookie));
        return (response.cookie(cookie));
    }


    //Get response time

    public long getResponseTime(ResponseOptions<Response> response) {
        logger.debug("Get Response time ");
        return (response.getTime());
    }

    //Validate if header (by name) is present

    public Boolean isHeaderPresentedOnResponse(ResponseOptions<Response> response, String name) {

        logger.debug("Is Header: "+name+" presented on the Response = "+response.headers().hasHeaderWithName(name));
        return (response.headers().hasHeaderWithName(name));
    }

    //Get response headers value

    public String getHeaderValueFromResponse(ResponseOptions<Response> response, String headerName) {

        logger.debug("Response Header: "+headerName+ " Value = "+response.headers().getValue(headerName));
        return (response.headers().getValue(headerName));

    }


    //Is JSON node value matches expectation

    public Boolean isStringNodeValueFromResponseMatch(ResponseOptions<Response> response, String node, String expectedNodeVal) {

        JsonNode responseBodyJson = jsonHelper.readJsonAsTree(response.getBody().asString());

        String nodeValFromResponse="";

        try {
            nodeValFromResponse= jsonHelper.getJSONnodeValue(responseBodyJson,node);
        }
        catch (Throwable e) {
            logger.error("Not able to get Node Value from response "+e);
        }

        return (nodeValFromResponse.equalsIgnoreCase(expectedNodeVal));


    }


    //Get response JSON body node value (as List)

    public List getNodeValAsListFromResponse(ResponseOptions<Response> response, String node) {
        JsonNode responseBodyJson = jsonHelper.readJsonAsTree(response.getBody().asString());
        List valReturn = new ArrayList();
        try {
            valReturn= jsonHelper.getJSONnodeAsList(responseBodyJson,node);
        }
        catch (Throwable e) {
            logger.error("not able to get NodeValAsListFromResponse "+e);
        }
        return valReturn;
    }



    //Get response XML body field value (as String)

    //RestAssured
    public String getXMLPathValAsStringFromResponse(ResponseOptions<Response> response, String path) {
        try {
            logger.debug("Get xml path " + path + " Value As String From XML Response");

            String stringResponse = response.body().asString();
            XmlPath xmlPath = new XmlPath(stringResponse);
            return xmlPath.get(path);

        } catch (XmlPathException e) {
            logger.debug("not able to get XML path value as a String "+e);
            return "WasNotFound";
        }
    }


    public String getNodeValAsStringFromResponse(ResponseOptions<Response> response, String node) {

        JsonNode responseBodyJson = jsonHelper.readJsonAsTree(response.getBody().asString());

        String nodeVal="";

        try {
            nodeVal=jsonHelper.getJSONnodeValue(responseBodyJson,node);
        }
        catch (Throwable e)
        {
            logger.error("not able to get node value "+e);

        }
        return nodeVal;
        }


}


