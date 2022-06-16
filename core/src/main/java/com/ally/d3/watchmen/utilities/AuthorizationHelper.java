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
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;


public class AuthorizationHelper {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationHelper.class);


    @Autowired
    RequestHelper requestHelper;



    public PreemptiveBasicAuthScheme grantAccess(String user, String password ) {
        PreemptiveBasicAuthScheme auth = new PreemptiveBasicAuthScheme();
        auth.setUserName(user);
        auth.setPassword(password);
        return auth;
    }


    public void setAuthorizationHeaderWithBasicAuth(String user,String password) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization",  grantAccess(user, password).generateAuthToken());

        logger.debug("Set Header: Authorization = *******");
        requestHelper.addHeadersToRequest(headers);
    }


    public void setAuthorizationHeaderWithBearerToken(String access_token) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + access_token);

        logger.debug("Set Header: Authorization ="+"Bearer *******");
        requestHelper.addHeadersToRequest( headers);
    }

}
