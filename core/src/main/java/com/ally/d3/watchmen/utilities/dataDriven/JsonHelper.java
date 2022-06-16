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

package com.ally.d3.watchmen.utilities.dataDriven;

import com.ally.d3.watchmen.steps.TestScope;
import com.ally.d3.watchmen.utilities.RequestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.bjansen.ssv.SwaggerValidator;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import org.apache.commons.io.FilenameUtils;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ValueChange;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class JsonHelper {

    @Autowired
    TestScope testScope;

    @Autowired
    ReadFile readFile;

    @Autowired
    RequestHelper requestHelper;

    private static final Logger logger = LoggerFactory.getLogger(JsonHelper.class);

    public String createJSONPayloadFromMap(Map<String, String> payload) {

        logger.debug("Create JSON payloads from map");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = "";
        try {
            jsonPayload = objectMapper.writeValueAsString(payload);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonPayload;
    }


    public JsonNode readJsonAsTree(String json) {

        JsonNode node;

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        try {
            node = mapper.readTree(json);
            logger.debug("Resulting JSON as a tree is: " + mapper.writeValueAsString(node));
            return node;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could NOT map JSON string to a tree");
        }

    }

    public JsonNode readJsonAsTreeFromFile(String file) {

        String json = readFile.readFileAsString(file);

        return readJsonAsTree(json);

    }

    public JsonNode readJsonAsTreeFromMap(Map<String, String> mapToJson) {

        JsonNode node;
        String jsonResult;

        ObjectMapper mapper = new ObjectMapper();
        //mapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        try {
            jsonResult = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(mapToJson);
            return readJsonAsTree(jsonResult);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could NOT map JSON string to a tree");

        }
    }


    //if node not found - getJSONnodeValue returns "NodeNotFound" as node value
    public String getJSONnodeValue(JsonNode jsonTree, String node) {

        if (jsonTree.isMissingNode()) {
            logger.debug("JSON Tree you try to retrieve value from is null");
            throw new RuntimeException("JSON you try to retrieve value from is NULL");
        }

        logger.debug("Get Node value from JSON path= " + node);

        //change path "node.node" to expected format /node/node/...
        String newNode = changePathFormatforJackson(node);

        String nodeVal = "";
        String nodeValReturn = "NodeNotFound";

        //convert path {/name} to node name {name}
        String currentNode;

        if (newNode.startsWith("/")) {
            currentNode = newNode.substring(1);
        } else currentNode = newNode;

        JsonNode jsonChildTree;

        //if path is nested - find parent and child


        if (currentNode.contains("/")) {


            String parentPath = currentNode.substring(0, currentNode.indexOf("/"));
            String childNode = currentNode.substring(currentNode.indexOf("/") + 1, currentNode.length());
            try {
                jsonChildTree = getSubTreeFromJson(jsonTree, parentPath);

                //recursive call
                nodeVal = getJSONnodeValue(jsonChildTree, childNode);
            } catch (RuntimeException e) {
                logger.debug("Not able to locate node: " + childNode);
                return nodeValReturn;
            }
        } else {

            //if path is not nested - just retrieve value
            try {
                nodeVal = getSubTreeFromJson(jsonTree, currentNode).toString();
            } catch (RuntimeException e) {
                logger.debug("Not able to locate node: " + currentNode);
                return nodeValReturn;
            }

        }


        if (nodeVal.startsWith("\"") & nodeVal.endsWith("\""))
            nodeValReturn = nodeVal.substring(1, nodeVal.length() - 1);
        else nodeValReturn = nodeVal;

        return nodeValReturn.trim();


    }

    public List getJSONnodeAsList(JsonNode jsonTree, String node) {

        if (jsonTree.isMissingNode()) {
            logger.debug("JSON Tree you try to retrieve value from is null");
            throw new RuntimeException("JSON you try to retrieve value from is NULL");
        }

        logger.debug("Get Node value from JSON path = " + node + " as List");

        //change path "node.node" to expected format /node/node/...
        String newNode = changePathFormatforJackson(node);

        //convert path {/name} to node name {name}
        String currentNode;

        List nodeValReturn = new ArrayList();

        if (newNode.startsWith("/")) {
            currentNode = newNode.substring(1);
        } else currentNode = newNode;

        JsonNode jsonChildTree;

        //if path is nested - find parent and child


        if (currentNode.contains("/")) {


            String parentPath = currentNode.substring(0, currentNode.indexOf("/"));
            String childNode = currentNode.substring(currentNode.indexOf("/") + 1, currentNode.length());
            try {
                jsonChildTree = getSubTreeFromJson(jsonTree, parentPath);

                //recursive call
                nodeValReturn = getJSONnodeAsList(jsonChildTree, childNode);
            } catch (RuntimeException e) {
                logger.debug("Not able to locate node: " + childNode);
                return nodeValReturn;
            }


        } else {

            //if path is not nested - just retrieve values

            try {
                nodeValReturn = getSubTreeFromJson(jsonTree, currentNode).findValues(currentNode);
            } catch (RuntimeException e) {
                logger.debug("Not able to locate node: " + currentNode);
                return nodeValReturn;
            }


            if ((getSubTreeFromJson(jsonTree, currentNode)).isArray()) {
                logger.debug("Node: " + node + " is an Array Node");
            } else logger.debug("Node: " + node + " is NOT an Array Node");


        }

        return nodeValReturn;


    }


    public Integer getJSONnodeArraySize(JsonNode jsonTree, String node) {

        if (jsonTree.isMissingNode()) {
            logger.debug("JSON Tree you try to retrieve value from is null");
            throw new RuntimeException("JSON you try to retrieve value from is NULL");
        }

        logger.debug("Get Node value from JSON path = " + node + " as List");

        //change path "node.node" to expected format /node/node/...
        String newNode = changePathFormatforJackson(node);
        Integer size;

        //convert path {/name} to node name {name}
        String currentNode;

        List nodeValReturn = new ArrayList();

        if (newNode.startsWith("/")) {
            currentNode = newNode.substring(1);
        } else currentNode = newNode;

        JsonNode jsonChildTree;

        //if path is nested - find parent and child


        if (currentNode.contains("/")) {


            String parentPath = currentNode.substring(0, currentNode.indexOf("/"));
            String childNode = currentNode.substring(currentNode.indexOf("/") + 1, currentNode.length());
            try {
                jsonChildTree = getSubTreeFromJson(jsonTree, parentPath);
                //recursive call
                size = getJSONnodeArraySize(jsonChildTree, childNode);
            } catch (RuntimeException e) {
                logger.debug("Not able to locate node: " + childNode);
                return 0;
            }


        } else {

            //if path is not nested - just check if it array node
            //if node is array - return size
            //if node is not array - return 0


            if ((getSubTreeFromJson(jsonTree, currentNode)).isArray()) {
                size= getSubTreeFromJson(jsonTree, currentNode).size();
                logger.debug("Node: " + node + " is an Array Node Size = " + size);
                return size;
            } else logger.debug("Node: " + node + " is NOT an Array Node");
            return 0;


        }

        return size;
    }


    public String changePathFormatforJackson(String path) {
        //change path "node.node" to expected format /node/node/...
        String newPath;
        if (!path.startsWith("/"))
            newPath = "/" + path.replace(".", "/");
        else
            newPath = path.replace(".", "/");
        return newPath;

    }


    //Set JSON node to String
    //path should be on format "/node/node"


    public void setJsonNodeToString(JsonNode jsonTree, String path, String value) {

        if (jsonTree.isMissingNode()) {
            logger.debug("JSON Tree you try to modify is null");
            throw new RuntimeException("JSON Tree you try to modify is null");
        }

        logger.debug("Set Json Node: " + path);

        ObjectMapper mapper = new ObjectMapper();

        //change path "node.node" to expected format /node/node/...
        String newPath = changePathFormatforJackson(path);


        //convert path {/name} to node name {name}
        String currentNode;

        if (newPath.startsWith("/")) {
            currentNode = newPath.substring(1);
        } else currentNode = newPath;

        //if path is nested - find parent and child

        if (currentNode.contains("/")) {


            String parentPath = currentNode.substring(0, currentNode.indexOf("/"));
            String childNode = currentNode.substring(currentNode.indexOf("/") + 1, currentNode.length());
            JsonNode jsonChildTree = getSubTreeFromJson(jsonTree, parentPath);

            //recursive call
            setJsonNodeToString(jsonChildTree, childNode, value);

        } else {

            //if path is not nested - update value
            JsonNode newValue;

            try {
                newValue = mapper.readTree(value);


            } catch (JsonProcessingException e) {
                logger.debug("Could NOT process new value");
                throw new RuntimeException("Could NOT process new value");
            }

            //update Json tree
            //JsonNode is immutable. So, cast it into ObjectNode that allow mutations:

            if (jsonTree.path(currentNode).isMissingNode()) {
                logger.debug("Could NOT process to the path: " + currentNode);
                throw new RuntimeException("Could NOT process to the path: " + currentNode);
            } else {
                logger.debug("Set Json Node: " + path + " as new value = " + newValue);
                ((ObjectNode) jsonTree).set(currentNode, newValue);
            }

        }
    }

    public void setJsonNodeToString_StringFormat(JsonNode jsonTree, String path, String value) {

        logger.debug("Set Json Node: " + path + " in String format as "+value);
        //to set new value as a String format new value should be in format "<value>"
        String newValueStringFormat;

        if (!(value.startsWith("\"") & value.endsWith("\""))) {
            newValueStringFormat = "\"" + value + "\"";
        } else newValueStringFormat = value;

        setJsonNodeToString(jsonTree, path, newValueStringFormat);
    }


    //Remove node from JSON tree
    //path should be on format "/node/node"

    public void removeNodeFromJSON(JsonNode jsonTree, String node) {

        if (jsonTree.isMissingNode()) {
            logger.debug("JSON Tree you try to modify is null");
            throw new RuntimeException("JSON Tree you try to modify is null");
        }

        logger.debug("Remove node from the JSON parent node: " + node);


        //change path "node.node" to expected format /node/node/...
        String newNode = changePathFormatforJackson(node);


        //convert path node {/name} to node name {name}
        String nodeToRemove;

        if (newNode.startsWith("/")) {
            nodeToRemove = newNode.substring(1);
        } else nodeToRemove = newNode;

        //if path is nested - find parent and child

        if (nodeToRemove.contains("/")) {


            String newParentPath = nodeToRemove.substring(0, nodeToRemove.indexOf("/"));
            String childNode = nodeToRemove.substring(nodeToRemove.indexOf("/") + 1, nodeToRemove.length());
            JsonNode jsonChildTree = getSubTreeFromJson(jsonTree, newParentPath);

            //recursive call
            removeNodeFromJSON(jsonChildTree, "/" + childNode);


        } else if (jsonTree.isArray()) {
            String arrayItem = nodeToRemove.substring(4, nodeToRemove.length() - 1);
            ((ArrayNode) jsonTree).remove(Integer.parseInt(arrayItem) - 1);
        } else {

            if (jsonTree.path(nodeToRemove).isMissingNode()) {
                logger.debug("Not able to locate node: " + nodeToRemove);
                throw new RuntimeException("Not able to locate node: " + nodeToRemove);
            } else

                ((ObjectNode) jsonTree).remove(nodeToRemove);

        }


    }

    public void addNodeToJSON(JsonNode jsonTree, String parentPath, String node, String value) {

        if (jsonTree.isMissingNode()) {
            logger.debug("JSON Tree you try to modify is null");
            throw new RuntimeException("JSON Tree you try to modify is null");
        }

        logger.debug("Add new node: " + node + " to the parent node: " + parentPath);

        //change path "node.node" to expected format /node/node/...
        String newParentPath = changePathFormatforJackson(parentPath);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode newValue;

        //check if new value can be processed
        try {
            newValue = mapper.readTree(value);
        } catch (JsonProcessingException e) {
            logger.debug("Could NOT process new value ");
            e.printStackTrace();
            throw new RuntimeException("Could NOT process new value for the node");
        }


        //if we want to add new node to the root
        if (newParentPath.equals("/")) {

            ((ObjectNode) jsonTree).putObject(node);
            ((ObjectNode) jsonTree).set(node, newValue);

        } else {

            //if we want add new node to some parent node
            //check parent node type and
            // if it is Value Node - set new value


            logger.debug("Add node to the parent: " + newParentPath);


            //convert path {/name} to node name {name}
            String currentNode;

            if (newParentPath.startsWith("/")) {
                currentNode = newParentPath.substring(1);
            } else currentNode = newParentPath;

            // ----> if path is nested - find parent and child

            if (currentNode.contains("/")) {


                String newParentPath2 = currentNode.substring(0, currentNode.indexOf("/"));
                String childNode = currentNode.substring(currentNode.indexOf("/") + 1, currentNode.length());

                JsonNode jsonChildTree = getSubTreeFromJson(jsonTree, newParentPath2);

                //recursive call
                addNodeToJSON(jsonChildTree, childNode, node, value);


            }

            // ---->  if path is not nested - add new node here
            else {

                JsonNode jsonChildTree = getSubTreeFromJson(jsonTree, currentNode);


                //if last parent is Array
                if (currentNode.toUpperCase().startsWith("GET(")) {

                    //recursive call
                    addNodeToJSON(jsonChildTree, "", node, value);
                }

                //if last parent is not Array - just add new node

                else if (jsonTree.path(currentNode).isValueNode()) {
                    logger.debug("Node: " + currentNode + " is not an Object node. Use Step 'I set JSON node..' to add nested object under it");
                    throw new RuntimeException("Node: " + currentNode + " is not an Object node. Use Step 'I set JSON node..' to add nested object under it");

                } else if (jsonTree.path(currentNode).isMissingNode()) {
                    logger.debug("Not able to locate node: " + currentNode);
                    throw new RuntimeException("Not able to locate node: " + currentNode);
                } else if (jsonTree.path(currentNode).isArray()) {
                    logger.debug("Node: " + currentNode + " is an Array node. New Item will be added under it");

                    //update Json tree
                    //JsonNode is immutable. So, cast it into ArrayNode that allow mutations:
                    //and add new item to the array


                    ((ArrayNode) jsonChildTree).add(newValue);
                } else {

                    //update Json tree
                    //JsonNode is immutable. So, cast it into ObjectNode that allow mutations:

                    ((ObjectNode) jsonChildTree).putObject(node);

                    ((ObjectNode) jsonChildTree).set(node, newValue);
                }
            }
        }


    }

    public void addItemToArrayNode(JsonNode jsonTree, String parentPath, String value) {

        if (jsonTree.isMissingNode()) {
            logger.debug("JSON Tree you try to modify is null");
            throw new RuntimeException("JSON Tree you try to modify is null");
        }

        logger.debug("Add new item: " + value + " to the array node: " + parentPath);

        //change path "node.node" to expected format /node/node/...
        String newParentPath = changePathFormatforJackson(parentPath);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode newValue;

        //check if new value can be processed
        try {
            newValue = mapper.readTree(value);
        } catch (JsonProcessingException e) {
            logger.error("Could NOT process new value "+e);
            throw new RuntimeException("Could NOT process new value for the node");
        }


        //if we want to add new node to the root
        if (newParentPath.equals("/")) {

            //update Json tree
            //JsonNode is immutable. So, cast it into ArrayNode that allow mutations:
            //and add new item to the array


            ((ArrayNode) jsonTree).add(newValue);

        } else {

            //if we want add new node to some parent node
            //check parent node type and
            // if it is Value Node - set new value


            logger.debug("Add node to the parent: " + newParentPath);


            //convert path {/name} to node name {name}
            String currentNode;

            if (newParentPath.startsWith("/")) {
                currentNode = newParentPath.substring(1);
            } else currentNode = newParentPath;

            // ----> if path is nested - find parent and child

            if (currentNode.contains("/")) {


                String newParentPath2 = currentNode.substring(0, currentNode.indexOf("/"));
                String childNode = currentNode.substring(currentNode.indexOf("/") + 1, currentNode.length());

                JsonNode jsonChildTree = getSubTreeFromJson(jsonTree, newParentPath2);

                //recursive call
                addItemToArrayNode(jsonChildTree, childNode, value);


            }

            // ---->  if path is not nested - add new node here
            else {

                JsonNode jsonChildTree = getSubTreeFromJson(jsonTree, currentNode);


                //if last parent is Array
                if (currentNode.toUpperCase().startsWith("GET(")) {

                    //recursive call
                    addItemToArrayNode(jsonChildTree, "", value);
                }

                //if last parent is not Array - just add new node

                else if (jsonTree.path(currentNode).isValueNode()) {
                    logger.debug("Node: " + currentNode + " is not an Object node. Use Step 'I set JSON node..' to add nested object under it");
                    throw new RuntimeException("Node: " + currentNode + " is not an Array node");

                } else if (jsonTree.path(currentNode).isMissingNode()) {
                    logger.debug("Not able to locate node: " + currentNode);
                    throw new RuntimeException("Not able to locate node: " + currentNode);
                } else if (jsonTree.path(currentNode).isArray()) {
                    logger.debug("Node: " + currentNode + " is an Array node. New Item will be added under it");

                    //update Json tree
                    //JsonNode is immutable. So, cast it into ArrayNode that allow mutations:
                    //and add new item to the array


                    ((ArrayNode) jsonChildTree).add(newValue);
                } else {

                    logger.debug("Node: " + currentNode + " is not an Object node. Use Step 'I set JSON node..' to add nested object under it");
                    throw new RuntimeException("Node: " + currentNode + " is not an Array node");
                }
            }
        }


    }


    //new node will look like  {node: "<value>"}
    public void addNodeToJSON_StringFormat(JsonNode jsonTree, String parentPath, String node, String value) {

        logger.debug("Add new node: " + node + " to the parent node: " + parentPath + " in String format");
        //to set new value as a String format new value should be in format "<value>"
        String newValueStringFormat;

        if (!(value.startsWith("\"") & value.endsWith("\""))) {
            newValueStringFormat = "\"" + value + "\"";
        } else newValueStringFormat = value;

        addNodeToJSON(jsonTree, parentPath, node, newValueStringFormat);
    }

    //this method will through RuntimeException if node is not found
    private JsonNode getSubTreeFromJson(JsonNode parentJson, String path) throws RuntimeException {

        JsonNode jsonChildTree;
        Integer arrayItem;

        if (path.isEmpty())
            return parentJson;

        if (path.toUpperCase().startsWith("GET(")) {

            String arrayItemStr = path.substring(4, path.length() - 1);
            //if path is like  "get(i)"
            if (isInt(arrayItemStr))
                arrayItem = Integer.parseInt(arrayItemStr);
            else
                //if path is like  "get(node=value)"
                arrayItem = getArrayItemByCondition(parentJson, path);

            jsonChildTree = parentJson.get(arrayItem - 1);
            if (Objects.isNull(jsonChildTree)) {
                logger.debug("Not able to proceed to the Array item: " + path);
                throw new RuntimeException("Not able to proceed to the Array item: " + path);
            }
        } else {


            jsonChildTree = parentJson.path(path);
            if (jsonChildTree.isMissingNode()) {
                logger.debug("Not able to proceed to the node: " + path);
                throw new RuntimeException("Not able to proceed to the node: " + path);
            }
        }
        return jsonChildTree;

    }

    public JsonNode getTreeFromJson(JsonNode jsonTree, String path) {

        if (jsonTree.isMissingNode()) {
            logger.debug("JSON Tree you try to modify is null");
            throw new RuntimeException("JSON Tree you try to modify is null");
        }

        JsonNode jsonChildTree;
        JsonNode jsonChildTree2;
        logger.debug("Set Json Node: " + path);

        ObjectMapper mapper = new ObjectMapper();

        //change path "node.node" to expected format /node/node/...
        String newPath = changePathFormatforJackson(path);


        //convert path {/name} to node name {name}
        String currentNode;

        if (newPath.startsWith("/")) {
            currentNode = newPath.substring(1);
        } else currentNode = newPath;

        //if path is nested - find parent and child

        if (currentNode.contains("/")) {


            String parentPath = currentNode.substring(0, currentNode.indexOf("/"));
            String childNode = currentNode.substring(currentNode.indexOf("/") + 1, currentNode.length());
            jsonChildTree = getSubTreeFromJson(jsonTree, parentPath);
            jsonChildTree2 = getTreeFromJson(jsonChildTree, childNode);

        } else {

            jsonChildTree2 = getSubTreeFromJson(jsonTree, currentNode);
            return jsonChildTree2;
        }
        return jsonChildTree2;
    }



    //this method will through RuntimeException if any node item matching condition
    private Integer getArrayItemByCondition(JsonNode parentJson, String condition) throws RuntimeException {

        Integer i = condition.indexOf("=");
        String node = condition.substring(4, i);
        String nodeVal = condition.substring(i + 1, condition.length() - 1);
        String newHodeVal = requestHelper.resolveAllPlaceholders(nodeVal);

        logger.debug("Find Array item where : " + node + " = " + nodeVal);

        for (int j = 0; j < parentJson.size(); j++) {
            Integer k = j + 1;
            String nodeToFound = "get(" + k + ")." + node;
            if (getJSONnodeValue(parentJson, nodeToFound).equals(newHodeVal.trim())) {
                logger.debug("Item number = " + k);
                return k;
            }
        }
        logger.debug("Not able to find Array item where : " + node + " = " + nodeVal);
        throw new RuntimeException("Not able to find Array item where : " + node + " = " + nodeVal);

    }


    //validate schema for JsonNode using Jackson library
    public ProcessingReport getReportJsonMatchSchemaFile(JsonNode jsonToValidate, String schemaFile) throws ProcessingException {

        logger.debug("Validate JSON matches schema");
        Path p;

        try {

            logger.debug("Validate if JSON schema file exists: " + schemaFile);
            p = readFile.getFilePath(schemaFile);
        } catch (Throwable e) {
            logger.error("JSON schema file does not exist "+e);
            throw new RuntimeException("Could NOT find JSON schema file: " + schemaFile);
        }

        final File jsonSchemaFile = new File(p.toString());
        final URI uri = jsonSchemaFile.toURI();


        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

        JsonSchema jsonSchema = factory.getJsonSchema(uri.toString());
        ProcessingReport processingReport = jsonSchema.validate(jsonToValidate);

        return processingReport;

    }

    public String readJSONPayloadFromFile(String jsonFile) {

        return readFile.readFileAsString(jsonFile);

    }

    private boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }


    public String convertJsonTreeToString(JsonNode jsonTree) {


        logger.debug("Conver JsonNode Tree to string ");

        String jsonAsString;

        try {
            //convert Json Tree to String

            ObjectMapper mapper = new ObjectMapper();
            jsonAsString = mapper.writeValueAsString(jsonTree);

        } catch (JsonProcessingException e) {
            logger.error("Error to map JSON to the String "+e);
            throw new RuntimeException("Error to map JSON to String" + e);
        }
        return jsonAsString;
    }

    public ProcessingReport getReportJsonMatchSchemaSwagger(String json, String swaggerFile, String definitionPointer)  {

        logger.debug("Get validation report ");
        String swaggerFileType=FilenameUtils.getExtension(swaggerFile);

        try {
            Path p = readFile.getFilePath(swaggerFile);
            InputStream specStream = Files.newInputStream(p);
            Reader specReader = new InputStreamReader(specStream);

            SwaggerValidator validator;

            switch (swaggerFileType.toUpperCase()) {
                case "YAML":
                    validator = SwaggerValidator.forYamlSchema(specReader);
                    return validator.validate(json, definitionPointer);

                case "JSON":
                    validator = SwaggerValidator.forJsonSchema(specReader);
                    return validator.validate(json, definitionPointer);

                default:
                    logger.error("Swagger schema expected in Yaml or Json format.");
                    throw new RuntimeException("Swagger schema expected in Yaml or Json format.");

            }


        }
        catch (IllegalArgumentException | IOException | ProcessingException e) {
            logger.error("Error to validate swagger schema " + e);
            throw new RuntimeException("Error to validate swagger schema " + e);
        }

    }

    public boolean isJsonEqualsJson(String json1, String json2) {

        ObjectMapper mapper = new ObjectMapper();
        try {

            return mapper.readTree(json1).equals(mapper.readTree(json2));
        } catch (JsonProcessingException e) {
            logger.error("Error to validate two JSONs" + e);
            throw new RuntimeException("Error to validate two JSONs" + e);


        }
    }

    public String printJsonDifference(String json1, String json2)  {

        StringBuilder dif = new StringBuilder();
        String finalDif = "";

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode tree1 = mapper.readTree(json1);
            JsonNode tree2 = mapper.readTree(json2);


        Javers j = JaversBuilder.javers().build();
        Diff diff = j.compare(tree1, tree2);
        if (diff.hasChanges()) {
            List<Change> changes = diff.getChanges();
            for (Change change : changes) {
                if (change instanceof ValueChange) {
                    ValueChange valChange = (ValueChange) change;
                    dif = dif.append(" || "+ valChange );

                }else
                    if (change instanceof ObjectRemoved) {

                       dif = dif.append(" || "+ change );
                }
                   else
                     if (change instanceof NewObject) {

                     dif = dif.append(" || "+ change );
            }
            }
            finalDif=dif.toString();

        }
        return finalDif;
        } catch (JsonProcessingException e) {
            logger.error("Error to process json " + e);
            throw new RuntimeException("Error to process json " + e);


        }
    }

    //Read JSOn and create map {keys,values}
    public Map<String, Object> convertJsonTreeToMap(JsonNode jsonTree, String filePath) throws JsonProcessingException {

        //LinkedHashMap will return the elements in the order they were inserted into the map

        Map<String, Object> twoLines = new LinkedHashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        try {
            twoLines = mapper.readValue(convertJsonTreeToString(jsonTree), new TypeReference<Map<String, Object>>() {
            });

            //Print JSON output
            return twoLines;
        } catch (JsonMappingException e) {
            logger.error("Error to convert JsonTree To Map " + e);
            throw new RuntimeException("Error to convert JsonTree To Map " + e);
        }
    }


    //Read JSON tree and write it as a csv file with delimiter
    public void convertJsonTreeToCSVwithDelimiter(JsonNode jsonTree, String fullFileName, String delimiter) throws IOException {

        List<String> keysArray=new ArrayList<>();
        List<String> valuesArray=new ArrayList<>();

        Iterator<String> fieldNamesIterator = jsonTree.fieldNames();

        Map<String, Object> twoLines = new LinkedHashMap<>();
        while (fieldNamesIterator.hasNext()) {
            String currentField=fieldNamesIterator.next();
            //add keys to the keysArray
            keysArray.add(currentField);
            String value=jsonTree.findValue(currentField).toString();
            //remove not needed "" from the value and add to the valuesArray
            valuesArray.add(value.substring(1,value.length()-1));
        }


        FileWriter writer = new FileWriter(fullFileName);
        String firstLine = keysArray.stream().collect(Collectors.joining(delimiter));
        writer.write(firstLine);
        writer.write("\n");
        String secondLine = valuesArray.stream().collect(Collectors.joining(delimiter));
        writer.write(secondLine);
        writer.close();

    }



}








