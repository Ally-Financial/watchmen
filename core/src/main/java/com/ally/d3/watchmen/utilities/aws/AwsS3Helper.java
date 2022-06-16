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
import com.ally.d3.watchmen.utilities.RequestHelper;
import com.ally.d3.watchmen.utilities.ResponseHelper;
import com.ally.d3.watchmen.utilities.dataDriven.ReadFile;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;


public class AwsS3Helper {

    @Autowired
    EncryptionHelper EncryptionHelper;

    @Autowired
    @Qualifier("WatchmenRestTemplate")
    RestTemplate restTemplate;

    @Autowired
    ReadFile readFile;


    @Value("${useProxy:true}")
    private Boolean useProxy;

    @Value("${host}")
    private String host;

    @Value("${port}")
    private Integer port;

    private static final Logger logger = LoggerFactory.getLogger(AwsS3Helper.class);

    public void encryptFile(String inputFilename, String outputFilename, String publicKeyFileName, Boolean isArmor) {

        logger.debug("Encrypt file "+ inputFilename);

        try {
            EncryptionHelper.pgpEncrypt(inputFilename, outputFilename, publicKeyFileName, isArmor);

        } catch (Throwable e) {
            logger.error("NOT able to encrypt file "+e);
            throw new RuntimeException("NOT able to encrypt file "+e);

        }
    }

    // Read publicKey using RestTemplate

    public void readPublicKeyAsFileFromUrl(String url, String fileName) {

        logger.debug("Read publicKey from the Url and save it as a file " + fileName);

        try {
            ResponseEntity<byte[]> byteResponse = restTemplate.getForEntity(new URI(url), byte[].class);
            logger.debug("Got response from public key Url: "+byteResponse.getStatusCode());
            if (byteResponse.getStatusCode().is2xxSuccessful()) {
                logger.debug("Writing file: {}", fileName);
                FileUtils.writeByteArrayToFile(new File(fileName), byteResponse.getBody());
            }
            else {
                logger.debug("Response from Public Key Url not successful");
                throw new RuntimeException("Response from Public Key Url not successful");
            }

        } catch (Throwable e) {
            logger.error("Exception downloading public key: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }


    // Send file to S3 using RestTemplate

    public void  uploadFileToS3 (String filePath, String preSignedURL, Map<String, String> headersMap) throws URISyntaxException {

        logger.debug("Send file "+filePath +" to the pre-signed URL "+preSignedURL);


        byte[] fileContent= readFile.readFileAsByteArray(filePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setAll(headersMap);
        HttpEntity   requestEntity = new HttpEntity<>(fileContent,headers);
        restTemplate.put(new URI(preSignedURL), requestEntity);
    }









}






