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

package com.ally.d3.watchmen.utilities.aws;

import com.ally.d3.watchmen.steps.TestScope;
import com.ally.d3.watchmen.utilities.dataDriven.JsonHelper;
import com.ally.d3.watchmen.utilities.dataDriven.ReadFile;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

public class AwsDynamoDBHelper {


    @Autowired
    JsonHelper jsonHelper;


    @Value("${region:us-east-1}")
    private String region;


    final static Logger logger = LoggerFactory.getLogger(AwsDynamoDBHelper.class);
    //DateUtils dateUtils = new DateUtils();
    //public static String jsonBody = null;
    //Make the tokens global

    public final DynamoDB getDynamoDBConnect() {


        AmazonDynamoDB amazonDynamoDb = AmazonDynamoDBAsyncClientBuilder.standard().withRegion(region).build();

        DynamoDB dynamoDb = new DynamoDB(amazonDynamoDb);

        return dynamoDb;
    }

  //Retrieves an item by primary key when the primary key consists of both a hash-key and a range-key.
    @Retryable(maxAttemptsExpression = "${max.attempts.query.db}",value=RuntimeException.class,backoff = @Backoff(delay = 1000))
    public final JsonNode getItem (String TableName, String HashKey, Object HashKeyValue, String RangeKey, Object RangeKeyValue) {


        DynamoDB dynamoDb = getDynamoDBConnect();

        Item item = null;
        JsonNode responeTree = null;

        logger.debug("Connecting to the amazonDynamoDb...");

        //read teable

        Table table = dynamoDb.getTable(TableName);

         //request from the table

        try {
            item = table.getItem(HashKey, HashKeyValue, RangeKey, RangeKeyValue);
            //System.out.println(item.toString());
            dynamoDb.shutdown();

        } catch (Exception e) {
            dynamoDb.shutdown();
            logger.error("Cannot retrieve item from the DynamoDB "+e);
            throw new RuntimeException("Cannot retrieve item from DynamoDB: "+e.getMessage());
        }

        try {

            String response = item.toJSONPretty();

            System.out.println("response as Pretty JSON: " + response);
            logger.debug("DynamoDB response as Pretty JSON: " + response);

            responeTree = jsonHelper.readJsonAsTree(response);

        }

        catch (Exception e) {
            logger.error("Cannot parse response as a JSON "+e);
            throw new RuntimeException("Cannot parse response as a JSON. "+e.getMessage());
        }
        return responeTree;
    }

    //Retrieves an item by primary key when the primary key is a hash-only key.
    @Retryable(maxAttemptsExpression = "${max.attempts.query.db}",value=RuntimeException.class,backoff = @Backoff(delay = 1000))
    public final JsonNode getItem (String TableName, String HashKey,Object HashKeyValue) {

        DynamoDB dynamoDb = getDynamoDBConnect();
        Item item = null;
        JsonNode responeTree = null;

        logger.debug("Connecting to the amazonDynamoDb...");

        //read teable

        Table table = dynamoDb.getTable(TableName);

        //request from the table

        try {
            item = table.getItem(HashKey, HashKeyValue);
            dynamoDb.shutdown();
            //System.out.println(item.toString());

        } catch (Exception e) {
            dynamoDb.shutdown();
            logger.error("Cannot retrieve item from the DynamoDB "+ e);
            throw new RuntimeException("Cannot retrieve item from DynamoDB: "+e.getMessage());
        }

        try {

            String response = item.toJSONPretty();

            System.out.println("response as Pretty JSON: " + response);
            logger.debug("DynamoDB response as a Pretty JSON: " + response);

            responeTree = jsonHelper.readJsonAsTree(response);

        }

        catch (Exception e) {
            logger.error("Cannot parse response as a JSON "+e);
            throw new RuntimeException("Cannot parse response as a JSON. "+e.getMessage());
        }
        return responeTree;
    }

}