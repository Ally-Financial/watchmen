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

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestAssuredRequestFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RestAssuredRequestFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec);

        logger.trace("\n ----------------------- R E Q U E S T ------------------------------------------------------------------------------"
                        +" \n"
                        +" \n--REQUEST METHOD: "+requestSpec.getMethod() + " \n--REQUEST URI: " + requestSpec.getURI() + " \n--REQUEST PROXY: " + requestSpec.getProxySpecification()
                        +" \n--REQUEST PARAMS: "+" \n"+requestSpec.getRequestParams()+" \n--REQUEST QUERY PARAMS: "+" \n"+requestSpec.getQueryParams()+" \n--REQUEST FORM PARAMS: "+" \n"+requestSpec.getFormParams()
                        +" \n--REQUEST PATH PARAMS: "+" \n"+requestSpec.getPathParams()+" \n--REQUEST HEADERS: "+" \n"+requestSpec.getHeaders()+" \n--REQUEST COOKIES: "+requestSpec.getCookies()
                        +" \n--REQUEST MULTIPARTS: "+" \n"+requestSpec.getMultiPartParams()+" \n--REQUEST BODY: "+requestSpec.getBody()
                        +" \n"
                        +" \n ----------------------- R E S P O N S E -----------------------------------------------------------------------------"
                        +" \n"
                        +" \n--RESPONSE STATUS CODE: "+response.getStatusCode()+ " " + response.getStatusLine()
                        +" \n--RESPONSE HEADERS: "+" \n"+response.getHeaders()+" \n--RESPONSE COOKIES: "+" \n"+response.getDetailedCookies()
                        +" \n--RESPONSE BODY: "+" \n"+response.getBody().asPrettyString()
                        +" \n"
                        +" \n ------------------ E N D  O F  R E S P O N S E ---------------------------------------------------------------------");

        return response;
    }
}