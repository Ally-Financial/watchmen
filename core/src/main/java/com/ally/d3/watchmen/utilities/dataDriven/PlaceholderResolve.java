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

import com.ally.d3.watchmen.steps.TestScope;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderResolve {

    //Environment - Interface representing the environment in which the current application is running
    //We need Environment to get access to properties files

    @Autowired
    private Environment environment;

    @Autowired
    TestScope testScope;

    @Autowired
    DefaultDataHelper defaultDataHelper;



    private static final Logger logger = LoggerFactory.getLogger(PlaceholderResolve.class);

    //replace {{prop_name}} with properties from a properties file

    //Map

    public Map<String,String> replacePropertiesOnMap (Map<String, String> someMap) {

        logger.debug("Take Map and replace all {{prop_name}} with corresponding properties from a properties file");

        Map<String, String> newMap = new LinkedHashMap<>();

       //iterate on Map and check if there are variable "{{name}}"
        //if such variable found - replace it with corresponding properties from property file
        for (String name : someMap.keySet()) {
            String prop_name;
            if (someMap.get(name).startsWith("{{") & (someMap.get(name).endsWith("}}"))) {
                prop_name = (someMap.get(name).substring(2, someMap.get(name).length() - 2));
                newMap.put(name, environment.getProperty(prop_name, "NOT_FOUND_ON_PROPERTIES_FILE"));
                logger.debug("resolved value is "+environment.getProperty(prop_name, "NOT_FOUND_ON_PROPERTIES_FILE"));

            }
         else if (someMap.get(name).startsWith("\"{{") & (someMap.get(name).endsWith("}}\""))) {
                prop_name =  (someMap.get(name).substring(3, someMap.get(name).length() - 3));
                newMap.put(name, "\"" + environment.getProperty(prop_name, prop_name)+"\"" );
                logger.debug("resolved value is "+environment.getProperty(prop_name, prop_name));
        }

        else newMap.put(name, someMap.get(name));


        }


        return newMap;
    }

    //replace <prop_name> with properties from a Container

    //Map

    public Map<String,String> replaceContainerValueOnMap (Map<String, String> someMap) {

        logger.debug("Take Map and replace all <prop_name> with corresponding properties from a Scenario Scope");

        Map<String, String> newMap = new LinkedHashMap<>();

        //iterate on Map and check if there are variable "<name>"
        //if such variable found - replace it with corresponding properties from Scenario Scope
        for (String name : someMap.keySet()) {
            if (someMap.get(name).startsWith("<") & (someMap.get(name).endsWith(">"))) {
                String key = (someMap.get(name).substring(1, someMap.get(name).length() - 1));
                newMap.put(name,  testScope.getFromContainer(key));
                logger.debug("resolved value is "+testScope.getFromContainer(key));

            } else if (someMap.get(name).startsWith("\"<") & (someMap.get(name).endsWith(">\""))) {
                String key = (someMap.get(name).substring(2, someMap.get(name).length() - 2));
                newMap.put(name, "\"" + testScope.getFromContainer(key) + "\"");
                logger.debug("resolved value is "+"\"" + testScope.getFromContainer(key) + "\"");
            }

            else newMap.put(name, someMap.get(name));

        }


        return newMap;
    }

    //replace %prop_name% with function

    //Map

    public Map<String,String> replaceFunctionOnMap (Map<String, String> someMap) {

        logger.debug("Take Map and replace all %prop_name% with response from corresponding function");

        Map<String, String> newMap = new LinkedHashMap<>();

        for (String name : someMap.keySet()) {
            if (someMap.get(name).startsWith("%") & (someMap.get(name).endsWith("%"))) {
                String key = (someMap.get(name).substring(1, someMap.get(name).length() - 1));
                logger.debug("replace " + "key: " + name + " with result of function execution ");
                String devider = "%";
                try {
                String resolvedValue = resolvePlaceholderValue(key,devider.charAt(0), null);
                logger.debug("resolved value is "+resolvedValue);
                newMap.put(name,  resolvedValue);
                }
                catch (Throwable e) {
                    logger.debug("Could NOT resolve function "+key);
                    e.printStackTrace();
                    throw new RuntimeException("Could NOT resolve function "+key);
                }

            } else if (someMap.get(name).startsWith("\"%") & (someMap.get(name).endsWith("%\""))) {
                String key = (someMap.get(name).substring(2, someMap.get(name).length() - 2));
                logger.debug("replace " + "key: " + name + " with result of function execution "+ key);
                String devider = "%";
                try {
                    String resolvedValue = resolvePlaceholderValue(key, devider.charAt(0), null);
                    logger.debug("resolved value is "+resolvedValue);
                    newMap.put(name, "\"" + resolvedValue + "\"");

                }
                catch (Throwable e) {
                    logger.debug("Could NOT resolve function "+key);
                    e.printStackTrace();
                    throw new RuntimeException("Could NOT resolve function "+key);
                }

            }

            else newMap.put(name, someMap.get(name));

        }


        return newMap;
    }




    //String
    public String replaceContainerValueString (String someString) {

        logger.debug("Take String and if it looks like <prop_name> - replace it with corresponding properties from a Scenario Scope");

        String newString;
        if (someString.startsWith("<") & (someString.endsWith(">"))) {
            String key = (someString.substring(1, someString.length() - 1));
            newString = testScope.getFromContainer(key);
            logger.debug("resolved value is "+newString);
        }
        else if (someString.startsWith("\"<") & (someString.endsWith(">\""))) {
            String key = (someString.substring(2, someString.length() - 2));
            newString = "\"" +testScope.getFromContainer(key)+"\"";
            logger.debug("resolved value is "+newString);
        }
        else newString = someString;


        return newString;
    }

    //String
    public String replaceFunctionString (String someString) {

        logger.debug("Take String and if it looks like %prop_name% - replace it with response from corresponding function");

        String newString;
        if (someString.startsWith("%") & (someString.endsWith("%"))) {
            String key = (someString.substring(1, someString.length() - 1));
            logger.debug("Replace " + someString + " with response from " + key);
            String devider = "%";
            try {
                newString = resolvePlaceholderValue(key, devider.charAt(0), null);
                logger.debug("Resolved value is "+newString);
            }
            catch (Throwable e) {
                logger.debug("Could NOT resolve function "+key);
                e.printStackTrace();
                throw new RuntimeException("Could NOT resolve function "+key);
            }
        }
        else if (someString.startsWith("\"%") & (someString.endsWith("%\""))) {
            String key = (someString.substring(2, someString.length() - 2));
            logger.debug("Replace " + someString + " with response from " + key);
            String devider = "%";
            try {
                newString = "\"" + resolvePlaceholderValue(key, devider.charAt(0), null)+"\"" ;
                logger.debug("Resolved value is "+newString);
            }
            catch (Throwable e) {
                logger.debug("Could NOT resolve function "+key);
                e.printStackTrace();
                throw new RuntimeException("Could NOT resolve function "+key);
            }
        }
        else newString = someString;

        return newString;
    }



    // replace {{prop_name}} with properties from a properties file
    //String
    public String replacePropertiesString (String someString) {

        logger.debug("Take String and if it looks like {{prop_name}} - replace it with corresponding properties from a properties file");

        String newString;
        String prop_name;
        if (someString.startsWith("{{") & (someString.endsWith("}}"))) {
            prop_name = (someString.substring(2, someString.length() - 2));
            newString = environment.getProperty(prop_name, prop_name);
            logger.debug("Resolved value is "+newString);
        }
        else if (someString.startsWith("\"{{") & (someString.endsWith("}}\""))) {
            prop_name = (someString.substring(3, someString.length() - 3));
            newString = "\"" +environment.getProperty(prop_name, prop_name)+"\"";
            logger.debug("Resolved value is "+newString);
        }
        else newString = someString;


        return newString;
    }


   //Replace placeholder on URL
    public String replacePlaceholdersURL(String fullString) {
        return resolvePlaceholders(fullString, Collections.emptyMap());
    }

   //replace <prop_name> with properties from a Container and {prop_name} with config.properties
    private String resolvePlaceholders(String fullString, Map<String, String> tempScope) {
        StringBuffer resolvedString = new StringBuffer();


        Pattern placeholderPattern = Pattern.compile("(?:[%<]|\\{\\{)([^%}>]+)(?:[%>]|}})");
        Matcher m = placeholderPattern.matcher(fullString);
        while (m.find()) {
            String resolvedValue = resolvePlaceholderValue(m.group(1), m.group().charAt(0), tempScope);
            m.appendReplacement(resolvedString, StringUtils.defaultIfEmpty(resolvedValue, m.group(1)));
        }

        m.appendTail(resolvedString);

        logger.debug("Resolved String is "+resolvedString.toString());

        return resolvedString.toString();
    }

    private String resolvePlaceholderValue(String placeholder, char identifier, Map<String, String> tempScope) {
        String placeholderKey = placeholder.contains(":") ? placeholder.substring(0, placeholder.indexOf(":")) : placeholder;
        String defaultValue = placeholder.substring(placeholder.indexOf(":") + 1);

        switch (identifier) {
            case '{':
                return environment.getProperty(placeholderKey, defaultValue);
            case '<':
                return tempScope.getOrDefault(placeholderKey, testScope.getContainer().getOrDefault(placeholder, defaultValue));
            case '%':
                String[] arguments = new String[0];
                int argStart = placeholder.indexOf("(");
                if (argStart > -1) {
                    arguments = placeholder.substring(argStart + 1, placeholder.length() -  1)
                            .trim()
                            .split("\\s*,\\s*");
                    placeholder = placeholder.substring(0, argStart).trim();
                }
                return functionMapper.getOrDefault(placeholder, args -> defaultValue).apply(arguments);
        }

        throw new IllegalArgumentException("Unknown placeholder identifier '" + identifier + "' with name " + placeholder);
    }


    //function mapper for runtime execution
    private  final Map<String, Function<String[], String>> functionMapper;



    {
        Map<String, Function<String[], String>> tempFunctions = new HashMap<>();
        tempFunctions.put("uniqueSSN", args -> defaultDataHelper.randomFormattedSSN());
        tempFunctions.put("yearsAgo", args -> defaultDataHelper.yearsAgo(args));
        tempFunctions.put("yearsAhead", args -> defaultDataHelper.yearsAhead(args));
        tempFunctions.put("daysAhead", args -> defaultDataHelper.daysAhead(args));
        tempFunctions.put("daysAgo", args -> defaultDataHelper.daysAgo(args));
        tempFunctions.put("randomAlpha", args -> defaultDataHelper.randomAlpha(Integer.parseInt(args[0])));
        tempFunctions.put("randomString", args -> defaultDataHelper.randomString(args));
        tempFunctions.put("randomNumber", args -> defaultDataHelper.randomNumber(Integer.parseInt(args[0])));
        tempFunctions.put("randomEmail", args -> defaultDataHelper.randomEmail());
        tempFunctions.put("randomNumberLessThan", args -> defaultDataHelper.randomNumberInRange(0,Integer.parseInt(args[0])));
        tempFunctions.put("randomNumberInRange", args -> defaultDataHelper.randomNumberInRange(Integer.parseInt(args[0]),Integer.parseInt(args[1])));
        tempFunctions.put("getTimeMs", args -> defaultDataHelper.getTimeMs());
        tempFunctions.put("getTimeSec", args -> defaultDataHelper.getTimeSec());
        tempFunctions.put("UUID", args -> defaultDataHelper.getRandomUUID());
        tempFunctions.put("getTimeMsMinutesAhead", args -> defaultDataHelper.getTimeMsMinutesAhead(Integer.parseInt(args[0])));
        tempFunctions.put("getTimeSecMinutesAhead", args -> defaultDataHelper.getTimeSecMinutesAhead(Integer.parseInt(args[0])));
        functionMapper = Collections.unmodifiableMap(tempFunctions);
    }



}
