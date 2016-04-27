package com.danielbchapman.openspotlightdataexchange;

import java.util.function.BiFunction;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.danielbchapman.utility.Xml;

public class TestExchangeMethods
{
   @Test
   public void DataMergeTest() throws ParserConfigurationException
   {
     final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
     
     BiFunction<String, String, Node> make = (n, v)->
     {
       Node out = doc.createElement(n);
       out.appendChild(doc.createTextNode(v));
       return out;
     };
     
     Node node = doc.createElement("Test");
     node.appendChild(make.apply("CANARY", "DO NOT CHANGE ME"));
     
     SpotlightData original= new SpotlightData("Update", "1.0.0.1", DataExchangeMethods.currentTimestamp(), "Vectorworks");
     SpotlightData update = new SpotlightData("Update", "1.0.0.1", DataExchangeMethods.currentTimestamp(), "JUnit");
     
     original.set("Purpose", "Old Purpose");
     update.set("Purpose", "New Purpose");
     
     original.set("Position", "STATIC POSITION");
     update.set("Position", "STATIC POSITION");
     
     DataExchangeMethods.updateNode(node, original, update, "JUnit", DataExchangeMethods.currentTimestamp(), DataExchangeMethods.defaultMappings());
     System.out.println(Xml.printXml(node));
   }
}
