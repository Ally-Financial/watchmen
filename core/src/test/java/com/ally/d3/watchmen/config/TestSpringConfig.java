package com.ally.d3.watchmen.config;

import com.ally.d3.watchmen.steps.TestScope;
import com.ally.d3.watchmen.utilities.*;
import com.ally.d3.watchmen.utilities.dataDriven.JsonHelper;
import com.ally.d3.watchmen.utilities.dataDriven.PlaceholderResolve;
import com.ally.d3.watchmen.utilities.dataDriven.ReadFile;
import com.ally.d3.watchmen.utilities.DataBaseHelper;
import oracle.jdbc.datasource.OracleDataSource;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;


@PropertySource("config.properties")
public class TestSpringConfig {

    @Autowired
    Environment env;

    public TestSpringConfig() {
        System.out.println("SpringConfig instantiated");
    }

    @Bean
    public RestAssuredHelper restAssureMagic(){
        return new RestAssuredHelper();
    }

    @Bean
    public TestScope testScope(){
        return new TestScope();
    }


    @Bean
    public ReadFile readFile () {return new ReadFile();}

    @Bean
    public AuthorizationHelper authorizationHelper() {return new AuthorizationHelper();}

    @Bean
    public ResponseHelper responseHelper () {return  new ResponseHelper();}

    @Bean
    public RequestHelper requestHelper () {return  new RequestHelper();}

    @Bean
    public JsonHelper jsonHelper () {return new JsonHelper();}

    @Bean
    public DataBaseHelper dataBaseHelper () {return new DataBaseHelper();}

    @Bean
    public PlaceholderResolve placeholderResolve () {return new PlaceholderResolve();}

    @Bean
    public DataSource jdbcOracle() throws SQLException {
        OracleDataSource dataSource = new OracleConnectionPoolDataSource();
        dataSource.setUser(env.getProperty("oracle.datasource.username"));
        dataSource.setPassword(env.getProperty("oracle.datasource.password"));
        dataSource.setURL(env.getProperty("oracle.datasource.url"));
        return dataSource;

    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource ds){
        return new JdbcTemplate(ds);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate
            (DataSource ds) {
        return new NamedParameterJdbcTemplate(ds);
    }





}
