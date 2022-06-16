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
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Region;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;


import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import com.amazonaws.services.simplesystemsmanagement.model.PutParameterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;


public class AwsParameterStoreHelper {


    final static Logger logger = LoggerFactory.getLogger(AwsParameterStoreHelper.class);

    private static Map<String, String> parameters = null;
    private static String SECURE_STRING = "SecureString";
    private static String SLASH = File.separator;

    private AWSSimpleSystemsManagement awsSimpleSystemsManagementClient;


    //get All parameters for the Namespace for future use
    public Map<String, String> getAllParamsForNamespace(String namespace) {
        long startTime = System.currentTimeMillis();
        if (null == parameters) {
            parameters = getParametersByPath(SLASH + namespace, true);
        }
        logger.debug("Time taken: {} ms to fetch values from aws parameter store.", System.currentTimeMillis() - startTime);
    return parameters;
    }


     //Return a map of all parameters from parameter store based on path and encryption key
    private Map<String, String> getParametersByPath(String path, boolean encryption) {
        try {
            GetParametersByPathRequest getParametersByPathRequest = new GetParametersByPathRequest().withPath(path)
                    .withRecursive(true).withWithDecryption(encryption);

            String token = null;
            Map<String, String> params = new HashMap<>();

            do {
                getParametersByPathRequest.setNextToken(token);
                GetParametersByPathResult parameterResult = awsSimpleSystemsManagementClient()
                        .getParametersByPath(getParametersByPathRequest);
                token = parameterResult.getNextToken();
                params.putAll(addParamsToMap(parameterResult.getParameters()));
            } while (token != null);

            return params;
        } catch (Exception e) {
            throw new RuntimeException("FAILED TO RETRIEVE PARAMS FROM SYSTEM MANAGER PARAMETER STORE ",
                    e);
        }
    }

     //Get particular parameter from store
    private String getParameterByName(String name, boolean encryption) {
        logger.debug("Get parameter: {} from the AWS ParameterStore", name);
        try {
            GetParameterRequest parameterRequest = new GetParameterRequest().withName(name)
                    .withWithDecryption(encryption);
            GetParameterResult parameterResult = awsSimpleSystemsManagementClient()
                    .getParameter(parameterRequest);

            if (parameterResult != null && parameterResult.getParameter() != null) {
                return parameterResult.getParameter().getValue();
            }
            return null;
        } catch (Exception e) {
            logger.error( "FAILED TO RETRIEVE " + name + " FROM SYSTEMMANAGER PARAMETER STORE ", e);
            throw new RuntimeException(

                    "FAILED TO RETRIEVE " + name + " FROM SYSTEMMANAGER PARAMETER STORE ", e);
        }

    }
    //add parameters to the map
    private static Map<String, String> addParamsToMap(List<Parameter> parameters) {
        return parameters.stream().map(param -> {
            int envSeparator = param.getName().indexOf(SLASH, 1);
            return new ImmutablePair<>(param.getName().substring(envSeparator + 1), param.getValue());
        }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }


     //Return a map of all parameters from parameter store
    public Map<String, String> getParameters() {
        return parameters;
    }


     //Return a specific parameter based on cache or from server
    public String getParameter(String namespace, String paramName, boolean doHardLookup) {
        if (doHardLookup) {
            return getParameterByName(SLASH + namespace + SLASH + paramName, true);
        } else {
            return getParameter(namespace, paramName);
        }
    }


     //Return a specific parameter
    public String getParameter(String namespace, String paramName) {
        if (null == parameters || parameters.isEmpty()) {
            /* Hard Refresh */
            parameters = getAllParamsForNamespace(namespace);
        }

        String fetchedValue = parameters.get(paramName);

        /* Safety Net - Re-fetch param as another try */
        if (StringUtils.isEmpty(fetchedValue)) {
            fetchedValue = getParameter(namespace, paramName, true);
        }

        return fetchedValue;
    }

    private AWSSimpleSystemsManagement awsSimpleSystemsManagementClient() {
        if (null == awsSimpleSystemsManagementClient) {

           try{

               awsSimpleSystemsManagementClient = AWSSimpleSystemsManagementClientBuilder.defaultClient();

           } catch (Exception e) {
                throw new RuntimeException("FAILED TO CONNECT TO SYSTEMMANAGER PARAMETER STORE ", e);
            }
        }

        return awsSimpleSystemsManagementClient;
    }

}
