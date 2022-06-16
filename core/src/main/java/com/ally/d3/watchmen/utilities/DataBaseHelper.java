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

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DataBaseHelper {

    @Autowired
    @Qualifier("WatchmenJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("WatchmenNamedParameterJdbcTemplate")
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    @Qualifier("WatchmenDataSource")
    DataSource dataSource;

    @Value("${max.attempts.query.db:5}")
    private Integer max_attempt_query_db;


    private static final Logger logger = LoggerFactory.getLogger(DataBaseHelper.class);


    @Retryable(maxAttemptsExpression = "${max.attempts.query.db}",value=RuntimeException.class,backoff = @Backoff(delay = 3000))
    public void establishConnection () {

        Boolean connected = false;
        logger.debug("Check established connection to the Data Base successful");
        try {
            jdbcTemplate.execute("SELECT 1 FROM DUAL");
            logger.debug("Connection to the Data Base SUCCESSFUL");
            connected= true;
        } catch (Exception e) {
            logger.error("Connection to the Data Base FAILED "+e);
            throw new RuntimeException("Connection to Data Base FAILED "+e);
        }
        Assert.assertTrue("Connection to Data Base FAIL", connected);
    }

    @Retryable(maxAttemptsExpression = "${max.attempts.query.db}",value=RuntimeException.class,backoff = @Backoff(delay = 3000))
    public String queryForString (String sql) {

        String sqlResponse;
        logger.debug("execute SQL request "+sql);
        try {

            sqlResponse = jdbcTemplate.queryForObject(sql, String.class);

        } catch (Exception e) {
            logger.error("execute SQL request FAIL");
            throw new RuntimeException("Execute SQL request FAILED "+e.getMessage());
        }
        return sqlResponse;
    }

    @Retryable(maxAttemptsExpression = "${max.attempts.query.db}",value=RuntimeException.class,backoff = @Backoff(delay = 3000))
    public List<String> queryForListOfStrings (String sql) {

        List<String> sqlResponse;
        logger.debug("execute SQL request "+sql);
        try {

            sqlResponse = jdbcTemplate.queryForList(sql, String.class);

        } catch (Exception e) {
            logger.error("execute SQL request FAIL");
            throw new RuntimeException("Execute SQL request FAILED "+e.getMessage());
        }
        return sqlResponse;
    }

    @Retryable(maxAttemptsExpression = "${max.attempts.query.db}",value=RuntimeException.class,backoff = @Backoff(delay = 3000))
    public Integer queryForInteger (String sql) {

        Integer sqlResponse;
        logger.debug("execute SQL request "+sql);
        try {

            sqlResponse = jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (Exception e) {

            logger.error("Execute SQL request FAILED");
            throw new RuntimeException("Execute SQL request FAILED "+e.getMessage());
        }
        return sqlResponse;
    }

    @Retryable(maxAttemptsExpression = "${max.attempts.query.db}",value=RuntimeException.class,backoff = @Backoff(delay = 3000))
    public List<Integer> queryForListOfIntegers (String sql) {

        List<Integer> sqlResponse;
        logger.debug("execute SQL request "+sql);
        try {

            sqlResponse = jdbcTemplate.queryForList(sql, Integer.class);
        } catch (Exception e) {

            logger.error("Execute SQL request FAILED");
            throw new RuntimeException("Execute SQL request FAILED "+e.getMessage());
        }
        return sqlResponse;
    }

    @Retryable(maxAttemptsExpression = "${max.attempts.query.db}",value=RuntimeException.class,backoff = @Backoff(delay = 3000))
    public String queryForStringWithParam (String sql, Map<String,String> params) {

        String sqlResponse;
        logger.debug("execute SQL request with parameters "+sql);
        try {

            sqlResponse = namedParameterJdbcTemplate.queryForObject(sql,params,String.class);
        } catch (Exception e) {

            logger.error("execute SQL request with parameters FAIL");
            throw new RuntimeException("Execute SQL request with parameters FAILED "+e.getMessage());
        }
        return sqlResponse;
    }

    @Retryable(maxAttemptsExpression = "${max.attempts.query.db}",value=RuntimeException.class,backoff = @Backoff(delay = 3000))
    public List<String> queryForListOfStringsWithParam (String sql, Map<String,String> params) {

        List<String> sqlResponse;
        logger.debug("execute SQL request with parameters "+sql);
        try {

            sqlResponse = namedParameterJdbcTemplate.queryForList(sql,params,String.class);
        } catch (Exception e) {

            logger.error("execute SQL request with parameters FAIL");
            throw new RuntimeException("Execute SQL request with parameters FAILED "+e.getMessage());
        }
        return sqlResponse;
    }

    @Retryable(maxAttemptsExpression = "${max.attempts.query.db}",value=RuntimeException.class,backoff = @Backoff(delay = 3000))
    public Integer queryForIntegerWithParam (String sql, Map<String,String> params) {

        Integer sqlResponse;
        logger.debug("execute SQL request with parameters "+sql);
        try {

            sqlResponse = namedParameterJdbcTemplate.queryForObject(sql,params,Integer.class);
        } catch (Exception e) {

            logger.error("Execute SQL request with parameters FAILED");
            throw new RuntimeException("Execute SQL request with parameters FAILED "+e.getMessage());
        }
        return sqlResponse;
    }

    @Retryable(maxAttemptsExpression = "${max.attempts.query.db}",value=RuntimeException.class,backoff = @Backoff(delay = 3000))
    public List<Integer> queryForListOfIntegersWithParam (String sql, Map<String,String> params) {

        List<Integer> sqlResponse;
        logger.debug("execute SQL request with parameters "+sql);
        try {

            sqlResponse = namedParameterJdbcTemplate.queryForList(sql,params,Integer.class);
        } catch (Exception e) {

            logger.error("Execute SQL request with parameters FAILED");
            throw new RuntimeException("Execute SQL request with parameters FAILED "+e.getMessage());
        }
        return sqlResponse;
    }





}
