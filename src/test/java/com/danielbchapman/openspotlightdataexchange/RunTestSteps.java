package com.danielbchapman.openspotlightdataexchange;

import java.io.File;

import org.junit.Test;
import org.w3c.dom.Document;

import com.danielbchapman.utility.Xml;

public class RunTestSteps
{
  @Test
  public void Step1RunExport()
  {
    Document doc = Xml.readDocument(new File("volatile/simple/simple.xml"));
    DataExchangeMethods.importAction(doc, new DataExchangeProcessor());
    //System.out.println(UtilityXml.printXml(doc));
    Xml.writeToFile(doc, "volatile/simple/step1.xml");
  }
}
