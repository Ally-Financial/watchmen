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
import com.ally.d3.watchmen.utilities.dataDriven.PlaceholderResolve;
import com.ally.d3.watchmen.utilities.dataDriven.ReadFile;
import com.ally.d3.watchmen.utilities.dataDriven.XMLHelper;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;


public class RequestHelper {

    private static final Logger logger = LoggerFactory.getLogger(RequestHelper.class);

    @Autowired
    RestAssuredHelper restAssuredHelper;

    @Autowired
    JsonHelper jsonHelper;

    @Autowired
    XMLHelper xmlHelper;

    @Autowired
    TestScope testScope;

    @Autowired
    PlaceholderResolve placeholderResolve;

    @Autowired
    ReadFile readFile;

    @Autowired
    RestAssuredRequestFilter enableRestAssuredLogs;

    //Create request specification: URL, Ally Proxy settings and Ally Proxy Authorization,
    //By default add header with Tester name to all requests for tracing

    public void startBuildingRequestForAPI(String url) {

        //specify requestSpecification
        testScope.setRequestSpec(restAssuredHelper.specifyRequestForURL(testScope.getRequestSpec(),url));

    }

    //Add Headers to existing RequestSpecification

    public void addHeadersToRequest(Map<String, String> headers) {

        logger.debug("Add headers to the RequestSpec");
        testScope.setRequestSpec(testScope.getRequestSpec().headers(headers));

    }

    //Add Form data  to existing RequestSpecification
    public void addFormDataToRequest(Map<String, String> formData) {

        logger.debug("Add form data to the RequestSpec");
        testScope.setRequestSpec(testScope.getRequestSpec().formParams(formData));

    }


    //Add cookies to existing RequestSpecification

    public void addCookiesToRequest(Map<String, String> cookies) {

        logger.debug("Add cookies to the RequestSpec");
        testScope.setRequestSpec(testScope.getRequestSpec().cookies(cookies));

    }

    //Add path variables to existing RequestSpecification
    // URL with path param should look like api.ally.com/com.ally.d3.watchmen.demo/products/{id}/{id2} --> api.ally.com/com.ally.d3.watchmen.demo/products/123/456
    // where "/{id}/{id2}" - requestBasePath
    //and "/123/456" - requestBasePath after replacing key with values

    public void addPathVariablesToRequest(Map<String, String> params) {

        //StringBuilder requestBasePath = new StringBuilder();
        //Get all the key from Map params to build requestBasePath
        Set<String> keys = params.keySet();
        for (String k : keys) {
            testScope.setRequestBasePath(testScope.getRequestBasePath().append("/{" + k + "}"));
        }

        //add requestBasePath to URL
        logger.debug("Add requestBasePath to the URL");
        testScope.setRequestSpec(testScope.getRequestSpec().basePath(testScope.getRequestBasePath().toString()));

        //replace path param keys with path values
        logger.debug("Replace path param keys with path values");
        testScope.setRequestSpec(testScope.getRequestSpec().pathParams(params));

    }

    //Add Query param to existing RequestSpecification
    // URL with query param should look like api.ally.com/com.ally.d3.watchmen.demo/products?id=123
    // where ?id=123 - query parameters

    public void addQueryParamToRequest(Map<String, String> queryParam) {

        logger.debug("Add queryParams Map to the RequestSpec");
        testScope.setRequestSpec(testScope.getRequestSpec().queryParams(queryParam));

    }

    public void addQueryParamToRequest(String key, String value) {

        logger.debug("Add queryParams to the RequestSpec");
        testScope.setRequestSpec(testScope.getRequestSpec().queryParams(key,value));

    }

    //Add Base Path
    // URL with query param should look like api.ally.com/com.ally.d3.watchmen.demo/products?id=123
    // where ?id=123 - query parameters

    public void addBasePathToRequest(String basePathtoAdd) {

        logger.debug("Add basePath to the RequestSpec");
        if (!basePathtoAdd.startsWith("/")){
            testScope.setRequestBasePath(testScope.getRequestBasePath().append("/"+basePathtoAdd));
        }
        else {
            testScope.setRequestBasePath(testScope.getRequestBasePath().append(basePathtoAdd));
        }
        testScope.setRequestSpec(testScope.getRequestSpec().basePath(testScope.getRequestBasePath().toString()));

    }


    //Add  body as string to existing RequestSpecification

    public void addBodyAsStringToRequest(String body) {

        logger.debug("Add String as a body to the RequestSpec");
        testScope.setRequestSpec(testScope.getRequestSpec().body(body));


    }

    //Add  body as string to existing RequestSpecification

    public void addBodyAsFileToByteArray(String file) {

        logger.debug("Convert file to the byte array and provide as a body");
        byte[] fileBytes =  readFile.readFileAsByteArray(file);
        testScope.setRequestSpec(testScope.getRequestSpec().body(fileBytes));
    }



    public void addJSONBodyAsStringToRequest(String body) {

        logger.debug("Add String as a body to the RequestSpec ");
        testScope.setRequestSpec(testScope.getRequestSpec().body(body));

        //try to save String as a JsonNode in test scope to be able to change it on the next steps

    }

    public void addXMLBodyAsStringToRequest(String body) {

        logger.debug("Add String as body to the RequestSpec ");
        testScope.setRequestSpec(testScope.getRequestSpec().body(body));

    }

    //remove node from JSON body
    //path should be on format "/node/node"
   public void removeNodeFromJSON(String node) {

    logger.debug("Remove Node "+node+" from the JSON payloads");

    //jsonTree will be automatically updated on TestScope
    //in case of error jsonTree remains the same and step  fail
       JsonNode requestJsonBody=getRequestBodyFromSpecAsJson(testScope.getRequestSpec());
       jsonHelper.removeNodeFromJSON(requestJsonBody,node);


    //Set JsonNode Json tree as Request body
    setJsonTreeAsRequestBody(requestJsonBody);

}

    //Set JSON node to String
    //Set JSON node to String \format {node: <value>}
    public void setBodyNodeToString(String path, String value) {

        logger.debug("Set JSON body node "+path+" as String "+value);

        //set Json field with path to new value
        //jsonTree will be automatically updated on TestScope
        //in case of error jsonTree remains the same and step fail

        JsonNode requestJsonBody=getRequestBodyFromSpecAsJson(testScope.getRequestSpec());
        jsonHelper.setJsonNodeToString(requestJsonBody,path,value);

         //Set JsonNode Json tree as Request body
         setJsonTreeAsRequestBody(requestJsonBody);

    }

    //Set JSON node to String as String format {node: "<value>"}
    public void setBodyNodeToString_StringFormat(String path, String value) {


        logger.debug("Set JSON body node "+path+" as String as String Format"+value);

        //to set new value as a String format new value should be in format "<value>"

        JsonNode requestJsonBody=getRequestBodyFromSpecAsJson(testScope.getRequestSpec());
        jsonHelper.setJsonNodeToString_StringFormat(requestJsonBody,path,value);

        //Set JsonNode Json tree as Request body
        setJsonTreeAsRequestBody(requestJsonBody);

    }

    //Add JSON node
    //new node will look like  {node: <value>}
    public void addNodeToJSON(String parentPath, String node, String value) {

        logger.debug("Add Node to the parent path "+node+" as a String "+value);

        //set Json field with path to new value
        //jsonTree will be automatically updated on TestScope
        //in case of error jsonTree remains the same and step fail

        JsonNode requestJsonBody=getRequestBodyFromSpecAsJson(testScope.getRequestSpec());

        jsonHelper.addNodeToJSON(requestJsonBody,parentPath,node,value);

        //Set updated Json tree as Request body
        setJsonTreeAsRequestBody(requestJsonBody);

    }

    //Add JSON node
    //new node will look like  {node: <value>}
    public void addNodeToArrayJSON(String parentPath, String value) {

        logger.debug("Add Node to the Array parent "+ parentPath+" as a String "+value);

        //add new item to the Json array node
        //jsonTree will be automatically updated on TestScope
        //in case of error jsonTree remains the same and step fail

        JsonNode requestJsonBody=getRequestBodyFromSpecAsJson(testScope.getRequestSpec());

        jsonHelper.addItemToArrayNode(requestJsonBody,parentPath,value);

        //Set updated Json tree as Request body
        setJsonTreeAsRequestBody(requestJsonBody);

    }


    //Add JSON Node in String format
    //new node will look like  {node: "<value>"}
    public void addNodeToJSON_StringFormat(String parentPath, String node, String value) {

        logger.debug("Add Node to the parent path "+node+" as a String "+value);

        //set Json field with path to new value
        //jsonTree will be automatically updated on TestScope
        //in case of error jsonTree remains the same and step fail

        JsonNode requestJsonBody=getRequestBodyFromSpecAsJson(testScope.getRequestSpec());

        jsonHelper.addNodeToJSON_StringFormat(requestJsonBody,parentPath,node,value);

         //Set updated Json tree as Request body
        setJsonTreeAsRequestBody(requestJsonBody);

    }

    //Copy JsonTree from ParentPath
    //Add as a new node ander newParent
    public void copyNodeAddAsNewNode(String parentPath, String newParentPath, String newNode) {

        logger.debug("Copy Node "+parentPath+" As New Node "+newNode);

        JsonNode requestJsonBody=getRequestBodyFromSpecAsJson(testScope.getRequestSpec());

        JsonNode treeToCopy=jsonHelper.getTreeFromJson(requestJsonBody,parentPath);
        //convert treeToCopy to String format
        String treeToCopyAsString = jsonHelper.convertJsonTreeToString(treeToCopy);
        addNodeToJSON(newParentPath, newNode, treeToCopyAsString);

    }

    public void copyNodeAddAsArray(String parentPath, String newParentPath) {

        logger.debug("Copy Node "+parentPath+" add under Array node "+newParentPath);


        JsonNode requestJsonBody=getRequestBodyFromSpecAsJson(testScope.getRequestSpec());
        JsonNode treeToCopy=jsonHelper.getTreeFromJson(requestJsonBody,parentPath);

        //convert treeToCopy to String format
        String treeToCopyAsString = jsonHelper.convertJsonTreeToString(treeToCopy);
        addNodeToArrayJSON(newParentPath,treeToCopyAsString);

    }




    //Set JsonNode Json tree as Request body
    public void setJsonTreeAsRequestBody(JsonNode jsonTree) {

        logger.debug("Set Json tree as a Request body");

            //convert Json Tree to String

            String newJsonBodyAsString = jsonHelper.convertJsonTreeToString(jsonTree);

            //add String body to the request

            addJSONBodyAsStringToRequest(newJsonBodyAsString);
        }



    //Set XML node to String
    public void setXmlBodyNodeToString(String path, String value) {

        logger.debug("Set XML body node: "+path+" as a String "+value);

        Document requestXMLBody = getRequestBodyFromSpecAsXMLDoc(testScope.getRequestSpec());


        //Dom Document will be automatically updated on TestScope
        //in case of error Dom Document remains the same and step fail

        xmlHelper.setXmlNode(requestXMLBody, path, value);

        //convert Dom Document back to to String

            String newXmlBodyAsString = xmlHelper.createStringFromDocument(requestXMLBody);

            //add String body to request

            addXMLBodyAsStringToRequest( newXmlBodyAsString);
    }


    //Add XML node
    public void addNodeToXml(String parentPath,String node, String value) {

        logger.debug("Add Node to the parent node "+node+", as a String "+value);
        Document requestXMLBody = getRequestBodyFromSpecAsXMLDoc(testScope.getRequestSpec());
      try {
          xmlHelper.addXmlNode(requestXMLBody, parentPath, node, value);
      }
      catch (RuntimeException e){
          logger.error("Not able to proceed to the parent xml node: "+ parentPath);
          Assert.assertTrue("Not able to proceed to the parent xml node: "+ parentPath, false);
      }

        //convert Dom Document back to the String

        String newXmlBodyAsString = xmlHelper.createStringFromDocument(requestXMLBody);

        //add String body to request

        addXMLBodyAsStringToRequest(newXmlBodyAsString);

    }

    //Add XML node Attribute
    public void addNodeAttrToXml(String attr,String node, String value) {

        logger.debug("Add attribute "+attr+", as a String "+value);
        Document requestXMLBody = getRequestBodyFromSpecAsXMLDoc(testScope.getRequestSpec());

        try {
            xmlHelper.addXmlNodeAttribute(requestXMLBody, attr, node, value);
        }
        catch (RuntimeException e){
            logger.error("Not able to proceed to the xml node: "+ node);
            Assert.assertTrue("Not able to proceed to the xml node: "+ node, false);
        }

        //convert Dom Document back to the String

        String newXmlBodyAsString = xmlHelper.createStringFromDocument(requestXMLBody);

        //add String body to request

        addXMLBodyAsStringToRequest(newXmlBodyAsString);

    }

    //remove node from XML body
    //path must be on format "/node/node"
    public void removeNodeFromXML(String path) {

        logger.debug("Remove XML Node " + path + ", from the XML payload");

        Document requestXMLBody = getRequestBodyFromSpecAsXMLDoc(testScope.getRequestSpec());

        //in case of error xml remains the same and step  fail

        try {
            xmlHelper.removeXmlNode(requestXMLBody, path);
        }
        catch (RuntimeException e){
            logger.error("Not able to remove xml node: "+ path);
            Assert.assertTrue("Not able to remove xml node: "+ path, false);
        }

        //convert Dom Document back to the String

        String newXmlBodyAsString = xmlHelper.createStringFromDocument(requestXMLBody);

        //add String body to request

        addXMLBodyAsStringToRequest(newXmlBodyAsString);
    }


    //remove node attribute from XML body
    //path must be on format "/node/node"
    public void removeNodeAttrFromXML(String path, String attr) {

        logger.debug("Remove attribute: "+attr+" from the XML Node " + path);

        Document requestXMLBody = getRequestBodyFromSpecAsXMLDoc(testScope.getRequestSpec());

        //in case of error xml remains the same and step  fail

        try {
            xmlHelper.removeXmlNodeAttribute(requestXMLBody, path,  attr);
        }
        catch (RuntimeException e){
            logger.error("Not able to remove attribute "+attr+" from xml node: "+ path);
            Assert.assertTrue("Not able to remove attribute "+attr+" from xml node: "+ path, false);
        }

        //convert Dom Document back to the String

        String newXmlBodyAsString = xmlHelper.createStringFromDocument(requestXMLBody);

        //add String body to request

        addXMLBodyAsStringToRequest(newXmlBodyAsString);
    }



    //----------------------------------------------------------------------------------------------------------------
    //     Execute request
    //----------------------------------------------------------------------------------------------------------------

    //Take RequestSpecification built on previous steps and Execute it
    //Execute request depends on Type

    public ResponseOptions<Response> executeRequestAndGetResponse(RequestSpecification requestSpec, String requestType) {

        logger.debug("Execute Request: "+requestType+" and get the Response");
        String requestTypeUpperCase = requestType.toUpperCase();

        switch (requestTypeUpperCase) {
            case "GET":
                return given().spec(requestSpec).get();
            case "PUT":
                return given().spec(requestSpec).put();
            case "POST":
                return given().spec(requestSpec).post();
            case "DELETE":
                return given().spec(requestSpec).delete();
            case "PATCH":
                return given().spec(requestSpec).patch();
            default:
                return given().spec(requestSpec).get();
        }
    }


    public ValidatableResponse executeRequestToValidate(String requestType) {

        logger.debug("Execute Request: "+requestType+" And Get Response To Validate");
        String requestTypeUpperCase = requestType.toUpperCase();

        switch (requestTypeUpperCase) {
            case "GET":
                return given().spec(testScope.getRequestSpec()).filter(enableRestAssuredLogs).get().then().log().status();
            case "PUT":
                return given().spec(testScope.getRequestSpec()).filter(enableRestAssuredLogs).put().then().log().status();
            case "POST":
                return given().spec(testScope.getRequestSpec()).filter(enableRestAssuredLogs).post().then().log().status();
            case "DELETE":
                return given().spec(testScope.getRequestSpec()).filter(enableRestAssuredLogs).delete().then().log().status();
            case "PATCH":
                return given().spec(testScope.getRequestSpec()).filter(enableRestAssuredLogs).patch().then().log().status();
            default:
                return given().spec(testScope.getRequestSpec()).filter(enableRestAssuredLogs).get().then().log().status();

        }
        }

    public Map<String, String> resolveAllPlaceholders (Map<String, String> map ) {

        logger.debug("Check and replace all {{property_name}} variables with corresponding properties from properties file");
        Map<String, String> newMap1 = placeholderResolve.replacePropertiesOnMap(map);

        logger.debug("Check and replace all <property_name> variables with corresponding properties from a Scenario Scope");
        Map<String, String> newMap2 = placeholderResolve.replaceContainerValueOnMap(newMap1);

        logger.debug("Check and replace all %property_name% variables with function execution result");
        Map<String, String> newMap3 = placeholderResolve.replaceFunctionOnMap(newMap2);

        return newMap3;
    }

    public String resolveAllPlaceholders (String stringValue ) {

        logger.debug("Check and replace all {{property_name}} variables with corresponding properties from properties file");
        String newValue1 = placeholderResolve.replacePropertiesString(stringValue);

        logger.debug("Check and replace all <property_name> variables with corresponding properties from a Scenario Scope");
        String newValue2 = placeholderResolve.replaceContainerValueString(newValue1);

        logger.debug("Check and replace all %property_name% variables with function execution result");
        String newValue3 = placeholderResolve.replaceFunctionString(newValue2);

        return newValue3;
    }

    public String resolveAllPlaceholdersURL (String url ) {

        return placeholderResolve.replacePlaceholdersURL(url);
    }

    public void storeInScenarioScopeMyVal (String key, String val) {

        //First - resolve all placeholders
        String newVal = resolveAllPlaceholders(val);

        logger.debug("Add to the Scenario scope new key and value " + key + " = " + newVal);

        testScope.saveInContainer(key, newVal);
    }




    public String getRequestBodyFromSpecAsString (RequestSpecification requestSpec) {

        QueryableRequestSpecification queryableRequestSpecification = SpecificationQuerier.query(requestSpec);
        return queryableRequestSpecification.getBody().toString();

    }

    public JsonNode getRequestBodyFromSpecAsJson (RequestSpecification requestSpec) {

        JsonNode requestBodyAsJson;

        try {
            requestBodyAsJson= jsonHelper.readJsonAsTree(getRequestBodyFromSpecAsString(requestSpec));
            logger.debug("Request Body is recognized as an JSON");
        }
        catch (RuntimeException e){
            logger.debug("Could NOT recognize Body as JSON. Return empty Json {}");
            requestBodyAsJson= jsonHelper.readJsonAsTree("{}");
        }

        return requestBodyAsJson;

    }

    public void storeInScenarioScopeJSONRequestBody (RequestSpecification requestSpec,String key) {

        testScope.saveInJsonContainer(key,getRequestBodyFromSpecAsJson(requestSpec));
    }


    public Document getRequestBodyFromSpecAsXMLDoc (RequestSpecification requestSpec) {

        Document bodyXmlDoc;
        try {
            bodyXmlDoc= xmlHelper.createDocumentFromString(getRequestBodyFromSpecAsString(requestSpec));
            logger.debug("Request body is recognized as an XML and parsed to org.w3c.dom.Document. org.w3c.dom.Document saved on Test scope");
        }
        catch (RuntimeException e){
            logger.debug("Could NOT recognize Request body as an XML and parse it to org.w3c.dom.Document");
            bodyXmlDoc = xmlHelper.createDocumentFromString("<>");
        }

        return bodyXmlDoc;

    }
}



