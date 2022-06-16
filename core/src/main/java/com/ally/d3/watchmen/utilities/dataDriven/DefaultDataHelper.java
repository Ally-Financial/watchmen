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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class DefaultDataHelper {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDataHelper.class);

    private  Random random = new Random();

    public  String randomSSN() {
        logger.debug("Execute method: randomSSN()");
        int i = (int) (100000000 + random.nextFloat() * 900000000);
        String regex = "^(?!000|666)[0-8][0-9]{2}(?!00)[0-9]{2}(?!0000)[0-9]{4}$";

        while (!Pattern.matches(regex, String.valueOf(i))) {
            i = (int) (100000000 + random.nextFloat() * 900000000);
        }

        return i + "";
    }

    public  String formatSsn(String ssn) {
        if (ssn.contains("-")) {
            logger.info("SSN generated as "+ ssn);
            return ssn;
        }
        String s=String.format("%s-%s-%s", ssn.substring(0, 3), ssn.substring(3, 5), ssn.substring(5, 9));
        logger.info("SSN generated as "+ s);
        return s;
    }

    public  String randomFormattedSSN() {

        return formatSsn(randomSSN());
    }



    public static String yearsAgo(String... args ) {
        logger.debug("Execute method: yearsAgo");
        int years = Integer.parseInt(args[0]);
        logger.debug(years + " years ago");
        String pattern;
        if (args.length>1) pattern = args[1];
        else pattern = "yyyy-MM-dd";
        logger.debug("Apply data format "+ pattern);
        String s=LocalDateTime.now().minusYears(years).format(DateTimeFormatter.ofPattern(pattern));
        logger.info("yearsAgo generated as: "+ s);
        return s;
    }

    public static String yearsAhead(String... args ) {
        logger.debug("Execute method: yearsAhead");
        int years = Integer.parseInt(args[0]);
        logger.debug(years + " years ahead");
        String pattern;
        if (args.length>1) pattern = args[1];
        else pattern = "yyyy-MM-dd";
        logger.debug("Apply data format "+ pattern);
        String s=LocalDateTime.now().plusYears(years).format(DateTimeFormatter.ofPattern(pattern));
        logger.info("yearsAhead generated as: "+ s);
        return s;
    }

    public static String daysAgo(String... args ) {
        logger.debug("Execute method: daysAgo");
        int days = Integer.parseInt(args[0]);
        String pattern;
        if (args.length>1) pattern = args[1];
        else pattern = "yyy-MM-dd";
        logger.debug("Apply data format "+ pattern);
        String s=LocalDateTime.now().minusDays(days).format(DateTimeFormatter.ofPattern(pattern));
        logger.info("daysAgo generated as: "+ s);
        return s;
    }

    public static String daysAhead(String... args ) {
        logger.debug("Execute method: daysAhead");
        int days = Integer.parseInt(args[0]);
        logger.debug(days + " days ahead");
        String pattern;
        if (args.length>1) pattern = args[1];
        else pattern = "yyy-MM-dd";
        logger.debug("Apply data format "+ pattern);
        String s=LocalDateTime.now().plusDays(days).format(DateTimeFormatter.ofPattern(pattern));
        logger.info("daysAhead generated as: "+ s);
        return s;
    }

    public static String randomAlpha(Integer length ) {
        logger.debug("Execute method: randomAlpha");
        logger.debug("Generate random Alpha string of length = "+length);
        String generatedString = RandomStringUtils.randomAlphabetic(length).toUpperCase();
        logger.debug("Generated String is"+ generatedString);
        logger.info("RandomAlpha String generated: "+ generatedString);
        return generatedString;
    }

    public static String randomNumber(Integer length ) {
        logger.debug("Execute method: randomNumber");
        logger.debug("Generate random Number of length = "+length);
        String generatedString = RandomStringUtils.randomNumeric(length);
        logger.debug("Generated Number is"+ generatedString);
        logger.info("RandomNumber generated as : "+ generatedString);
        return generatedString;
    }

    public static String randomNumberInRange(Integer min, Integer max ) {
        logger.debug("Execute method: numberInRange");
        logger.debug("Execute method: randomNumber less than "+max+" more than "+min);

        if (min>max){
            logger.error("RandomNumberInRange: Min value must be less than Max value");
            throw new RuntimeException("randomNumberInRange generation: Min value must be less than Max value");
        }
        Random random = new Random();
        String generatedString = String.valueOf(random.nextInt(max - min) + min);
        logger.debug("Generated randomNumberInRange is"+ generatedString);
        logger.info("RandomNumberInRange generated as : "+ generatedString);
        return generatedString;
    }


    public static String randomEmail( ) {
        logger.debug("Execute method: randomEmail");
        String generatedEmail = RandomStringUtils.randomAlphabetic(20)+"@TestAlly.com";
        logger.debug("Generated email is"+ generatedEmail);
        logger.info("RandomEmail generated as : "+ generatedEmail);
        return generatedEmail;
    }

    public static String randomString(String...chars ) {
        logger.debug("Execute method: randomString");
        int length = Integer.parseInt(chars[0]);
        String pattern;
        if (chars.length>1) pattern = chars[1];
        else pattern = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        String generatedString = RandomStringUtils.random(length,pattern);
        logger.debug("Generated String is"+ generatedString);
        logger.info("RandomString generated as : "+ generatedString);
        return generatedString;
    }

    public static String getTimeMs(){

        Calendar calendar = Calendar.getInstance();
        String s=String.valueOf(calendar.getTimeInMillis());
        logger.debug("getTimeMs generated: "+ s);
        logger.info("getTimeMs generated as : "+ s);

        return s;

    }

    public static String getTimeSec(){

        Calendar calendar = Calendar.getInstance();
        String s=String.valueOf(calendar.getTimeInMillis()/1000);
        logger.debug("getTimeSec generated: "+ s);
        logger.info("getTimeSec generated as : "+ s);

        return s;
    }

    public static String getTimeMsMinutesAhead(Integer mins){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, mins);
        String ms=String.valueOf(calendar.getTimeInMillis());
        logger.debug("getTimeMsMinutesAhead generated: "+ ms);
        logger.info("getTimeMsMinutesAhead generated as : "+ ms);
        return ms;
    }

    public static String getTimeSecMinutesAhead(Integer mins){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, mins);
        long milliseconds = calendar.getTimeInMillis();
        long seconds=milliseconds/ 1000;
        String s=String.valueOf(seconds);
        logger.debug("getTimeSecMinutesAhead generated: "+ s);
        logger.info("getTimeSecMinutesAhead generated as : "+ s);
        return s;
    }



    public static String getRandomUUID(){

        String s=UUID.randomUUID().toString();
        logger.debug("getRandomUUID generated: "+ s);
        logger.info("getRandomUUID generated as : "+ s);
        return s;


    }



}
