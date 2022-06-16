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

import com.jayway.awaitility.Duration;
import com.jayway.awaitility.core.ConditionTimeoutException;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.exception.XmlPathException;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;


public class SeleniumHelper {

    @Autowired
    WebDriver driver;


    @Value("${waitForWebElementSeconds:5}")
    private Integer waitForWebElementSeconds;

    private static final Logger logger = LoggerFactory.getLogger(SeleniumHelper.class);


    public void openPage(String URL) {

        logger.debug("Open page:" + URL);
        driver.get(URL);
    }



    public WebElement getElementByXpathWait(String xpath) {

        try {
            logger.debug("Wait for the element and get it by xpath: " + xpath);
            await()
                    .atMost(waitForWebElementSeconds, SECONDS)
                    .with()
                    .pollInterval(Duration.ONE_HUNDRED_MILLISECONDS)
                    .until(() -> isElementExistsByXpath(xpath).equals(true));
        } catch (ConditionTimeoutException e) {
            logger.debug("Element was not found by xpath: "+xpath+" during "+waitForWebElementSeconds+" seconds");
            return null;
        }
        return driver.findElement(By.xpath(xpath));
    }




    public WebElement getElementByIdWait(String id) {

        try {
            logger.debug("Wait for the element and get it by id: " + id);
            await()
                    .atMost(waitForWebElementSeconds, SECONDS)
                    .with()
                    .pollInterval(Duration.ONE_HUNDRED_MILLISECONDS)
                    .until(() -> isElementExistsById(id).equals(true));
        } catch (ConditionTimeoutException e) {
            logger.debug("Element was not found by id: "+id+" during "+waitForWebElementSeconds+" seconds");
            return null;
        }
        return driver.findElement(By.id(id));
    }


    public Boolean isElementExistsById (String id) {

        List<WebElement> elements = driver.findElements(By.id(id));
        if (elements.size() >= 1) {
            logger.debug("There are found " + elements.size()+ " element(s) with the id: "+id);
            return true;
        } else return false;
    }

    public Boolean isElementExistsByXpath (String xpath) {

        List<WebElement> elements = driver.findElements(By.xpath(xpath));
        if (elements.size() >= 1) {
            logger.debug("There are found " + elements.size()+ " element(s) with the Xpath: "+xpath);
            return true;
        } else return false;
    }




    public String elementGetText(WebElement element) {
        logger.debug("Get element text");
        if (isElementDisplayed(element))
            return element.getText();
        else {
            logger.debug("Element NOT displayed, so return Text as null");
            return null;
        }
    }

    public String getElementTextById(String id) {
        logger.debug("Get element by id and get text");
        WebElement element = getElementByIdWait(id);
        if (element==null)
            return null;
        else return elementGetText(element);
    }

    public String getElementTextByXpath(String xpath) {
        logger.debug("Get element by Xpath and get text");
        WebElement element = getElementByXpathWait(xpath);
        if (element==null)
            return null;
        else return elementGetText(element);
    }


    public Boolean elementClick(WebElement element) {
        logger.debug("Click on element");
        if (isElementEnabled(element)) {
            element.click();
            return true;
        } else {
            logger.debug("Element NOT enabled, cannot click on it");
            return false;
        }
    }

    public Boolean elementDoubleClick(WebElement element) {
        logger.debug("Click on element");
        if (isElementEnabled(element)) {
            Actions act = new Actions(driver);
            act.doubleClick(element).perform();
            return true;
        } else {
            logger.debug("Element NOT enabled, cannot double click on it");
            return false;
        }
    }

    public Boolean elementEnterText(WebElement element, String text) {
        logger.debug("Enter text to the text element");
        if (isElementEnabled(element)) {
            element.sendKeys(text);
            return true;
        } else {
            logger.debug("Element NOT enabled, cannot enter text");
            return false;
        }
    }



    public Boolean getElementByIdClick(String id) {
        logger.debug("Get element by id and click");
        WebElement element = getElementByIdWait(id);
        if (element!=null) return elementClick(element);
        else return false;
    }

    public Boolean getElementByXpathClick(String xpath) {
        logger.debug("Get element by xpath and click");
        WebElement element = getElementByXpathWait(xpath);
        if (element!=null) return elementClick(element);
        else return false;
    }

    public Boolean getElementByXpathDoubleClick(String xpath) {
        logger.debug("Get element by xpath and doubleclick");
        WebElement element = getElementByXpathWait(xpath);
        if (element!=null) return elementDoubleClick(element);
        else return false;
    }

    public Boolean getElementByIdDoubleClick(String xpath) {
        logger.debug("Get element by xpath and doubleclick");
        WebElement element = getElementByIdWait(xpath);
        if (element!=null) return elementDoubleClick(element);
        else return false;
    }

    public Boolean getElementByIdEnterText(String id,String text) {
        logger.debug("Get element by id and click");
        WebElement element = getElementByIdWait(id);
        if (element!=null)
            return elementEnterText(element,text);

        else return false;
    }

    public Boolean getElementByXpathEnterText(String xpath,String text) {
        logger.debug("Get element by xpath and click");
        WebElement element = getElementByXpathWait(xpath);
        if (element!=null)
            return elementEnterText(element,text);

        else return false;
    }


    public Boolean isElementDisplayed (WebElement element) {


        if (element.isDisplayed()) {
            logger.debug("Element is displayed");
            return true;
        }
        else {
            logger.debug("Element is NOT displayed");
            return false;
        }
    }

    public Boolean isElementEnabled(WebElement element) {


        if (element.isEnabled()) {
            logger.debug("Element is enabled");
            return true;
        }
        else {
            logger.debug("Element is NOT enabled");
            return false;
        }
    }
}
