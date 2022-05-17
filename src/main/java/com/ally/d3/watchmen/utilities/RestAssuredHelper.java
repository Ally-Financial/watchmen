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
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import cucumber.api.Scenario;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.Header;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.exception.XmlPathException;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.response.ResponseOptions;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.output.WriterOutputStream;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.ally.d3.watchmen.steps.TestScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        //Define Ally Proxy settings
        //Add header with Tester name for debugging
        //Invoke Request-Response logging



        //Ally Proxy reactivate tunnel settings
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");


        //initialize RequestSpecBuilder
        RequestSpecBuilder builder = new RequestSpecBuilder();

        //setProxy if useProxy=true
        if (useProxy){
            logger.debug("Set Proxy = "+host+":"+port);
            builder.setProxy(host, Integer.parseInt(port));}


        //add header with tester name to the builder
        //builder.addHeader("TesterName", userName);


        //create RequestSpecification and add logging

        //RequestSpecification requestSpec = builder.build().log().all();
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

    //Validate if node present on response
    //this is standard RestAssured method. Path should be on Rest Assured format.
    // It does not work for that way how we present Arrays on Watchmen.
    //use JSONHelper instead
/*
    public Boolean isNodePresentedOnBody(ResponseOptions<Response> response, String name) {

        try {
            if (!(response.body().path(name) == null)) {
                logger.debug("Validate If Node " + name + " presented on Body = true");
                return true;
            } else {
                logger.debug("Validate If Node " + name + " presented on Body = false");
                return false;
            }
        }
        catch (Exception e) {
            logger.debug("Validate If Node " + name + " presented on Body = false");
            return false;
        }

    }
    */



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



    public void setResponseLoggingToFile(Scenario scenario){

        // save rest-assured logging to the file scenarioID.txt

        logger.debug("Save request-response logs to the file: "+scenario.getId().replaceAll("/","_")+".txt");
        try {

           // Path logDir = Paths.get("./logs/watchmen/requestResponse/");
            //+File.separator
            Path logDir = Paths.get("."+File.separator+"logs"+File.separator+"watchmen"+File.separator+"requestResponse"+File.separator);


            // create logs directory path
            // if the log-directory doesn't exist then create it
            if (Files.notExists(logDir)) {
                try { Files.createDirectories(logDir); }
                catch (Exception e ) { e.printStackTrace(); }
            }

            // create logs subdirectory path
            String pattern = "yyyy-MM-dd";
            Path confDir = Paths.get(logDir+File.separator+LocalDate.now().format(DateTimeFormatter.ofPattern(pattern))+File.separator);


            // if the sub-directory doesn't exist then create it
            if (Files.notExists(confDir)) {
                try { Files.createDirectories(confDir); }
                catch (Exception e ) { e.printStackTrace(); }
            }
            File outputFile = new File(confDir+File.separator+scenario.getId().replaceAll("/","_").replaceAll("\\\\","_")+".txt");
            FileWriter fileWriter = new FileWriter(outputFile);
            PrintStream printStream = new PrintStream(new WriterOutputStream(fileWriter), true);

            RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(printStream));
        }
        catch (Exception e) {
            logger.error("Error to create "+scenario.getId().replaceAll("/","_")+ " file, so will log to console");
        }
    }



}


