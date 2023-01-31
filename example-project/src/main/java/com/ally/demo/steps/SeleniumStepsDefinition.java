package com.ally.demo.steps;

import com.ally.d3.watchmen.steps.CommonApiStepsDefinition;
import com.ally.d3.watchmen.utilities.RequestHelper;
import com.ally.d3.watchmen.utilities.SeleniumHelper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


public class SeleniumStepsDefinition {

    private static final Logger logger = LoggerFactory.getLogger(SeleniumStepsDefinition.class);

    @Autowired
    CommonApiStepsDefinition watchmen;

    @Autowired
    SeleniumHelper seleniumHelper;

    @Autowired
    RequestHelper requestHelper;

    @Autowired
    WebDriver driver;

    @Value("${outlookURL}")
    private String outlookUrl;

    @Given("^I open Web Page \"([^\"]*)\"$")
    public void openWebPage(String url) {
        logger.info("I open Web Page " + url);

        logger.debug("Check if there are any placeholder for the URL and replace it");
        String newURL = requestHelper.resolveAllPlaceholdersURL(url);

        seleniumHelper.openPage(newURL);

    }

    @And("^I navigate to the element by Id \"([^\"]*)\" and verify Text equals to \"([^\"]*)\"$")
    public void navigateElementByIdTextEquals(String id, String expectedText) {

        logger.info("I navigate to the element " + id + " by Id and check Text equals to " + expectedText);

        String text = seleniumHelper.getElementTextById(id);
        if (text == null)
            Assert.assertTrue("Was NOT able to get text for the element with id: " + id, (false));
        else if (text.equals(expectedText)) {
            Assert.assertTrue("Text for the element with id: " + id + " matches expected", (true));
        } else
            Assert.assertTrue("Text Does not match expected text for the element with id: " + id + ". Actual text is: " + text, (false));
    }

    @And("^I navigate to the element by Id \"([^\"]*)\" and verify Text contains \"([^\"]*)\"$")
    public void navigateElementByIdTextContains(String id, String expectedText) {
        logger.info("I navigate to the element " + id + " by Id and check Text equals to " + expectedText);

        String text = seleniumHelper.getElementTextById(id);
        if (text == null)
            Assert.assertTrue("Was NOT able to get text for the element " + id, (false));
        else if (text.contains(expectedText)) {
            Assert.assertTrue("Text matches expected " + id, (true));
        } else
            Assert.assertTrue("Text Does not contain expected text for the element with id: " + id + ". Actual text is: " + text, (false));
    }

    @And("^I navigate to the element by Xpath \"([^\"]*)\" and verify Text equals to \"([^\"]*)\"$")
    public void navigateElementByXpathTextEquals(String xpath, String expectedText) {
        logger.info("I navigate to the element " + xpath + " by Xpath and check Text equals to " + expectedText);

        String text = seleniumHelper.getElementTextByXpath(xpath);
        if (text == null)
            Assert.assertTrue("Was NOT able to get text for the element with Xpath: " + xpath, (false));
        else if (text.equals(expectedText)) {
            Assert.assertTrue("Text for the element with Xpath: " + xpath + " matches expected", (true));
        } else
            Assert.assertTrue("Text Does not match expected text for the element with Xpath: " + xpath + ". Actual text is: " + text, (false));
    }

    @And("^I navigate to the element by Xpath \"([^\"]*)\" and verify Text contains \"([^\"]*)\"$")
    public void navigateElementByXpathTextContains(String xpath, String expectedText) {
        logger.info("I navigate to the element " + xpath + " by Xpath and check Text equals to " + expectedText);

        String text = seleniumHelper.getElementTextByXpath(xpath);
        if (text == null)
            Assert.assertTrue("Was NOT able to get text for the element " + xpath, (false));
        else if (text.contains(expectedText)) {
            Assert.assertTrue("Text matches expected " + xpath, (true));
        } else
            Assert.assertTrue("Text Does not contain expected text for the element with Xpath: " + xpath + ". Actual text is: " + text, (false));
    }


    @And("^I navigate to the element by Id \"([^\"]*)\" and click on it$")
    public void navigateElementByIdClick(String id) {
        logger.info("I navigate to the element " + id + " by Id and click on it");
        Assert.assertTrue("Was not able to click on the element with id " + id, (seleniumHelper.getElementByIdClick(id)));
    }

    @And("^I navigate to the element by Xpath \"([^\"]*)\" and click on it$")
    public void navigateElementByXpathClick(String xpath) {
        logger.info("I navigate to the element " + xpath + " by Id and click on it");
        Assert.assertTrue("Was not able to click on the element with xpath " + xpath, (seleniumHelper.getElementByXpathClick(xpath)));
    }

    @And("^I navigate to the element by Xpath \"([^\"]*)\" and double click on it$")
    public void navigateElementByXpathDoubleClick(String xpath) {
        logger.info("I navigate to the element " + xpath + " by Id and click on it");
    }

    @And("^I navigate to the element by Xpath \"([^\"]*)\" and enter text \"([^\"]*)\"$")
    public void navigateElementByXpathEnterText(String xpath, String text) {
        logger.info("Step: I navigate to the element by Xpath " + xpath + " and enter text " + text);
        Assert.assertTrue("Was not able to enter text to the element with xpath " + xpath, (seleniumHelper.getElementByXpathEnterText(xpath, text)));
    }

    @And("^I navigate to the element by Id \"([^\"]*)\" and enter text \"([^\"]*)\"$")
    public void navigateElementByIdEnterText(String id, String text) {
        logger.info("Step: I navigate to the element by Id " + id + " and enter text " + text);
        Assert.assertTrue("Was not able to enter text to the element with Id " + id, (seleniumHelper.getElementByIdEnterText(id, text)));
    }


    @And("^I open Outlook mailbox using user \"([^\"]*)\" and password \"([^\"]*)\" and navigate to the latest email$")
    public void openOutlookMailboxUsingUserAndPasswordAndNavigateToTheLatestEmail(String outlookUser, String outlookPassword) {
        logger.info("Step: I open Outlook mailbox using user " + outlookUser + " and password " + outlookPassword + " and navigate to the latest email");

        String newOutlookUser = requestHelper.resolveAllPlaceholders(outlookUser);
        String newOutlookPassword = requestHelper.resolveAllPlaceholders(outlookPassword);

        String signInButton = "//*[contains(@data-task,'signin')]";
        String loginUserTextBox = "//*[contains(@name,'loginfmt')]";
        String submitButton = "idSIButton9";
        String loginPwdTextBox = "//*[contains(@name,'passwd')]";
        String submitButton2 = "idSIButton9";
        String latestEmail = "//*[contains(@class,'customScrollBar')]//child::div[1]//child::div[1]//child::div[1]//child::div[1]//child::div[1]//child::div[1]//child::div[1]";

        openWebPage(outlookUrl);
        if (seleniumHelper.isElementExistsByXpath(signInButton)) {
            navigateElementByXpathClick(signInButton);
            watchmen.waitForSeconds(4);
            navigateElementByXpathEnterText(loginUserTextBox, newOutlookUser);
            watchmen.waitForSeconds(4);
            navigateElementByIdClick(submitButton);
            watchmen.waitForSeconds(4);
            navigateElementByXpathEnterText(loginPwdTextBox, newOutlookPassword);
            watchmen.waitForSeconds(4);
            navigateElementByIdClick(submitButton);
            watchmen.waitForSeconds(4);
            navigateElementByIdClick(submitButton2);
            watchmen.waitForSeconds(4);
        }
        navigateElementByXpathClick(latestEmail);
        watchmen.waitForSeconds(10);

    }

    @Then("^I sign out from Outlook$")
    public void closeBrowser() {
        logger.info("Step: I sign out from Outlook");
        navigateElementByXpathClick("//*[contains(@class,'mectrl_profilepic mectrl_profilepic_initials')]");
        navigateElementByIdClick("mectrl_body_signOut");
    }

    @Then("^I navigate to latest email$")
    public void latestEmail() {
        logger.info("Step: I navigate to latest email");
        navigateElementByXpathClick("//*[contains(@class,'customScrollBar')]//child::div[1]//child::div[1]//child::div[1]//child::div[1]//child::div[1]//child::div[1]//child::div[1]");

    }

    @Then("^I clean up cookies$")
    public void deleteCookies() {
        logger.info("Step: I clean up cookies");
        driver.manage().deleteAllCookies();
    }

}
