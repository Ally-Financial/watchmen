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

import com.ally.d3.watchmen.utilities.dataDriven.ReadFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WriteFile {

    private static final Logger logger = LoggerFactory.getLogger(WriteFile.class);


    public void bytesToFile(String fileName, byte[] fileBytes) {

        logger.debug("Write bytes as a file "+fileName);

        try {
            FileOutputStream encryptedFile = new FileOutputStream(fileName);
            encryptedFile.write(fileBytes);
            encryptedFile.close();
        } catch (IOException e) {
            logger.error("Not able to write file " + fileName+ " "+e);
            throw new RuntimeException("Not able to write file " + fileName + e);
        }
    }



    public void stringToFile(String fileName, String content) {

        logger.debug("Write string as a file "+fileName);

        File output = new File(fileName);
        FileWriter writer;

       try {
           writer = new FileWriter(output);
       }

       catch (IOException e){
           logger.error("Not able to create file to write to it " + fileName+ " "+e);
           throw new RuntimeException("Not able to create file to write to it " + fileName + e);
       }

       try {
           writer.write(content);
           writer.flush();
           writer.close();

       }
       catch (IOException e){
           logger.error("Not able to write to the file " + fileName+ " "+e);
           throw new RuntimeException("Not able to write file " + fileName + e);
       }

    }




}
