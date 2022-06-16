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
import com.ally.d3.watchmen.utilities.aws.*;
import com.ally.d3.watchmen.utilities.dataDriven.DefaultDataHelper;
import com.ally.d3.watchmen.utilities.dataDriven.JsonHelper;
import com.ally.d3.watchmen.utilities.dataDriven.PlaceholderResolve;
import com.ally.d3.watchmen.utilities.dataDriven.ReadFile;
import com.fasterxml.jackson.databind.JsonNode;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AwsStepsDefinition {

    private static final Logger logger = LoggerFactory.getLogger(AwsStepsDefinition.class);


    //Inject dependency - to create an instance of class  RequestHelper
    @Autowired
    RequestHelper requestHelper;

    //Inject dependency - to share state between steps in one scenario
    @Autowired
    TestScope testScope;

    @Autowired
    com.ally.d3.watchmen.utilities.aws.AwsDynamoDBHelper AwsDynamoDBHelper;

    @Autowired
    JsonHelper jsonHelper;

    @Autowired
    AwsS3Helper awsS3Helper;

    @Autowired
    ReadFile readFile;

    @Autowired
    WriteFile writeFile;


    @Autowired
    PlaceholderResolve placeholderResolve;

    @Autowired
    DefaultDataHelper defaultDataHelper;


    @When("I query DynamoDB table \"(.*)\" with Primary Key \"(.*)\" value \"(.*)\" and Secondary Key \"(.*)\" value \"(.*)\" and save result as JSON \"([^\"]*)\"$")
    public void queryDynamoDB_PrimaryKey_SecondaryKey_SaveResult(String tableName, String hashKey, String hashVal, String rangeKey, String rangeVal, String name) {

        //Resolve placeholders
        String newHashVal = requestHelper.resolveAllPlaceholders(hashVal);
        String newRangeVal = requestHelper.resolveAllPlaceholders(rangeVal);
        logger.info("Step: I request DynamoDB table " + tableName + " with PrimaryKey= " + newHashVal + " and SecondaryKey= " + newRangeVal + " and save result as: " + name);

        //Request DB and save result
        try {
            JsonNode response = AwsDynamoDBHelper.getItem(tableName, hashKey, newHashVal, rangeKey, newRangeVal);
            testScope.saveInJsonContainer(name, response);
        } catch (RuntimeException e) {
            Assert.assertTrue("Was NOT able to query from DynamoDB " + e, (false));
        }

    }

    @Given("^I query DynamoDB table \"([^\"]*)\" with Primary Key \"([^\"]*)\" value \"([^\"]*)\" and save result as JSON \"([^\"]*)\"$")
    public void queryDynamoDB_PrimaryKey_SaveResult(String tableName, String hashKey, String hashVal, String name) {

        //Resolve placeholders
        String newHashVal = requestHelper.resolveAllPlaceholders(hashVal);
        logger.info("Step: I request DynamoDB table " + tableName + " with PrimaryKey= " + newHashVal + "  and save result as: " + name);

        //Request DB and save result
        try {
            JsonNode response = AwsDynamoDBHelper.getItem(tableName, hashKey, newHashVal);
            testScope.saveInJsonContainer(name, response);
        } catch (RuntimeException e) {
            Assert.assertTrue("Was NOT able to query from DynamoDB " + e, (false));
        }


    }

    @And("^I assert that saved JSON \"(.*)\" node \"(.*)\" equals to value \"(.*)\"$")
    public void jsonNodeEqualsToVal(String jsonName, String node, String val) {

        //Resolve placeholders
        String newVal = requestHelper.resolveAllPlaceholders(val);
        logger.info("Step: I assert that saved JSON node equals to value "+newVal);


        logger.debug("Assert JSON Node: " + node + " Equals to String: " + newVal);

        JsonNode json = testScope.getFromJsonContainer(jsonName);

        String actualNodeValue = jsonHelper.getJSONnodeValue(json, node);
        Assert.assertTrue("JSON Node: " + node + " does not match expected value. Expected value is: " + newVal + "; Actual value is: " + actualNodeValue, actualNodeValue.equalsIgnoreCase(newVal));

    }


    @And("^I assert that saved JSON \"(.*)\" node \"(.*)\" contains value \"(.*)\"$")
    public void jsonNodeContainsVal(String jsonName, String node, String val) {


        //Resolve placeholders
        String newVal = requestHelper.resolveAllPlaceholders(val).toUpperCase();
        logger.info("Step: I assert that saved JSON node contains value "+newVal);


        logger.debug("Assert JSON Node: " + node + " contains String: " + newVal);

        JsonNode json = testScope.getFromJsonContainer(jsonName);

        String actualNodeValue = jsonHelper.getJSONnodeValue(json, node);
        Assert.assertTrue("JSON Node: " + node + " does not contain expected string. Expected string is: " + newVal + "; Actual node value is: " + actualNodeValue, actualNodeValue.toUpperCase().contains(newVal));

    }

    @And("^I read publicKey from Url \"([^\"]*)\" and save as a file \"([^\"]*)\"$")
    public void readPublicKeyAsFileFromUrl(String url, String fileName) {

        logger.debug("Read publicKey from Url and save as a file " + fileName);
        //consumer_key and consumer_secret comes from Env

        //resolve placeholders
        String newFileName = placeholderResolve.replacePlaceholdersURL(fileName);
        String newUrl = placeholderResolve.replacePlaceholdersURL(url);

        awsS3Helper.readPublicKeyAsFileFromUrl(newUrl, newFileName);
    }


    @And("^I encrypt file \"([^\"]*)\" with publicKey \"([^\"]*)\" and send PUT request to the S3 pre-signed URL \"([^\"]*)\" with the headers:$")
    public void encryptFile(String inputFilename, String filePublicKey, String preSignedURL, DataTable headersTable) {
        logger.info("Step: Encrypt file with publicKey and PUT it to the S3 pre-signed URL");

        //resolve placeholders
        String newInputFilename = placeholderResolve.replacePlaceholdersURL(inputFilename);
        String newFilePublicKey = placeholderResolve.replacePlaceholdersURL(filePublicKey);
        String newUrl = placeholderResolve.replaceContainerValueString(preSignedURL);
        String outputFilename = defaultDataHelper.getTimeMs() + ".gpg";

        //get headers:
        Map<String, String> headers = headersTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newHeaders = requestHelper.resolveAllPlaceholders(headers);


        // first - encrypt file and save it as *.gpg
        try {
            awsS3Helper.encryptFile(newInputFilename, outputFilename, newFilePublicKey,false);
        } catch (Throwable e) {
            Assert.assertTrue("NOT able to encrypt file" + e, false);
        }


        // second - upload *.gpg file to the pre-signed S3 URL
        try {
            awsS3Helper.uploadFileToS3(outputFilename, newUrl,newHeaders);

        } catch (Throwable e) {
            Assert.assertTrue("NOT able to upload file. Error " + e, false);
        }


    }

    @And("^I encrypt file \"([^\"]*)\" with publicKey \"([^\"]*)\" using armored format and send PUT request to the S3 pre-signed URL \"([^\"]*)\" with the headers:$")
    public void encryptFileArmoring(String inputFilename, String filePublicKey, String preSignedURL, DataTable headersTable) {
        logger.info("Step: Encrypt file with publicKey using armored format and PUT it to the S3 pre-signed URL");

        //get headers:
        Map<String, String> headers = headersTable.asMap(String.class, String.class);

        //Resolve placeholders
        Map<String, String> newHeaders = requestHelper.resolveAllPlaceholders(headers);


        //resolve placeholders
        String newInputFilename = placeholderResolve.replacePlaceholdersURL(inputFilename);
        String newFilePublicKey = placeholderResolve.replacePlaceholdersURL(filePublicKey);
        String newUrl = placeholderResolve.replaceContainerValueString(preSignedURL);
        String outputFilename = defaultDataHelper.getTimeMs() + ".gpg";




        // first - encrypt file and save it as *.gpg
        try {
            awsS3Helper.encryptFile(newInputFilename, outputFilename, newFilePublicKey,true);
        } catch (Throwable e) {
            Assert.assertTrue("NOT able to encrypt file" + e, false);
        }


        // second - upload *.gpg file to the pre-signed S3 URL
        try {
            awsS3Helper.uploadFileToS3(outputFilename, newUrl,newHeaders);

        } catch (Throwable e) {
            Assert.assertTrue("NOT able to upload file. Error " + e, false);
        }


    }

}









