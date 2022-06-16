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

package com.ally.d3.watchmen.config;

import com.ally.d3.watchmen.steps.AwsStepsDefinition;
import com.ally.d3.watchmen.utilities.*;
import com.ally.d3.watchmen.utilities.aws.*;
import com.ally.d3.watchmen.utilities.dataDriven.*;
import com.ally.d3.watchmen.utilities.DataBaseHelper;
import oracle.jdbc.datasource.OracleDataSource;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import org.mariadb.jdbc.MariaDbDataSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import com.ally.d3.watchmen.steps.TestScope;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;
import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

@EnableRetry
@PropertySource("config.properties")
public class SpringConfig {


    @Autowired
    Environment env;

    public SpringConfig() {
        System.out.println("WATCHMEN started");
    }


    @Bean
    public RestAssuredHelper restAssuredHelper() {
        return new RestAssuredHelper();
    }

    @Bean
    public RestAssuredRequestFilter enableRestAssuredLogs() {
        return new RestAssuredRequestFilter();
    }

    @Bean
    public TestScope testScope() {
        return new TestScope();
    }


    @Bean
    public ReadFile readFile() {
        return new ReadFile();
    }


    @Bean
    public WriteFile writeFile() {
        return new WriteFile();
    }

    @Bean
    public AuthorizationHelper authorizationHelper() {
        return new AuthorizationHelper();
    }

    @Bean
    public ResponseHelper responseHelper() {
        return new ResponseHelper();
    }

    @Bean
    public RequestHelper requestHelper() {
        return new RequestHelper();
    }

    @Bean
    public JsonHelper jsonHelper() {
        return new JsonHelper();
    }


    @Bean
    public PlaceholderResolve placeholderResolve() {
        return new PlaceholderResolve();
    }


    @Bean
    public DataBaseHelper dataBaseHelper() {
        return new DataBaseHelper();
    }


    @Bean(name = "WatchmenDataSource")
    @Profile("OracleDB")
    public DataSource jdbcOracle() throws SQLException {
        OracleDataSource dataSource = new OracleConnectionPoolDataSource();
        dataSource.setUser(env.getProperty("oracle.datasource.username"));
        dataSource.setPassword(env.getProperty("oracle.datasource.password"));
        dataSource.setURL(env.getProperty("oracle.datasource.url"));
        System.out.println("Oracle DB instantiated");
        return dataSource;

    }

    @Bean(name = "WatchmenDataSource")
    @Profile("AuroraDB")
    public DataSource jdbcMaria(AwsParameterStoreHelper awsParameterStoreHelper) throws SQLException {
        MariaDbDataSource dataSource = new MariaDbDataSource();
        dataSource.setUser(env.getProperty("aurora.datasource.username"));
        if (env.getProperty("aurora.datasource.password") == null) {
            String pwd = awsParameterStoreHelper.getParameter(env.getProperty("aurora.namespace"), env.getProperty("aurora.paramname"), true);
            dataSource.setPassword(pwd);
        } else
            dataSource.setPassword(env.getProperty("aurora.datasource.password"));
        dataSource.setUrl(env.getProperty("aurora.datasource.url"));
        System.out.println("Aurora DB instantiated");
        return dataSource;
    }


    @Bean(name = "WatchmenDataSource")
    //if no DB spring.profile provided
    @ConditionalOnMissingBean
    public DataSource jdbcDefault() throws SQLException {

        return new AbstractDataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return null;
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return null;
            }
        };
    }


    @Bean(name = "WatchmenJdbcTemplate")
    //handles the creation and release of db resources, executes SQL queries
    @Qualifier("jdbcTemplate")
    public JdbcTemplate jdbcTemplate(DataSource ds) {

        return new JdbcTemplate(ds);

    }


    @Bean(name = "WatchmenNamedParameterJdbcTemplate")
    //to provide named parameters on SQL query
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate
            (DataSource ds) {
        return new NamedParameterJdbcTemplate(ds);
    }


    @Bean
    public DefaultDataHelper defaultDataHelper() {
        return new DefaultDataHelper();
    }

    @Bean
    public XMLHelper xmlHelper() {
        return new XMLHelper();
    }

    @Bean
    public AwsDynamoDBHelper awsDynamoDBHelper() {
        return new AwsDynamoDBHelper();
    }

    @Bean
    public EncryptionHelper pgpUtil() {
        return new EncryptionHelper();
    }


    @Bean
    public AwsS3Helper awsS3Helper() {
        return new AwsS3Helper();
    }

    @Bean
    @Profile("AuroraDB")
    public AwsParameterStoreHelper awsParameterStoreHelper() {
        return new AwsParameterStoreHelper();
    }

    // @Bean
    // public AwsCredentialsHelper awsCredentialsHelper() {
    //     return new AwsCredentialsHelper();
    // }


    @Bean(name = "WatchmenRestTemplate")
    public RestTemplate restTemplate() {
        RestTemplate restTemplate;
        if (env.getProperty("useProxy") == null || env.getProperty("useProxy").equalsIgnoreCase("true")) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(env.getProperty("host"), Integer.parseInt(env.getProperty("port"))));
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setProxy(proxy);

            restTemplate = new RestTemplate(requestFactory);

        } else
            restTemplate = new RestTemplate();

        restTemplate.setInterceptors(Arrays.asList(new CustomHttpRequestInterceptor()));
        return restTemplate;
    }

    @Bean
    @Profile("Chrome")
    public SeleniumHelper seleniumHelper() {
        return new SeleniumHelper();
    }

    @Bean
    @Profile("Chrome")
    public WebDriver driver() {

        System.setProperty("webdriver.chrome.driver", env.getProperty("driver.path"));

            WebDriver driver = new ChromeDriver();
            System.out.println("Chrome driver instantiated");
            return driver;
        }

}


