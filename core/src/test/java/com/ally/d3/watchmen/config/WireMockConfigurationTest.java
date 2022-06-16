package com.ally.d3.watchmen.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class WireMockConfigurationTest {

   private WireMockServer wireMockServer;

   //to make sure every test method gets its own WireMockServer object.
   @BeforeEach
    void configureSystemUnderTest() {
        //Create and configure the WireMockServer
       //creates a WireMock server that listens to all local IPv4 addresses (0.0.0.0).
       //configure WireMock to find a free port before it starts the WireMock server
       this.wireMockServer = new WireMockServer(options()
               .dynamicPort()
       );
       this.wireMockServer.start();
       configureFor(this.wireMockServer.port());
   }

    //stop WireMock server after a test method has been run
    @AfterEach
    void stopWireMockServer() {
        this.wireMockServer.stop();
    }
}