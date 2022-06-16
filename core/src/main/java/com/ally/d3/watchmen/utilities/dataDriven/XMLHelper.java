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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.StringReader;
import java.io.StringWriter;

public class XMLHelper {

    private static final Logger logger = LoggerFactory.getLogger(XMLHelper.class);


    public Document createDocumentFromString(String xml)  {

        logger.debug("Create org.w3c.dom.Document");

        Document doc = null;

        try {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(new InputSource(new StringReader(xml)));
        doc.getDocumentElement().normalize();
        logger.debug("Root of xml DOM: " + doc.getDocumentElement().getNodeName());
        return doc;

        } catch (Exception ex) {
            logger.debug("Could NOT parse XML string as an org.w3c.dom.Document");
            throw new RuntimeException("Could NOT parse XML string as an org.w3c.dom.Document");
        }

    }


    //path must be on format /data/startdate
    public void setXmlNode(Document xml, String path, String value){
        logger.debug("set Xml Node Value");

            Node nodeToUpdate = getXmlNode(xml,path);
            if (nodeToUpdate==null){
                logger.debug("Not able to proceed to the xml node: "+ path);
                throw new RuntimeException("Not able to proceed to the xml node: "+ path);
            }
            logger.debug("Node found, set new Value");
            nodeToUpdate.setTextContent(value);

    }


    //path must be on format /data/startdate
    public void addXmlNode(Document xml, String parentPath, String node, String value){
        logger.debug("add Xml Node to the Xml Document");

            Node parentNode = getXmlNode(xml,parentPath);
            if (parentNode!=null){
                logger.debug("Parent Node found, append new child to it");
                Element newNode = xml.createElement(node);
                Element parentElem = (Element)parentNode;
                newNode.appendChild(xml.createTextNode(value));
                parentElem.appendChild(newNode);
                parentElem.appendChild(xml.createTextNode("\n"));
            }
            else {
                logger.debug("Not able to proceed to the parent xml node: " + parentPath);
                throw new RuntimeException("Not able to proceed to the parent xml node: " + parentPath);
            }
    }

    //path must be on format /data/startdate
    public void addXmlNodeAttribute(Document xml, String attr, String path, String value){
        logger.debug("add Xml Node Attribute to the Xml Document");

        Node nodeToAddAttr = getXmlNode(xml,path);
        if (nodeToAddAttr!=null) {
            logger.debug("Node found, add attribute to it");
            //Create a new attribute
            Attr attrToAdd = xml.createAttribute(attr);
            attrToAdd.setValue(value);

            //Add the attribute to the node
            nodeToAddAttr.getAttributes().setNamedItem(attrToAdd);
        }
        else {
            logger.debug("Not able to proceed to the xml node: " + path);
            throw new RuntimeException("Not able to proceed to the xml node: " + path);
        }

    }


    //path must be on format /data/startdate
    public void removeXmlNode(Document xml, String path){
        logger.debug("Remove Xml Node from Xml Document");

        Node nodeToRemove = getXmlNode(xml,path);
        if (nodeToRemove!=null){
            logger.debug("Node found, remove it from parent");
            Node parent = nodeToRemove.getParentNode();
            parent.removeChild(nodeToRemove);
        }
        else {
            logger.debug("Not able to proceed to the xml node: " + path);
            throw new RuntimeException("Not able to proceed to the xml node: " + path);
        }
    }

    //path must be on format /data/startdate
    public void removeXmlNodeAttribute(Document xml, String path, String attribute){
        logger.debug("remove Xml Node Attribute: "+attribute+", from Xml node: "+path);

        Node nodeToRemove = getXmlNode(xml,path);
        if (nodeToRemove!=null) {
            logger.debug("Node found, look for attribute and remove it");
            if (nodeToRemove.getAttributes().getNamedItem(attribute) != null)
                nodeToRemove.getAttributes().removeNamedItem(attribute);
            else {
                logger.debug("Not able to find attribute: " + attribute);
                throw new RuntimeException("Not able to find attribute: " + attribute);
            }
        }
        else {
            logger.debug("Not able to proceed to the xml node: " + path);
            throw new RuntimeException("Not able to proceed to the xml node: " + path);
        }
    }



    //path must be on format /data/startdate
    public Node getXmlNode(Document xml, String path){
        logger.debug("get Xml Node for XML path: "+path);

        Node node=null;

        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(path);
            Object result = expr.evaluate(xml, XPathConstants.NODESET);
            NodeList nodeToGetList = (NodeList) result;
            logger.debug("Size of the listNode for the requested XmlPath = "+ nodeToGetList.getLength());
            if (nodeToGetList.getLength()==0){
                logger.debug("Not able to find any xml node by path: "+ path);
                return node;
            }
            node = nodeToGetList.item(0);
            return node;

        } catch (XPathExpressionException | NullPointerException e ) {
            logger.debug("Not able to proceed to the xml node: "+ path);
            return node;
        }
    }

    //if node not found - getJSONnodeValue returns "NodeNotFound" as node value
    public String getXmlNodeValue(Document xml, String path) {

        logger.debug("get Xml Node value: "+path);

            Node node = getXmlNode(xml,path);

        if (node==null){
            logger.debug("Not able to proceed to the xml node: "+ path);
            throw new RuntimeException("Not able to proceed to the xml node: "+ path);
        }
            else {
            logger.debug("Node found, get its value");
            return node.getTextContent();

        }
    }

/*
        public String getXmlNodeAttributeValue(Node node, String atr) {
            logger.debug("get Xml Node attribute value: " + node);
            if (node == null)
                return "";
            else {
                return node.getAttributes().getNamedItem("atr").getTextContent();
            }
        }

        */


    //method to convert Dom Document to String
    public String createStringFromDocument(Document doc)
    {
        try
        {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch(TransformerException ex)
        {
            logger.debug("Not able to convert Dom Document to String");;
            return "";
        }
    }

    //isXmlNodeExist

    public Boolean isXmlNodeExist(Document xml, String path){
        return getXmlNode(xml,path)!=null;
    }







}
