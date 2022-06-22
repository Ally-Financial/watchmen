# Watchmen

**API Automation**


------


## Steps

# <img src="core/src/docs/watchmen-steps.png" width="800" height="400">

---


- **I want to call API Endpoint "<>"**
    - Create new instance of RequestSpecBuilder
    and add to the RequestSpecBuilder URL provided on the step (after resolving all placeholders) and Proxy host and port (config.properties file) if useProxy=true

    - Examples:

          I want to call API Endpoint "https://dev.ally.com/alerts"

          I want to call API Endpoint "{{alerts_URL}}"

          I want to call API Endpoint "https://dev.ally.com/{{alerts_endpoint}}"


     "alerts_endpoint", "alerts_URL" have to be provided on config.properties or as a runtime variable




---

- **I provide basePath as "<>"**
    - Resolve all placeholders and add to the request specification "/basePath"

    - Examples:

           I provide basePath as "{{basePath}}"

           I provide basePath as "/path1/{{path2}}/<path3>/"


    "basePath", "path2" have to be provided on config.properties or as a runtime variable,
    "path3" has to be saved on the previous steps in the Scenario scope
---

- **I provide path variables as data Table:
         |path_id|path_value |**
    -  Read data table and for the each row resolve placeholders and add to the request specification PathVariables

    - Examples:

           I provide path variables as data Table:

               |path1|v1              |
               |path2|<booster_id>    |
               |path3|accounts        |
               |path4|{{account_id}}  |


     "account_id" has to be provided on config.properties or as a runtime variable,
     "booster_id" has to be saved on the previous steps in the Scenario scope
---

- **I provide query parameters as data Table:
         |param_id|param_value |**
    - Read data table and for the each row resolve placeholders and add to the request specification Query parameters

    - Examples:

             I provide query parameters as data Table:

                   |isExternal|true        |
                   |accountId |<account_id>|
                   |customer  |{{cid}}     |
                   |lob       |auto        |



     "cid" has to be provided on config.properties or as a runtime variable,
     "account_id" has to be saved on the previous steps in the Scenario scope
---

- **I provide query parameter "key" as "values"**
    - Split values by "," resolve placeholders for each value and add to the request specification: Query parameters

     - Examples:

               I provide query parameter "lob" as "auto"
               I provide query parameter "lob" as "auto,deposit"
               I provide query parameter "lob" as "{{lob1}},{{lob2}}"
               I provide query parameter "Account" as "<account_id>"


     "lob1" and "lob2" have to be provided on config.properties or as a runtime variable,
     "account_id" has to be saved on the previous steps in the Scenario scope

---

- **I provide headers as data Table:
          |header|value |**
    - Read data table and for the each row resolve placeholders and add to the request specification header = value

     - Examples:

              I provide headers as data Table:

                       |isExternal|true        |
                       |accountId |<account_id>|
                       |customer  |{{cid}}     |
                       |lob       |auto        |


      "cid" has to be provided on config.properties or as a runtime variable,
      "account_id" has to be saved on the previous steps in the Scenario scope
---

- **I provide headers as csv file "file_name.csv"**
    - Read "file_name.csv" file resolve all placeholders and add to the request specification header = value.
      File must be stored under resources, first row of the file will be skipped as it is header

     - Examples:

                    I provide headers as csv file "data/alerts/headers.csv"
                    I provide headers as csv file "data/{{env}}/alerts/headers.csv"

     "env" has to be provided on config.properties or as a runtime variable
---

- **I provide cookies as data Table:
    |cookie|value |**
    - Read data table and for the each row resolve placeholders and add to the request specification:
       <cookie_name = cookie_value>

     - Examples:

                  I provide cookies as data Table:

                           |isExternal|true        |
                           |accountId |<account_id>|
                           |customer  |{{cid}}     |
                           |lob       |auto        |


      "cid" has to be provided on config.properties or as a runtime variable,
      "account_id" has to be saved on the previous steps in the Scenario scope
---

- **I provide body as String: "my payload"**
    - Add to the request specification body as a String

     - Examples:

                   I provide body as String: "{"id":123}"
                   I provide body as String: "" Accepted! ""
                   I provide body as String: "{{payload}}"


         "payload" has to be provided on config.properties or as a runtime variable,
---

- **I provide body as raw JSON file "payload.json"**
    - Read "payload.json" file, resolve all placeholders and if it exists add to the request specification body as a String

    - Examples:

                  I provide body as raw JSON file "testData/alerts/deposit.json"
                  I provide body as raw JSON file "testData/{{env}}/alerts/deposit.json"


    "env" has to be provided on config.properties or as a runtime variable,

---

- **I provide body as raw XML file "filename"**
    - Read "payload.xml" file, resolve all placeholders and if it exists add to the request specification body as a String

    - Examples:

                I provide body as raw XML file "testData/alerts/deposit.xml"
                I provide body as raw XML file "testData/{{env}}/alerts/deposit.xml"


    "env" has to be provided on config.properties or as a runtime variable,
---

- **I provide form data as data Table:
    |data|value |**
    - Read Data table and resolve all placeholders and add to the request specification: FormParams

    - Examples:

             I provide form data as data Table:

                   |isExternal|true        |
                   |accountId |<account_id>|
                   |customer  |{{cid}}     |
                   |lob       |auto        |


     "cid" has to be provided on config.properties or as a runtime variable,
     "account_id" has to be saved on the previous steps in the Scenario scope
---

- **I provide form data as csv file "file_name.csv"**
    - Read "file_name.json" file, resolve all placeholders and if it exists - resolve all placeholders and add to the request specification: FormParams
      File must be stored under resources, first row of the file will be skipped as it is header

      - Examples:

                 I provide form data as csv file "testData/alerts/deposit.csv"
                 I provide form data as csv file "testData/{{env}}/alerts/deposit.csv"


       "env" has to be provided on config.properties or as a runtime variable,
---

- **I provide token "token" as a Bearer token on Authorization header**
    - Resolve placeholdersBearer for token and add "Bearer" followed by a space and token as Authorization header


    - Examples:

                I provide token "1234567890" as a Bearer token on Authorization header
                I provide token "{{alerts_access_token}}" as a Bearer token on Authorization header
                I provide token "<access_token>" as a Bearer token on Authorization header


    "alerts_access_token" has to be provided on config.properties or as a runtime variable,
    "access_token" has to be saved on the previous steps in the Scenario scope
---

- **I provide user name "user" and password "pwd" as a Basic authentication on Authorization header**
    - Resolve placeholders for user and password and add "Basic" followed by base64-encoded string user:password as Authorization header

    - Examples:

               I provide user name "Olga.Ermolova" and password "123456" as a Basic authentication on Authorization header
               I provide user name "{{alerts_user}}" and password "{{alerts_pwd}}" as a Basic authentication on Authorization header
               I provide user name "<new_user>" and password "{{basic_pwd}}" as a Basic authentication on Authorization header


     "alerts_user","alerts_pwd",'basic_pwd" have to be provided on config.properties or as a runtime variable,
     "new_user" has to be saved on the previous steps in the Scenario scope
---

- **I set JSON body node "JSON_node_path" to "value"**
    - Read request specification body, locate requested body node using JSON_node_path. New node value (after resolving all placeholders) will replace the old value
      Assign updated Json as new Request payload.

      Syntax:
      If you have to set value as a String - please use format ""..."" ( example - ""Anna"", ""29708""),

      If you have to set value as a Number or Boolean -please use format "..." (example - "29708", "true")

      - Examples:

                  I set JSON body node "customer.firstName" to ""Anna""
                  I set JSON body node "customer.isActive" to "true"
                  I set JSON body node "customer.firstName" to ""{{new_name}}""
                  I set JSON body node "customer.firstName" to "<first_name>"
                  I set JSON body node "customer.dob" to ""%yearsAgo(25)%""
                  I set JSON body node "customer.dob" to ""%yearsAgo(25,MM/dd/yyyy)%""


       "new_name" has to be provided on config.properties or as a runtime variable,
       "first_name" has to be saved on the previous steps in the Scenario scope,
       "yearsAgo(25)" Watchmen will generate on a runtime
---

- **I set JSON body node to value:
    |JSON_node_path|value |**
    - Read data table and for the each row: read request specification body, locate requested body node using JSON_node_path. New node value (after resolving all placeholders) will replace the old value
      Assign updated Json as new Request payload.

     Syntax:
         If you have to set value as a String - please use format "..." ( example - "Anna", "29708"),
         If you have to set value as a Number or Boolean -please use format ... (example - 29708, true)

      - Examples:

           And I set JSON body node to value:

             |customer.firstName      |""%randomAlpha(20)%"" |
             |customer.isActive       |true                   |
             |customer.dob            |"%yearsAgo(25)%"       |
             |customer.address.zip    |"<zip>"                |
             |customer.address.country|"{{country}}"          |


       "country" has to be provided on config.properties or as a runtime variable,
       "zip" has to be saved on the previous steps in the Scenario scope,
       "yearsAgo(25)", "randomAlpha(20)" Watchmen will generate on a runtime
---

- **I add JSON node "node_name" to Parent node "parent_JSON_path" as value "value"**
    - Read request specification body, locate requested parent body node using parent_JSON_path,
      Add new node "node_name" under parent node with new value after resolving all placeholders.
      Assign updated Json as Request payload

    Syntax:
    If you have to add as a  String - please use format ""..."" ( example - ""Anna"", ""29708"")

    If you have to add as a Numeric or boolean  -please use format "..." (example - "29708", "true")


    - Examples:

               I add JSON node "zip" to Parent node "customer.address" as value ""28203""
               I add JSON node "city" to Parent node "customer.address" as value ""{{city}}""
               I add JSON node "" to Parent node "applicants.get(1).addresses" as value "{"addressLine1": "123 new street","city": "Sunny city"}"


           "city" has to be provided on config.properties or as a runtime variable,


---

- **I add JSON node "node_name" to Parent node "parent_JSON_path" as raw JSON "file.json"**
    - Read request specification body, locate requested parent body node using parent_JSON_path,
      Add new node "node_name" under parent node, Resolve all placeholders for the file name, Read file content as a String
      and set new node value as content of the file. Assign updated Json as Request payload

      - Examples:

                 I add JSON node "Residential" to Parent node "applicant.get(1).addresses" as raw JSON "data/address.json"
                 I add JSON node "Residential" to Parent node "applicant.get(1).addresses" as raw JSON "data/{{env}}/address.json"


      "env" has to be provided on config.properties or as a runtime variable

---

- **I add new item to JSON Array node "arrayNode_jsonPath" as raw JSON "file.json""**
    - Read request specification body, locate requested node of type Array using arrayNode_jsonPath,
      Resolve all placeholders for the file name and read file content as a String.
      Add new item to the array node as file content. Assign updated Json as Request payload

       - Examples:

                   I add new item to JSON Array node "applicants" as raw JSON "data/valid_applicant.json"
                   I add new item to JSON Array node "applicants" as raw JSON "data/{{env}}/valid_applicant.json"


       "env" has to be provided on config.properties or as a runtime variable
---

- **I remove JSON body node "JSON_node_path"**
    - Read request specification body, locate requested body node using JSON_node_path.
      Remove node and assign updated Json as Request payload

    - Examples:

               I remove JSON body node "customer.get(1).first_name"


---

- **I copy JSON tree from "parent.node" and add it under Array node "new_parent.node"**
    - Read request specification body, locate requested parent.node using JSON_node_path. Copy entire child JSON tree from the  parent.node
      Locate requested new_parent.node using JSON_node_path and paste copied JSON tree as a new item to the Array.
      Assign updated Json as Request payload

    - Examples:


              I copy JSON tree from "applicants.get(1).addresses.get(1)" and add it under Array node "applicants.get(1).addresses"
---

- **I copy JSON tree from "parent.node" and add it under Parent node "new_parent.node" as new node "new_node.name"**
    - Read request specification body, locate requested parent.node using JSON_node_path, copy entire child JSON tree from the  parent.node
      Locate requested new_parent.node using JSON_node_path and add new node new_node.name. Set new_node.name as  a JSON tree.
      Assign updated Json as Request payload

      - Examples:


                I copy JSON tree from "applicants.get(1).addresses.get(1)" and add it under Parent node "applicants.get(1).addresses.get(1)" as new node "Mailing"
---

- **I set XML body node "Xpath" to "new_value"**
    - Using  this step you can change any node of type Element or Attribute on runtime. Read request specification body, locate requested body node using Xpath. New node value (after resolving all placeholders) will replace the old value
      Assign updated XML as a new Request payload.

     Syntax:
         If you have to add as a  String - please use format ""..."" ( example - ""Anna"", ""29708"")
         If you have to add as a Numeric or boolean  -please use format "..." (example - "29708", "true")


   - Examples:

              I set XML body node "//CustomerInfo/Party/TaxIdentificationNumber" to ""%uniqueSSN()%""
              I set XML body node "//CustomerInfo/Party/TaxIdentificationNumber" to ""<saved_SSN>""
              I set XML body node "//CustomerInfo/Party/Contact//PostAddress/AddressLine" to ""Test 1""
              I set XML body node "(//CustomerInfo/Party/Contact//PostAddress/AddressLine)[4]" to ""Test 2""
              I set XML body node "//CustomerInfo/Party/Contact/@Type" to ""{{testType}}""


       "testType" has to be provided on config.properties or as a runtime variable,
       "saved_SSN" has to be saved on the previous steps in the Scenario scope,
       "uniqueSSN()" Watchmen will generate at a runtime


---

 - **I set XML body node to value:
       |Xpath|new_value|**
     - Using  this step you can change any node of type Element or Attribute on runtime. Read data table and for the each row: read request specification body, locate requested body node using Xpath. New node value (after resolving all placeholders) will replace the old value
       Assign updated XML as a new Request payload.

       Syntax:
               If you have to add as a  String - please use format "..." ( example - "Anna", "29708")
               If you have to add as a Numeric or boolean  -please use format ... (example - 29708, true)


   - Examples:


                    I set XML body node to value:

                       |//CustomerInfo/Party/TaxIdentificationNumber               |%uniqueSSN%|
                       |//CustomerInfo/Party/TaxIdentificationNumber               |<saved_SSN>|
                       |//CustomerInfo/Party/Contact//PostAddress/AddressLine.     |"Test 1"   |
                       |(//CustomerInfo/Party/Contact//PostAddress/AddressLine)[4] |"Test 2"   |



       "saved_SSN" has to be saved on the previous steps in the Scenario scope,
       "uniqueSSN()" Watchmen will generate at a runtime

---

- **I add XML node "new_node_name" to Parent node "parent_Xpath" with value "new_node_value"**
    - Using this step you can add child node of type Element to any other XML node. Read request specification body, locate requested body node using Xpath. New node value (after resolving all placeholders) will be added.
      Assign updated XML as a new Request payload.

    Syntax:
             If you have to add as a  String - please use format "..." ( example - "Anna", "29708")
             If you have to add as a Numeric or boolean  -please use format ... (example - 29708, true)



   - Examples:

             I add XML node "cmn:AddressLine3" to Parent node "(//CustomerInfo/Party/Contact//PostAddress)[2]" as String ""Test""
             I add XML node "cmn:AddressLine3" to Parent node "(//CustomerInfo/Party/Contact//PostAddress)[2]" as String ""%randomString(10)%""


     "randomString(10)" Watchmen will generate at a runtime


---

- **I add attribute "attr_name" to XML node "Xpath" with value "attr_value"**
    - Using this step you can add attribute to the node of type Element. Read request specification body, locate requested body node using Xpath. Add attribute (after resolving all placeholders).
      Assign updated XML as a new Request payload.
    Syntax:
                   If you have to add as a  String - please use format "..." ( example - "Anna", "29708")
                   If you have to add as a Numeric or boolean  -please use format ... (example - 29708, true)


   - Examples:

                  I add attribute "newAttr" to XML node "(//CustomerInfo/Party/PersonName/FirstName)[2]" as String "ContactType"
                  I add attribute "newAttr" to XML node "(//CustomerInfo/Party/PersonName/FirstName)[2]" as String ""%randomString(ABC 567^&*#$%_*&)%""


           "randomString(10)" Watchmen will generate at a runtime
---

- **I remove XML body node "Xpath"**
    - Using this step you can remove any node of type Element. Read request specification body, locate requested body node using Xpath. Remove node.
      Assign updated XML as a new Request payload.

    - Examples:

                  I remove XML body node "(//CustomerInfo/Party/Contact//PostAddress)[2]"

---


- **I remove attribute "atr_name" from XML body node "Xpath"**
    - Using this step you can remove any node of type Attribute.. Read request specification body, locate requested body node using Xpath. Remove attribute.
      Assign updated XML as a new Request payload.

     - Examples:

                   I remove attribute "Type" from XML body node "(//CustomerInfo/Party/Contact)[2]"

---

- **I send "GET|POST|PUT|DELETE" request**
    - Watchmen will use Request specification defined on all previous steps. Based on provided request type Watchmen will get() | post() | put() | delete()|patch()
      Result will be stored in scenarios scope as: testScope.responseToValidate

       - Examples:

                   I send "GET" request
                   I send "Get" request

---

- **I send "Request type" request and wait for the Response "Response code"**
    - Request being sent repeatedly once per second for X seconds till returned status code is as expected. Once timeout has exceeded and response is not as expected, step fails.
      By default request being executed once per second for 5 seconds. To change default polling time add to config.properties
      waitForResponseSeconds=X

    - Examples:

                    I send "POST" request and wait for the Response "200"
---

- **Response has Status code: "Status code"**
    - Assert that Response has expected status code

    - Examples:

            Response has Status code: "200"
---

- **Response has Content Type "Expected Content Type"**
    - Assert that Response has expected Content Type

    - Examples:

           Response has Content Type "application/json; charset=utf-8"
---

- **Response has response time < <integer> milliseconds**
    - Assert that Response time less then expected (milliseconds)

    - Examples:

               Response has response time < 5000 milliseconds
---

- **Response has next header with the name: "header_name"**
    - Assert that Response has expected header

     - Examples:

                  Response has next header name: "Content-Location"
---

- **Response header "header_name" has next value: "value"**
    - Assert that Response has Header "header_name" and if true - Resolve all placeholders for the expected value
      Assert that header has expected value ignoring case

    - Examples:

                  Response header "Allow" has next value: "true"
                  Response header "Allow" has next value: "{{allow}}"
                  Response header "Allow" has next value: "<saved_before>"

    "allow" has to be provided on config.properties or as a runtime variable,
    "saved_before" has to be saved on the previous steps in the Scenario scope,
---

- **Response has ALL the headers from data Table:
    |header_name|**
    - Read data table and create list of expected headers. Assert that Response has all the headers from list.
      If some headers from the expected list missed on response - step fail.
      If response has some additional headers - step not fail

     - Examples:

                Response has ALL the headers from data Table:
                        |Cache-Control      |
                        |Location           |
                        |Content-Location   |
                        |Retry-After        |

---

- **Response has ALL the headers from csv file: "headers.csv"**
     - Resolve all the placeholders for file name. Read file and build list, assert that Response has all the headers from list.
       If some headers from the expected list missed on response - step fail.
       If response has some additional headers - step not fail

     - Examples:

                 Response has ALL the headers from csv file: "data/headers.csv"
                 Response has ALL the headers from csv file: "data/{{env}}/headers.csv"


      "env" has to be provided on config.properties or as a runtime variable,


---

- **Response has ALL the headers from data Table and ONLY that:
    |header_name|**
    - Read data table and create list of expected headers. Assert that Response has all the headers from list.
      If some headers from the expected list missed on response - step fail.
      If response has some additional headers - step fail

    - Examples:

                Response has ALL the headers from data Table and ONLY that:
                           |Cache-Control      |
                           |Location           |
                           |Content-Location   |
                           |Retry-After        |

---
- **Response has ALL the headers from csv file "file_name.csv" and ONLY that**
    - Resolve all the placeholders for file name. Read file and build list,
      Read data table and create list of expected headers. Assert that Response has all the headers from list.
      If some headers from the expected list missed on response - step fail.
      If response has some additional headers - step fail

    - Examples:

                Response has ALL the headers from csv file "data/headers.csv" and ONLY that
                Response has ALL the headers from csv file  "data/{{env}}/headers.csv" and ONLY that


    "env" has to be provided on config.properties or as a runtime variable,

---

- **Response body JSON has Node: "JSON_node_path"**
    - Assert that Response body JSON has expected Node using JSON_node_path


   - Examples:

                    Response body JSON has Node: "customer.firstName"

---

- **Response body JSON node equals to val:
     |JSON_node_path|value|**
    - Read Data table and for the each row: assert that Response body has expected node using JSON_node_path.
      If first step is true - resolve placeholders and Assert that Node value equals to "Value" ignoring case.
      Step Fails if node not found

    - Examples:

               Response body JSON node equals to val:
                     |product.price2         |{{price}}     |
                     |product.price3         |<saved_price> |
                     |isOtsEligible          |true          |

    "price" has to be provided on config.properties or as a runtime variable,
    "saved_price" has to be saved on the previous steps in the Scenario scope,
---

- **Response body JSON node contains String:
     |JSON_node_path|value|**
    - Read Data table and for the each row: assert that Response body has expected node using JSON_node_path.
      If first step is true - resolve placeholders and Assert that Node value contains "Value" ignoring case.
      Step Fails if node not found

    - Examples:

              Response body JSON node contains String:
                         |product.price2         |{{price}}     |
                         |product.price3         |<saved_price> |
                         |isOtsEligible          |true          |

     "price" has to be provided on config.properties or as a runtime variable,
     "saved_price" has to be saved on the previous steps in the Scenario scope,
---

- **Response body JSON node "JSON_node_path" is an array of size =XX**
    - Assert that Response body has expected node using JSON_node_path
      If first step is true - Assert that node is an array of expected size.
      If requested node is not an array but a single value - size = 0.
      Step Fails if node not found

     - Examples:

               Response body JSON node "product.price" is an array of size =5


---

- **Response body JSON matches schema "schema.json"**
    - Assert that Response body JSON matches schema "schema.json" Path to the schema file can be provided using placeholders.
      If validation fails - report provided for further analyses.
      "schema.json" must be stored under Resources.

    - Examples:

                   Response body JSON matches schema "data/{{env}}/approved_schema.json"

     "env" has to be provided on config.properties or as a runtime variable
---

- **Response body JSON matches swagger file "file_path" schema "definition_pointer"**
    - Resolve all placeholders for "file_path" and locate swagger file (yaml or json)
      Read swagger file, locate schema on swagger file using definition_pointer and build json schema.
      Validate response using json schema. If validation fails - report provided for further analyses.
      Swagger file must be stored under Resources.

    - Examples:

                Response body JSON matches swagger file "data/{{env}}/ccn.yaml" schema "/components/schemas/preferences"

     "env" has to be provided on config.properties or as a runtime variable
---

- **Response body JSON matches JSON file "file_path"**
    - Resolve all placeholders for "file_path" and locate json file. Read response and assert response matches json file.
      Please see for more information: https://www.baeldung.com/jackson-compare-two-json-objects.
      In case of error Watchmen fails step and provide report where it is the difference

       - Examples:

                 Response body JSON matches JSON file "data/{{env}}/expected_response.json"

        "env" has to be provided on config.properties or as a runtime variable
---

- **Response body XML has node: "Xpath"**
    - Assert that Response body XML has expected node using xpath

    - Examples:

               Response body XML has node: "//Status/Code"
               Response body XML has node: "//Status/Code/@Type"


---

- **Response body XML node equals to val:
          |Xpath|value|**
    - Read Data table and for each row: assert that Response body has expected node using xpath.
      If first step is true - resolve placeholders and Assert that Node value equals to "Value" ignoring case

    - Examples:

               Response body XML node equals to val:
                          |//CustomerStatus/CustomerID/@Type.    |CIF               |
                          |(//CustomerStatus/CustomerID/@Type)[2]|{{expected_type}} |
                          |(//CustomerStatus/CustomerID.         |<custID>          |

       "expected_type" has to be provided on config.properties or as a runtime variable,
       "custID" has to be saved on the previous steps in the Scenario scope,
---

- **Response body XML node contains val:
    |Xpath|value|**
    - Read Data table and for each row: assert that Response body has expected node using xpath.
      If first step is true - resolve placeholders and Assert that Node value contains "Value" ignoring case

    - Examples:

               Response body XML node contains val:
                     |//CustomerStatus/CustomerID/@Type.    |CIF               |
                     |(//CustomerStatus/CustomerID/@Type)[2]|{{expected_type}} |
                     |(//CustomerStatus/CustomerID.         |<custID>          |

       "expected_type" has to be provided on config.properties or as a runtime variable,
       "custID" has to be saved on the previous steps in the Scenario scope,
---

- **I store Response header "header_name" as "key" in the scenario scope**
    - If "header_name" found on the Response - store (key,header_value) in the Scenario scope.
      If "header_name" does not found on the Response  - step will Fail

    - Examples:

              I store Response header "Connection" as "Connection" in the scenario scope

---

- **I store Cookie "cookie_name" as "key" in the scenario scope**
    - If "cookie" found on the Response - store (key,cookie_value) in the Scenario scope.
      If "cookie" does not found on the Response - step will Fai

    - Examples:

              I store Cookie "CMSMSESSION" as "CMSMSESSION" in the scenario scope

---

- **I store JSON Response body node "JSON_node_path" as "key" in the scenario scope**
    - If "JSON_node_path"  found on the JSON Response - store (key,node_value) in the Scenario scope.
      If "JSON_node_path" not found on the Response -  step will Fail

    - Examples:

               I store JSON Response body node "method" as "method" in the scenario scope
               I store JSON Response body node "Customers.get(1).SSN" as "SSN" in the scenario scope
               I store JSON Response body node "Customers.get(firstName=Anna).SSN" as "SSN" in the scenario scope
---

- **I store XML Response body node "xpath" as "key" in the scenario scope**
    - If node found using "xpath" on the XML Response  - store (key,node_value) in he Scenario scope.
          If node not found using "xpath"  -  step will Fail

    - Examples:

              I store XML Response body node "//CustomerStatus/CustomerID" as "CustomerID" in the scenario scope
              I store XML Response body node "(//CustomerStatus/CustomerID/@Type)[1]" as "CustomerID_Type" in the scenario scope
---


- **I establish connection with Database "DB""**
    - Currently Watchmen supports Oracle DB and Aurora DB (aws) connection
    - Watchmen reads information about requested connection to DB during launch time from the config.properties (spring.profiles.active)
    - By default (if spring.profiles.active not specified) there are no connection to any DB
    - On this step Watchmen will verify the connection to the Data Base SUCCESSFUL by executing "SELECT 1 FROM DUAL"
    - If provided DB credentials or URL connection are invalid - connection cannot be established and this step will fail

    - Examples:

              I establish connection with Database "Oracle"
              I establish connection with Database "AuroraDB"

---

- **I query for Integer "sql_query" and store result as "key" in the scenario scope**
    - Resolve placeholders for sql_query. If SQL provided as a file (*.sql)- locate file and read from file SQL query (one query per file).
      Execute SQL query. Cast result to Integer. Save result in Scenario Scope as a key.


   - Examples:

                I query for Integer "SELECT Customerid FROM Customers WHERE CustomerName=Anna" and store result as "Customerid" in the scenario scope
                I query for Integer "data/{{env}}/query_Customerid.sql" and store result as "Customerid" in the scenario scope


   "env" has to be provided on config.properties or as a runtime variable,
   "query_Customerid.sql" has to be stored under Resources and contains SQL query


---

- **I query for String "sql_query" and store result as "key" in the scenario scope"**
    - Resolve placeholders for sql_query. If SQL provided as a file (*.sql) - locate file and read from file SQL query (one query per file).
      Execute SQL query. Cast result to String. Save result in Scenario Scope as a key.

   - Examples:

             I query for String "SELECT CustomerName FROM Customers WHERE id=1234" and store result as "CustomerName" in the scenario scope
             I query for Integer "data/{{env}}/query_CustomerName.sql" and store result as "Customerid" in the scenario scop


    "env" has to be provided on config.properties or as a runtime variable,
    "query_CustomerName.sql" has to be stored under Resources and contains SQL query


---

- **I query for String "sql_query" with parameters as Data Table and store result as "key" in the scenario scope:
        |param_name|value|**
    - Resolve placeholders for sql query. If SQL query provided as a file (*.sql) - locate file and read from file SQL query (one query per file).
      Resolve all placeholders for values from data table. Execute SQL query.
      Cast result to String. Save result in Scenario Scope as key

   - Examples:

                I query for String "SELECT CustomerName FROM Customers WHERE CustomerId=:id" with parameters as Data Table and store result as "CustomerName" in the scenario scope:

                |id|1234|



                I query for String "query_CustomerName_ById.sql" with parameters as Data Table and store result as "CustomerName" in the scenario scope:

                |id|1234|



    "query_CustomerName_ById.sql" has to be stored under Resources and contains SQL query


---

- **I query for Integer "sql_query" with parameters as Data Table and store result as "key" in the scenario scope:
        |param_name|value|**
    - Resolve placeholders for sql query. If SQL query provided as a file (*.sql) - locate file and read from file SQL query (one query per file).
      Resolve all placeholders for values from data table. Execute SQL query.
      Cast result to Integer. Save result in Scenario Scope as key

    - Examples:

               I query for Integer "SELECTCustomeridFROMCustomers WHERE CustomerName=:name" with parameters as Data Table and store result as "Customerid" in the scenario scope:

                    |name|Anna|



               I query for Integer "query_CustomerId_ByName.sql" with parameters as Data Table and store result as "Customerid" in the scenario scope:

                    |name|Anna|



     "query_CustomerId_ByName.sql" has to be stored under Resources and contains SQL query

---

- **I query DynamoDB table "TableName" with Primary Key "PK" value "value" and Secondary Key "SK" value "value" and save result as JSON "Json_name"**
    - Establish connection to DynamoDB using settings from config.properties file.
      Resolve all placeholder for Primary Key value and Secondary Key value and query for data.
      Save Json response as "Json_name" in the Scenario Scope

    - Examples:

              I query DynamoDB table "Customers" with Primary Key "ID" value "123" and Secondary Key "Name" value "Anna" and save result as JSON "Customer_1"
              I query DynamoDB table "Customers" with Primary Key "<custID>" value "123" and Secondary Key "Name" value "Anna" and save result as JSON "Customer_1"

     "custID" has to be saved on the previous steps in the Scenario scope
---

- **I query DynamoDB table "TableName" with Primary Key "PK" value "value" and save result as JSON "Json_name"**
    - Establish connection to DynamoDB using secrets from config.properties file.
      Resolve all placeholder for Primary Key value and query for data.
      Save Json response as "Json_name" in the Scenario Scope

    - Examples:

                I query DynamoDB table "Customers" with Primary Key "<custID>" value "123" and save result as JSON "Customer_1"

    "custID" has to be saved on the previous steps in the Scenario scope

---

- **I assert that saved JSON "Json_name" node "Json_path" contains value "value"**
    - Resolve all placeholders for value. Read Json "Json_name" from the Scenario Scope.
      Assert that json_path value contains expected value (ignoring case).

    - Examples:

                 I assert that saved JSON "Customer_1" node "name" contains value "Anna"
                 I assert that saved JSON "Customer_1" node "SSN" contains value "<ssn>"

     "ssn" has to be saved on the previous steps in the Scenario scope

---

- **I assert that saved JSON "Json_name" node "Json_path" equals to value "value"**
    - Resolve all placeholders for value. Read Json "Json_name" from the Scenario Scope.
      Assert that json_path value equals to value (ignoring case).

    - Examples:

                 I assert that saved JSON "Customer_1" node "name" equals to value "Anna"
                 I assert that saved JSON "Customer_1" node "SSN" equals to value "<ssn>"

     "ssn" has to be saved on the previous steps in the Scenario scope
---

- **I request pre-signed URL and publicKey**
    - Step does not exist on Watchmen - you have to add it and provide step definition yourself.
      Step should implement next logic: call the API responsible for providing you pre-signed URL and publicKey.
      API responses back with the pre-signed URL and publicKey URL.
      Save pre-signed URL in the Scenario scope as "uploadUrl". Save publicKey URL in the Scenario scope as ""publicKeyUrl"
---

- **I read publicKey from Url "publicKeyURL" and save as a file "publicKeyFile_name"**
    - Resolve placeholders for publicKeyURL and read public key from Url and save it as txt file.

    - Examples:

               I read publicKey from Url "<publicKeyURL>" and save as a file "public_key.txt"


    "publicKeyURL" has to be saved on the previous steps in the Scenario scope

---

- **And I encrypt file "file_name" with publicKey "publicKeyFile_name" and send PUT request to the S3 pre-signed URL "pre-signed URL" with the headers:
     |header|value|**
    - Resolve all placeholders for file_name and publicKeyFile_name
    - Read file "file_name". Read public_key from publicKeyFile_name. Encrypt file with public_key using PGP encryption
      Read headers from data table. Resolve all placeholders for headers and upload encrypted file to the pre-signed URL
      (PUT request) with the headers.

    - Examples:

             I encrypt file "data/{{env}}/alertTrigger.json" with publicKey "public_key.txt" and send PUT request to the S3 pre-signed URL "<uploadUrl>" with the headers:
                 |Content-Type                |       |
                 |x-amz-server-side-encryption|aws:kms|

    "env" has to be provided on config.properties or as a runtime variable,
    "uploadUrl" has to be saved on the previous steps in the Scenario scope

---

- **And I encrypt file "file_name" with publicKey "publicKeyFile_name" using armored format and send PUT request to the S3 pre-signed URL "pre-signed URL" with the headers:
     |header|value|**
    - Resolve all placeholders for file_name and publicKeyFile_name
    - Read file "file_name". Read public_key from publicKeyFile_name. Encrypt file with public_key using PGP encryption with armored format.
      Read headers from data table. Resolve all placeholders for headers and upload encrypted file to the pre-signed URL
      (PUT request) with the headers.

    - Examples:

              I encrypt file "data/{{env}}/alertTrigger.json" with publicKey "public_key.txt" using armored format and send PUT request to the S3 pre-signed URL "<uploadUrl>" with the headers:
                |Content-Type                |       |
                |x-amz-server-side-encryption|aws:kms|

        "env" has to be provided on config.properties or as a runtime variable,
        "uploadUrl" has to be saved on the previous steps in the Scenario scope

---

- **I assert that numeric "numeric1" is equal to "numeric2"**
    - Resolve all type of placeholders for numeric1 and numeric2
      Assert ignoring numeric format (25 = 25.00)

      - Examples:

                 I assert that numeric "23" is equal to "23.0"
                 I assert that numeric "<saved_age>" is equal to "18"

      "saved_age" has to be saved on the previous steps in the Scenario scope
---

- **I assert that numeric "numeric1" is bigger than "numeric2"**
    - Resolve all type of placeholders for numeric1 and numeric2
      Assert ignoring numeric format (25 = 25.00)

    - Examples:

                 I assert that numeric "45.5" is bigger than "30"
                 I assert that numeric "%randomNumber(3)%" is bigger than "<saved_age>"
                 I assert that numeric "<saved_Age>" is bigger than "25"

     "saved_age" has to be saved on the previous steps in the Scenario scope
     "randomNumber(3)" Watchmen will generate at a runtime

---

- **I assert that numeric "numeric1" is not equal to "numeric2"**
    - Resolve all type of placeholders for numeric1 and numeric2
      Assert ignoring numeric format (25 = 25.00)

    - Examples:

                I assert that numeric "23" does not equal to "345"
                I assert that numeric "<price>" does not equal to "{{price}}"

     "price" has to be saved on the previous steps in the Scenario scope,
     "price" has to be provided on config.properties or as a runtime variable

---

- **I assert that string "string1" contains "string2"**
    - Resolve all type of placeholders for string1 and string2
      Assert ignoring case

    - Examples:

               I assert that string "Application submitted" contains "submitted"
               I assert that string "<app_status>" contains "submitted"

    "app_status" has to be saved on the previous steps in the Scenario scope,

---

- **I assert that string "string1" does not contain "string2"**
    - Resolve all type of placeholders for string1 and string2
      Assert ignoring case

    - Examples:

               I assert that string "<app_status>" does not contain "submitted"

     "app_status" has to be saved on the previous steps in the Scenario scope

---

 - **I assert that string "string1" is equal to  "string2"**
     - Resolve all type of placeholders for string1 and string2
       Assert ignoring case

     - Examples:

                I assert that string "Application submitted" is equal to "<app_status>"
                I assert that string "<app_status>" is equal to "{{app_status}}"

     "app_status" has to be saved on the previous steps in the Scenario scope,
     "app_status" has to be provided on config.properties or as a runtime variable
---

- **I assert that string "string1" is not equal to "string2"**
    - Resolve all type of placeholders for string1 and string2
      Assert ignoring case

    - Examples:

                 I assert that string "Application submitted" is not equal to  "<app_status>"
                 I assert that string "<app_status>" is not equal to  "{{app_status}} "

     "app_status" has to be saved on the previous steps in the Scenario scope,
     "app_status" has to be provided on config.properties or as a runtime variable

---

- **I clear my previous API call**
    - If you chain few requests in one scenario, every next API call must start from cleaning up previous request/response.

      Step executes next:

      response = null;
      responseToValidate =null;
      requestSpec = restAssuredHelper.startNewRequestSpecification();
      requestBasePath = new StringBuilder();

      TestScope (Container and Json Container where we store needed data) not being cleaned up.

      - Examples:

                 I clear my previous API call


---

- **I clear my previous API call and switch Proxy**
    - If you chain few requests in one scenario, every next API call must start from cleaning up previous request/response.
      If different requests requiring Proxy on/off use this step to switch.
      Step executes next:

      switch Proxy;
      response = null;
      responseToValidate =null;
      requestSpec = restAssuredHelper.startNewRequestSpecification();
      requestBasePath = new StringBuilder();

      TestScope (Container and Json Container where we store needed data) not being cleaned up.

      - Examples:

                  I clear my previous API call and switch Proxy

---

- **I provide test data as data Table**
    - If you need to allocate all the test data for the big Scenario in one place - use this step.
      Data value can use any type of placeholders. Watchmen will read data table, resolve all placeholders and
      store key, value in the scenario scope. To use test data in the next steps just refer to it
      using placeholder <key>.

    *Example Scenario:*
    ```
         Scenario: Successfully chain few API calls in one scenario


             Given I provide test data as data table:
                   | CIF   			 	   | {{CIF016}}    |
                   | ACCOUNT_NUMBER        | 2343444343434 |

             Given I want to call API Endpoint "{{demo_request}}"
             And I provide basePath as "account/<ACCOUNT_NUMBER>"
             And I provide my Access Token on Authorization header
             And I provide headers as csv file "config/headers.csv"
             And I provide headers as data Table:
                   | CIF |<CIF> |
             When I send "GET" request
             Then Response has Status code: "200"

             And I clear my previous API call

             Given I want to call API Endpoint "second_request/<saved_name>"
             And   I provide headers as csv file "demo_headers.csv"
             And   I provide body as raw JSON file "demo.json"
             And   I set JSON body node to String:
                   |accountNumber |<ACCOUNT_NUMBER>|
             When I send "GET" request
             Then Response has Status code: "200"
    ```
---

- **I wait for (\d+) seconds**
  - Watchmen will wait for <> seconds. Please be aware that wait time is not optimized - even if desired condition is already true,
  tests execution still sleeping for requested amount of time.

   - Examples:

                I wait for 5 seconds

---

## People

- [Olga Ermolova]

---

## License

Copyright 2022 Ally Financial, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the Licenseis distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
