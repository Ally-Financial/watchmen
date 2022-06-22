# Watchmen
------
**Advancing API Test Automation Through Reuse**   


------

Watchmen is an API test automation framework. It provides reusable set of Gherkin steps to express requests, test data mining, assert responses,
connect to data bases, and more giving you the ability to define test scenarios you want to execute.
During runtime, Watchmen transforms Gherkin steps into Java code, enabling you to test your API

---

## Watchmen Philosophies   
 
Anybody, who have ever developed a complex microservices applications, or moved a monolith into a microservice architecture, can confirm, that they had to deal with hundreds of small loosely coupled services.
In result, contract testing, functional testing, integration testing, end-to-end testing in microservices architecture get very complex very fast.
The more microservices you have, the more tests you need, more brittle and time-consuming it becomes to write, execute and maintain test set. 
To reduce the cost of API tests creation and maintenance, Watchmen framework was created. 

**The main philosophy of Watchmen is reuse - reuse steps, reuse input data, reuse assertions, reuse team knowledge**.

While the back-end implementation and technology stack for different APIs are very different, API contracts are very similar. 
The concept of reuse has helped our teams to retrieve testers knowledge from existing tests and reuse it to create abstract Gherkin steps and Java methods. 
Created from predefined set of steps, Watchmen tests are BDD, code-free and self-expressive. 

Based on our experience, three are 2 main limitations that undermine the viability of test automation: 

 - **Inability to provide valid (text, json, csv etc) inputs for each test execution:**
 
We trigger our tests very often (after every code change) and generating valid test inputs is critical to thoroughly test APIs. 
For example, we need correct and unique SSN numbers for testing customer creation service, different First names, Last names and DOB for testing search service, 
unique user name and email for testing enrollment service. Without providing valid and unique values for every test execution, existing tests failing. 
Asking testers to manually generate these values before each test run defeats the very purpose of automated testing.

- **Inability to write meaningful tests:**

The majority of the existing automated testing tools do not explain the usage of scenarios. In addition they also make debugging complex and ineffective. 
Developers and testers have a hard time debugging tests whose purpose is unclear. 

With Watchmen framework we were able to overcome all these challenges and make testing simple but highly efficient:

- By reusing steps, test inputs, assertions and also by mining test data on runtime, Watchmen helps us to achieve high test coverage with reducing test creation time by 90% and almost zero test maintenance.
- By using Cucumber tool based on Behavior Driven Development, Watchmen tests can be read like a story. People who can't read code are able to see description about system behavior and test coverage without any additional documentation. 
- By providing different types of reports, detailed logs and, in addition, raw request and response pairs, Watchmen makes troubleshooting and debugging easier.

**The second principe of Watchmen is flexibility**.

- Wathchmen does not dictate how your API have to be implemented, what is your input data format and how you manage your CI/CD pipeline.
- With Watchmen you can configure your request, send it and receive a response for any API type: Rest, Soap, GraphQL etc.
- With Watchmen you can provide test data in any format and in any combination.
- With Watchmen you can trigger test execution locally or using any CI/CD tool.
- With Watchmen you can extend functionality adding any new steps or capabilities.


---
## Features

- **Watchmen is an API automation framework for any API type**
    - With Watchmen you can configure your request, apply data transformations and generation on runtime, send request and receive a response 
      Then you can validate response and retrieve any data from it for the future use
- **Allowing data driven approach**
    - You can define input data as Cucumber data tables, csv, Json, Xml, txt, yaml files
    - You can provide template JSON payload and modify it on the runtime (change node value, add new node, remove node etc) to define different data driven scenarios
    - You can provide template XML  payload and modify it on the runtime (change node or attribute values, add new node, remove node etc) to define different data driven scenarios
- **Making API Authentication an Easy Process**
    - You can implement your own step to request access token and Watchmen will use token as a Bearer token for next requests
    - You can provide User name and Password and Watchmen will use them to generate Basic authorization token for the requests 
- **Connection with Database**
    - You can read test data directly from DB or perform E2E validation    
- **Placeholders resolves**
    - To modify any input data (including json, xml, csv) on runtime (SSN, email, date on any format, string, number, epoch time etc) you can use different types of placeholders
- **Chain few requests in one scenario**
    - You can chain as many API calls in one Scenario as you need and share data between them 
- **Handling Asynchronous Wait**
    - When you have to test distributed asynchronous system, most of the time, failures are not due to actual error scenarios. 
    They are the consequence of delayed response time and fallback errors. Watchmen can retry requests few times and poll from DB. Retry settings you configure yourself
- **Integration with swagger**
    - You can integrate with swagger for deep response schema validation and generation test objects and methods specific for the particular API
- **Integration with aws**
    - You can connect to DynamoDB, AuroraDB, encrypt files and send them to S3 bucket 
- **Integration with Selenium**
    - You can launch Chrome browser for E2E UI validation 
- **Integration with CI/CD pipeline**
    - You can trigger Watchmen tests with every code change in just a few lines of code 
- **Flexibility**
    - You can add your own steps reusing Watchmen steps or Watchmen helper methods               
- **Easy Setup**
    - All you need is Java 8, Maven or Grade and SDK
- **Fast & Portable**
    - You can wrap Watchmen as a jar file and use Watchmen steps and Watchmen helper methods at any test project you are working on.  

---
## Main Ingredients

Watchmen is based on the following:

  - Java
  - Maven
  - Cucumber-JVM
  - Spring
  - Rest-assured
  - Jackson
  - AWS SDK
  - Openapitools
  - JDBS
  - WebDriver
  - Cluecumber

---
## Install

   - Watchmen packaged as a JAR file and stored in Maven Central Repository. Any java development project can use it as a test resource
   - Clone Watchmen example project and add your tests under resources/features
   - You are free to organize your files using regular java package conventions
   - Add your step definition class if needed and implement your steps
   - Create your scenarios using Watchmen steps. Add tags 
   - Update config.properties with run configurations and runtime data if needed
   - Create your \<TestRun\>.java using template. Define features, glue, tags and type of reports you want to generate ("plugins")
   - Execute \<TestRun\>.java
   - See cucumber reports and logs 
   - Run "mvn cluecumber-report:reporting" to generate Cluecumber report

---  
## Run
- **Easy to run from command line**

Clone Watchmen example project 

       CD project directory 
       mvn test  -Dtest=CoinBaseTestsRunner
---





## Table of Contents

1. [Watchmen Steps](#watchmen-steps)
2. [Watchmen Data Management](#watchmen-data-management)
3. [Watchmen & JSON](#watchmen-and-JSON)
4. [Watchmen & XML](#watchmen-and-XML)
5. [Watchmen Placeholders](#watchmen-placeholders)
6. [Generation Data on Runtime](#generation-data-on-runtime)
7. [Share State Between Steps](#watchmen-share-state-between-steps)
8. [Relational Database Connection](#relational-Database-connection)
9. [Non Relational Database Connection](#non-Relational-Database-connection)
10. [Integration with Swagger](#integration-with-swagger)
11. [Integration with Selenium](#integration-with-selenium)
12. [Integration with aws](#integration-with-aws)
13. [Watchmen Proxy](#watchmen-proxy)
14. [Watchmen Logs](#watchmen-logs)
15. [Watchmen Reports](#watchmen-reports)
16. [Watchmen Security](#watchmen-security)
17. [People](#people)
18. [License](#License)



## Watchmen Steps

**All Watchmen steps and algorithms please see on WATCHMEN_STEPS.md**

- If needed, you can add your own steps definition. In order to do so you have to create new Java StepDefinition class and provide it as a glue in TestRunner class. 
- You can implement new steps from scratch, or you can reuse existing Watchmen steps or Watchmen Java Helper methods. 

---
## Watchmen Data Management
- Data-Driven Testing using Gherkin syntax for Scenario
   -  Declare parameters in the step line
   -  Declare parameters in the step using data Table under the step
- Data-Driven Testing using Gherkin syntax for Scenario Outline 
   - Parameterization with Example Keyword. The scenario will run as many times as many rows Example table has, passing in one row at a time. 
- Data-Driven Testing using External Files
  - Parameterization using csv files. 
  - Parameterization using Json files
  - Parameterization using XML files 
  - Parameterization using Swagger files
- Parameterization using config.property 
  - config.properties hardcoded values
  - OS environment variables
  - Java System properties 
  - Command line arguments
- Parameterization using Scenario Scope. You can store data in the Scenario scope container and json container and use it on the next steps.
- Parameterization using autogenerated function. You can generate data on runtime using predefined functions
 
## Watchmen and JSON  

- To be able to work with JSON (modify JSON payload on runtime, validate JSON response sent by API or DynamoDB), Watchmen uses Tree Model representation of JSON.
- To locate, add, update or remove any node from JSON payload you need to use special language for navigation through JSON documents - JSON path.

- In JSON all path starts from the root node of the tree. 



All possible JSON path expressions are listed below:

| Syntax | Description |
| :------- | :--- |
| nodename     | Select node with the name “nodename”  |
| nodename.  | Select a child node from current node |
| nodename.get(i)      | Select an item i from the array (starts from (1))  |
| nodename.get(node=value)	 |Select an item from the array where (node=value). For value you can apply placeholders |

*Examples:*

| Node to locate | Json path |
| :------- | :--- |
| lastName of the first applicant     | applicants.get(1).lastName  |
| lastName of the second applicant | applicants.get(2).lastName |
| ssn of the first applicant    | applicants.get(1).ssn |
| Residence address of the first applicant	 | applicants.get(1).addresses.get(addressType=Residence) |
|Mailing address of the second applicant|applicants.get(2).addresses.get(addressType=Mailing)|
| City of Residence address of the first applicant	 | applicants.get(1).addresses.get(addressType=Residence).city |

*Example Scenario:*
```
    Scenario Outline: Submit applicant with invalid info
    
        Given I want to call API Endpoint "{{watchmen.baseUrl}}"
        And   I provide Basic authentication on Authorization header
        And   I provide headers as csv file "watchmen/headers/defaultHeaders.csv"
        And   I provide body as raw JSON file "watchmen/payloads/api/v1/addApplicant.json"
        And   I set JSON body node "applicant.issuedIdent.identValue" as unique SSN
        And   I set JSON body node "<node>" to ""<value>""
        When  I send "POST" request
        Then  Response has Status code: "400"
        And   Response has Content Type "application/json"
        And   Response body JSON matches schema "watchmen/response/api/v1/validationError.json"
    
      Examples:
      | node                                  | value                                     |
      | applicant.applicantName.firstName     | Anna @Anna                                |
      | applicant.applicantName.firstName     | Anna#s Anna                               |
      | applicant.applicantName.lastName      | Smith& Smith                              |
      | applicant.email.get(1).emailAddr      | ASDSD.S5_68df-sdfg@sdfdfdf67554@sdfrv     |
      | applicant.email.get(1).emailAddr      | ASDSD.S5_68df-sdfg.sdfdfdf67554.sdfrv     |
    

 
```
---
## Watchmen and XML 

- To be able to modify XML payload on runtime and validate XML payload sent by API, Watchmen store the structure of an XML payload using Node tree model. 
- The sequence of connections from node to node is called a path.

- To locate, add, update or remove any node from XML payload you need to use special language for navigation through XML documents - Xpath

- In XPath, all expressions are evaluated based on a context node. The context node is the node in which a path starts from. The default context is the root node, indicated by a single slash (/).

The most useful path expressions are listed below:

| Syntax | Description |
| :------- | :--- |
|nodename|	Select all nodes with the name “nodename”|
|/	|A beginning single slash indicates a select from the root node, subsequent slashes indicate selecting a child node from current node|
|//	|Select direct and indirect child nodes in the document from the current node - this gives us the ability to “skip levels”|
|.	|Select the current context node|
|..	|Select the parent of the context node|
|@	|Select attributes of the context node|
|[@attribute = 'value']|	Select nodes with a particular attribute value|
|text()|	Select the text content of a node|
|Pipe|	Pipe chains expressions and brings back results from either expression, think of a set union|

For more information please see - https://librarycarpentry.org/lc-webscraping/02-xpath/index.html

*Example Scenario:*
```
     Scenario: addCustomers -- Scenario1 -- Happy Path for Adding Single Customer
     
        Given I want to call API Endpoint "{{endpoint.Customer_XML}}"
        And   I provide headers as data Table:
          | Content-Type | text/xml;charset=UTF-8            |
          | SOAPAction   | "http://ally.com/ws/AddCustomers" |
        And  I provide body as raw XML file "data/Customers/addCustomers/singleCustomerPayload.xml"
        And  I set XML body node "//CustomerInfo/Party/TaxIdentificationNumber" to "%uniqueSSN()%"
        And  I set XML body node "//CustomerInfo/Party/BirthDate" to "%yearsAgo(40)%"
        And  I remove XML body node "(//CustomerInfo/Party/Contact)[1]"
        And  I remove XML body node "(//CustomerInfo/Party/Contact)[2]"
        And  I remove XML body node "//CustomerInfo/Party/PersonName/NameSuffix"
        And  I remove XML body node "//CustomerInfo/Party/MothersMaidenName"
        When I send "POST" request
        Then Response has Status code: "200"
        And  Response body XML node equals to val:
          | //Status/Code                     | CUST-AC-S001                         |
          | //Status/Description              | Customer details added successfully. |
          | //Status/Severity                 | INFO                                 |
          | //CustomerStatus/CustomerID/@Type | CIF

 
```

---

## Watchmen Placeholders

 

- **Placeholder** is any piece of test data that is being resolved on runtime.  Such as a password or database strings stored on config.properties file, applicationId retrieved from previous response and stored on Scenario Scope, unique SSN requested to be generated on execution time. 

- When you use placeholders, you specify the test data by using the following convention:


- **{{parameter-name}}**  - resolve test data  by using the unique name that you specify as the parameter on config.properties file.  Parameter on config.properties file can be pointed to the environment variable or provided as command line argument. 
- **\<parameter-name\>**  -  resolve test data  by using the unique name that you specified when you stored piece of response on Scenario Scope.
- **%function-name%**   -  resolve test data  by using the unique name of function (java method) responsible for data generation.

**What steps support placeholders?**
 
- All steps where you provide input values. Including URL, Base path, any Cucumber data tables, Example tables (Scenario Outline), csv files.  

**What steps do not support placeholders?**
 

- Inside of raw JSON file and raw XML file 
- But you still can modify data provided using file on the next steps ("I set JSON node, I add JSON node…) where placeholders are available. 

*Example Scenario:*
```
    Given I want to call API Endpoint "http://{{demoAPI}}"
    And   I provide basePath as "/method/<method>/connection/<Connection>/price/{{price}}/%yearsAgo(0)%"
    And   I provide query parameters as data Table:
        |ID1|<method>      |
        |ID2|{{price}}     |
        |ID3|%daysAhead(2)%|
    And   I provide headers as data Table:
        |Content-Type|application JSON              |
        |header1     |%yearsAgo(100, MM/dd/yyyy)%   |
        |header2     |%randomEmail%                 |
    When  I send "GET" request
    Then  Response has Status code: "404"
    
    
     Given I want to call API Endpoint "{{watchmen.baseUrl}}"
     And   I provide headers as csv file "watchmen/headers/{{env}}/defaultHeaders.csv"
     And   I provide body as raw JSON file "watchmen/payloads/api/v1/addApplicant.json"
     And   I set JSON body node "<node>" to ""<value>""
     When  I send "POST" request
     Then  Response has Status code: "400"
        
          Examples:
          | node                                  | value                                 |
          | applicant.dob                         | %yearsAgo(17, MM/dd/yyyy)%            |                    |
          | applicant.ssn                         | {{SSN_Anna}}                          |
          | applicant.applicantName.lastName      | Smith& Smith                          |
          | applicant.email.get(1).emailAddr      | %randomEmail%                         |
          | applicant.email.get(1).emailAddr      | ASDSD.S5_68df-sdfg.sdfdfdf67554.sdfrv |
        
 
```
---

## Generation Data on Runtime


- In order to make maintenance of Data Driven scenarios as simple as possible, Watchmen provides new feature to generate input data on fly.
- Without generation data on runtime testers need constantly update json payloads, xml payloads or feature files with new valid data satisfied criteria. 
Watchman takes care of such scenarios. Sensitive input data always unique and valid. 

Some scenarios where you can find it useful:
 
- Customer applies for application and provides a unique SSN
-  Customer applies for application and provides a unique email
-  Customer enrolls on online banking and provides a unique User name
-  In order to be eligible to apply customer should be older then 18 years old
-  In order to be eligible to apply customer should be younger then 18 years old
-  Schedule payment or transfer for today

**How to?** 
 
- To make test data to be generated on runtime you have to use placeholder with the syntax **%function()%**
 
-  Next functions are available 


| Function | Algorithm |
| :------- | :--- |
| uniqueSSN      | generates valid SSN in format XXX-XX-XXXX  |
| yearsAgo(n, format)      | generates Date N years ago in any requested format. By default (if format is not provided) format is YYYY-MM-DD  |
| yearsAhead(n, format)     | generates Date N years ahead in any requested format. By default (if format is not provided) format is YYYY-MM-DD  |
| daysAhead(n, format)     | generates Date N days ahead in any requested format. By default (if format is not provided) format is YYYY-MM-DD  |
| daysAgo(n, format)      | generates Date N days ago in any requested format. By default (if format is not provided) format is YYYY-MM-DD  |
| randomAlpha(length)     | generates Alpha string by requested length  |
| randomString(length, allowed symbols)     | generates String by requested length using any requested chars. By default (if no allowed symbols provided)  – Alphanumeric.  |
| randomNumber (length)     | generates Number by requested length  |
| randomEmail     | generates random email on format Alphanumeric@testAlly.com  |
| getTimeMs     | returns 13 digit Epoch time: returns the number of milliseconds since January 1, 1970, 00:00:00 GTM. Use this function if you need to generate long number that is guaranteed to be bigger than generated before.  |
| getTimeMsMinutesAhead(integer)    | Returns 13 digit Epoch time: returns the number of milliseconds since January 1, 1970, 00:00:00 GTM plus (minus) <integer> minutes.  |
| getTimeSec     | returns 10 digit Epoch time: returns the number of seconds since January 1, 1970, 00:00:00 GTM. Use this function if you need to generate long number that is guaranteed to be bigger than generated before.  |
| getTimeSecMinutesAhead    | returns 10 digit Epoch time: returns the number of seconds since January 1, 1970, 00:00:00 GTM plus (minus) <integer> minutes. * |
| randomNumberLessThan(integer)     | generates Number less than integer.  |
| randomNumberInRange(integer1, integer2)    | generates Number less than integer2 and bigger than integer 1  |
| UUID    | generates random universally unique identifier (UUID). It can be used for random file names, session id in web application, transaction id etc. UUID is a combination of 8-4-4-4-12 characters and looks like: dc6efef8-8b17-4aa0-9434-375f42935f91
 

Examples: test case executed on November 4, 2020:

| Placeholder | Result of data generation |
| :------- | :--- |
| %uniqueSSN%| 456-53-8763  |
| %daysAhead(2)%| 2020-11-06|
| %daysAhead(2,MM/dd/yyyy)%|11/06/2020 |
| %yearsAgo(25,yyyy/mm/dd:hh:mi:ssam)%| 1998/05/31:12:00:00AM|
| %yearsAgo(100)% | 1920-11-04|
| %yearsAhead(100,MM/dd/yyyy)%| 11/04/2120 |
| %randomAlpha(10)% | BCUEPJNAOY|
| %randomString(10, ABC 567^&*#$%_*&)%| %C&6_B&## |
| %randomString(10)%      | DC3EEJ5AK7 |
| %randomNumber(10)%| 6229181101|
| %randomEmail% | vLaAZAirRhuaciPiClzr@TestAlly.com|
| %getTimeMs%      | 1611264537851 |
| %randomNumberLessThan(50)%| 43 |
| %randomNumberInRange(200,500)%| 342 |
| %UUID%      | dc6efef8-8b17-4aa0-9434-375f42935f91  |


*Example Scenario:*
```
        And   I want to call API Endpoint "{{demo}}"
        And   I provide basePath as "/demo/%randomAlpha(10)%"
        And   I provide form data as data Table:
          |data1     |%daysAhead(2)%                  |
          |data2     |%randomString(10)%              |
          |data5     |%daysAhead(2,MM/dd/yyyy)%       |
          |data6     |%yearsAhead(100,MM/dd/yyyy)%    |
        And   I provide body as raw JSON file "demo.json"
        And   I set JSON body node to String:
          |user.DOB |"%yearsAgo(25,MM-dd-yyyy)%"|
        And  I add JSON node "email" to Parent node "" as String ""%randomEmail%""
        And  I set JSON body node "Name.suffix" to ""%randomAlpha(3)%""
        And  I provide headers as data Table:
          |header|%randomString(10, ABC 567^&*#$%_*&)%|
        And  I provide query parameters as data Table:
          |id|%daysAhead(2)%|
        And   I provide path variables as data Table:
          |ID3 |%randomNumberLessThan(50)%|
        And I assert that numeric "%randomNumber(3)%" is bigger than "%randomNumber(2)%"

 
```
---

## Watchmen-Share State Between Steps
- A scenario in Gherkin is created by steps. Some steps can depend on previous steps result. This means that we must be able to share state between steps. 
Java object TestScope.java holding the state of scenario. This is a singleton and to create and managing instances of shared objects Watchmen uses Spring library. Spring does all dependency managing, including making sure instances ends up where we need it.

*TestScope.java share next data between steps:*

| Variable | Holds next state |
| :------- | :--- |
| requestSpec      | Holds Request Specification (URL Headers, body, cookie, parameters)  |
| requestBasePath  | Holds Base path for URL. Before sending request will be yield to main URL |
| response      | Holds Response (Headers, body, Status code) in data type = ResponseOptions<Response>. When Watchmen sends request and receive response Response all together with ResponseToValidate automatically saved to TestScope.  |
| responseToValidate  | Holds Response in data type = ValidatableResponse. When Watchmen sends request and receive response Response all together with  ResponseToValidate automatically saved to TestScope. ValidatableResponse object contains the same data as a Response object, but its methods are assertions rather than get type methods. |
| container  | Map<String, String> - Used to save key-value data. Needed when you chain few API calls in one scenario. |
| jsonContainer  | Map<String, JsonNode> - Used to save key-JsonTree data. Needed when you chain few API calls in one scenario and need to store JsonTree |

*Example Scenario:*
```
     Scenario: Successfully chain few API calls in one scenario
     
         Given I want to call API Endpoint "first_request"
         And   I provide headers as csv file "headers.csv"
         When  I send "GET" request
         Then  Response has Status code: "200"
         And   I store Response header "aws_token" as "saved_token" in scenario scope
         And   I store Response body node "customer.get(id=12345).name" as "saved_name" in scenario scope
         And   I store Cookie "csfrid" as "saved_csfrid" in scenario scope
         And   I assert that string "<saved_name>" contains "Anna"
     
         And I clear my previous API call
     
         Given I want to call API Endpoint "second_request/<saved_name>"
         And   I provide headers as csv file "demo_headers.csv"
         And   I provide headers as data Table:
          |Access token|<saved_token>|
         And   I provide body as raw JSON file "demo.json"
         And   I set JSON body node to String:
           |user.name |"<saved_name>"|
         And I add JSON node "new_name" to Parent node "user" as String ""<saved_name>""
         And I provide cookies as data Table:
           |csfrid|saved_csfrid|  
         When I send "GET" request
         Then Response has Status code: "200"
         And Response body JSON node equal to val:  
           |user.name |"<saved_name>"|  
         And   I store Response body node "user.name" as "saved_name_2" in scenario scope
         And   I assert that string "<saved_name>" is equal to "<saved_name_2>"

 
```
---

## Relational Database connection

- For today Watchmen has implemented connection with the next relational databases:
  
  **Oracle DB, AuroraDB (aws)**
  
  To connect to **Oracle DB** please add to your config.properties file next variables: 
  
        spring.profiles.active=OracleDB
        max.attempts.query.db =X 
        oracle.datasource.url=${url}
        oracle.datasource.username=${user}
        oracle.datasource.password=${pwd}
          
          
  To connect to **Aurora DB**, you have to provide next configurations on your config.properties:
 
         spring.profiles.active=AuroraDB
         max.attempts.query.db=X
         aurora.datasource.url=jdbc:mariadb:aurora://${aurora.host}:${aurora.port}/${aurora.name}
         aurora.datasource.username=${username}
         aurora.datasource.password=${password}

 
 Also, you can read aurora.datasource.password directly from the AWS SMM on the run time. 
 In order to do that please provide next settings: 
 
 1. remove aurora.datasource.password=YYYY from the config.properties
 2. provide next settings for the SMM: 
 
          aurora.paramname=${paramname}
          aurora.namespace=${namespace}
 
 
 3. Provide aws credentials to get connected to AWS SMM: 
 https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html         

---

## Non Relational Database connection

- For today Watchmen has implemented connection with next NoSQL databases:
 
  **DynamoDB**
  
  To establish connection Watchmen uses the Default Credential Provider Chain:
  https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
  
  To manage asynchronous wait please add to your config.properties file:
  
      
          max.attempts.query.db =X 
---
    
## Integration with Swagger

- **By integrating with Swagger you can generate test objects and methods specific for the particular API.**

- to set up Swagger Integration use plugin for Maven: https://github.com/OpenAPITools/openapi-generator
 

- and add api.yaml – swagger file to the resources or directly integrate with git

**This is it!**

To generate library  run command 

    mvn clean compile 




- **You can use Swagger for deep response schema validation.**
-  Please see Watchmen step: 

   *Response body JSON matches swagger file "file_path" schema "definition_pointer"*

---

## Integration with Selenium

- Download the selenium driver that matches your installed version of Chrome from https://chromedriver.chromium.org/downloads
- Unzip the download and move the contents to a directory that is in your system's path

- Add to your config.properties file next line:

      spring.profiles.active=Chrome
      
- Add to your config.properties file next information: 

               driver.path=${path}

                     Mac example: driver.path=/Users/szyf8j/work/git-ws/chromedriver

                     Windows example: driver.path=/Users/szyf8j/work/git-ws/chromedriver.exe



- **WThere are no fully implemented Gherkin Steps available, but Watchmen provides simple methods you can use to implement your steps for browser interaction.**

- Implement your own web steps using methods from SeleniumHelper class - please see watchmen/example-project for example selenium steps implementation 

- All methods on the SeleniumHelper are browser independent, so if you want to test on different browsers you don't need to change methods or steps definitions and only need to establish connection with the browser you need. 
     
- Add your scenarios - please see watchmen/example-project for example scenario

---

## Integration with aws

- For today Watchmen has implemented connection with the next aws services:
  
  **DynamoDB, AuroraDB, S3 bucket**

---


## Watchmen Proxy

- By default all requests from Watchmen will not go through proxy.
- If you need to configure Watchmen to go through Proxy, provide on config.properties:
 

        host=${host}
        port=${port} 
        useProxy=true  
---   
   
## Watchmen Logs

Watchmen is a Spring boot application:
- Logging is built in 
- Logger.Factory.getLogger() for logger instance 
- Uses Logback (by default) 
- Log levels - info, debug, error and trace (for request-response)
- Sensitive information is masked (see settings on logbook.xml)
- For logs configuration you need to use logback.xml stored under Resources.
        
---

## Watchmen Reports


- Watchmen by default generates Cucumber reports in the form of HTML, XML, JSON & TXT, depends on settings on Runner class.
- In addition all request and response pairs are logged in log files, which makes troubleshooting and debugging easier.
- Cluecumber (open source library) generates aggregated test report from Cucumber JSON files: https://github.com/trivago/cluecumber-report-plugin

 

- Run your test cases as usual - cucumber.Json will be generated on target/cucumber-report.
To generate your aggregated report run mvn command:

      mvn cluecumber-report:reporting
      
---

## Watchmen Security

 - Watchmen code does not contain any PII, secrets, keys etc
 - All the inputs you do not want to expose, have to be provided in the config.properties file as a reference to the system variables, or env variables, or command line arguments:
 
        consumer_key=${consumer_key}
        consumer_secret=${consumer_secret}
   
 - While generating logs Watchmen masks all sensitive information 
 - To manage masking rules all what you need to do is define regex patterns for masking on logback.xml file  
 
---

## People

- Ally DQA team
---

## License
---
Copyright 2022 Ally Financial, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the Licenseis distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
