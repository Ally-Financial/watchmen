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

import java.io.BufferedReader;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.IOException;

import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadFile {

    private static final Logger logger = LoggerFactory.getLogger(ReadFile.class);


    //Method to read csv file (file contains few columns of data separated by coma) from ClassPath (Resources)
    // First line - header

    public  List<String[]> readCSVfiletoList(String csvFile) {
        logger.debug("Read file "+csvFile);

        String[] cols = null;
        List<String[]> list = new ArrayList<>();

        Path p = getFilePath(csvFile).normalize();

            try {
                logger.debug("Parse file");
                BufferedReader br = java.nio.file.Files.newBufferedReader(p);
                //skip first line
                logger.debug("Skip first line");
                br.readLine();
                //Start from second row
                String line = br.readLine();

                logger.debug("Use coma as a separator");
                while (line != null) {
                    //--use coma as separator
                    //--cols = line.split(",");
                    //--if coma , inside " " that means it is part of the string and not separation symbol
                    cols = line.split(",");
                    list.add(cols);
                    line = br.readLine();

                }
            } catch (IOException e) {
                logger.error("Not able to parse file."+e);
                throw new RuntimeException("Not able to parse file " + csvFile );

            }
            logger.debug("Return list of lines[]");
            return list;
    }

    //used by CAPI team
    public  List<String[]> readCSVfiletoListUsingDelimiter(String csvFile, String delimiter) {
        logger.debug("Read file "+csvFile+ " starting from the first row");

        String[] lineAsArray = null;
        List<String[]> listOfLines = new ArrayList<>();

        Path p = getFilePath(csvFile).normalize();

        try {
            logger.debug("Parse file");
            BufferedReader br = java.nio.file.Files.newBufferedReader(p);
            String currentLine = br.readLine();

            logger.debug("Use delimiter as a separator");
            while (currentLine != null) {
                //--use coma as separator
                //--cols = line.split(",");
                //--if coma , inside " " that means it is part of the string and not separation symbol
                lineAsArray = currentLine.split(delimiter);
                listOfLines.add(lineAsArray);
                currentLine = br.readLine();

            }
        } catch (IOException e) {
            logger.error("Not able to parse file."+e);
            throw new RuntimeException("Not able to parse file " + csvFile );

        }
        logger.debug("Return list of lines[]");
        return listOfLines;
    }

    public List<String> readFirstColumnCSVtoList(String csvFile) {
        logger.debug("Read file "+csvFile);

        List<String[]> allColumns = readCSVfiletoList(csvFile);

        List<String> firstColumn = new ArrayList<String>();
        for (int i = 0; i < allColumns.size(); i++) {
            String[] row = allColumns.get(i);
            firstColumn.add(row[0]);
        }
        logger.debug("Return list of lines with first column only");
        return firstColumn;


    }

    public Map<String, String> readFirstTwoColumnsCSVtoMap(String csvFile) {
        logger.debug("Read file "+csvFile);

        List<String[]> allColumns = readCSVfiletoList(csvFile);

        Map<String, String> firstTwoColumns = new HashMap<String, String>();

        for (int i = 0; i < allColumns.size(); i++) {
            String[] row = allColumns.get(i);
            firstTwoColumns.put(row[0], row[1]);
        }
        logger.debug("Return list of lines with first and second columns only");
        return firstTwoColumns;


    }
//used by CAPI team
    public Map<String, String> readFirstTwoLinesCSVtoMapUsingDelimiter(String csvFile, String delimiter) {
        logger.debug("Read file "+csvFile);

        List<String[]> allLines = readCSVfiletoListUsingDelimiter(csvFile,delimiter);

        //LinkedHashMap will return the elements in the order they were inserted into the map
        Map<String, String> firstTwoLinesAsMap = new LinkedHashMap<>();

            String[] keys = allLines.get(0);
            String[] values = allLines.get(1);

            if (keys.length==values.length) {

                for (int i = 0; i < allLines.get(0).length; i++) {
                    firstTwoLinesAsMap.put(keys[i], values[i]);
                }
                logger.debug("Return Map for first two lines");
                return firstTwoLinesAsMap;
            }
            else
                logger.error("Error to build map from csv file  - number of elements is different on line one and line two"  );
                throw new RuntimeException("Error to build map from csv file  - number of elements is different on line one and line two" );

    }

    public String readFileAsString(String file) {
        return  new String(readFileAsByteArray(file));
        }



    public byte[] readFileAsByteArray(String file) {
        logger.debug("Read file as bytes "+ file);
        byte[] fileBytes = null;

        Path p = getFilePath(file).normalize();

        logger.debug("File exists, reading file as ByteArray");
                try {
                    fileBytes = Files.readAllBytes(p);
                } catch (Throwable e2) {
                    logger.debug("Not able to read file");
                    throw new RuntimeException("Not able to read file: " + file);
                }
        logger.debug("Return file content as bytes");
        return fileBytes;
    }


    public Path getFilePath(String fileName){

        logger.debug("Get the file path: " + fileName);
        Path p;

        try {
            logger.debug("Look in the Resources");
            p = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
        } catch (Throwable e1) {
            logger.debug("File does not exist on the Resources. Look in the working directory.. ");
            try {
                logger.debug("Look in the working directory");
                p = Paths.get(fileName).normalize();
            } catch (Throwable e2) {
                logger.debug("File does not exist on the Resources");
                throw new RuntimeException("File not found: " + fileName);
            }
        }
        //check if file exists

        if (Files.exists(p)) return p;
        else {
            logger.error("File not found: "+fileName);
            throw new RuntimeException("File not found: "+fileName);
    }


    }
}









