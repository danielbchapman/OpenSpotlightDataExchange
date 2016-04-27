package com.danielbchapman.openspotlightdataexchange;

import java.io.File;

import org.junit.Test;
import org.w3c.dom.Document;

import com.danielbchapman.utility.UtilityXml;

public class RunTestSteps
{
  @Test
  public void Step1RunExport()
  {
    Document doc = UtilityXml.readDocument(new File("volatile/simple/simple.xml"));
    DataExchangeMethods.importAction(doc, new DataExchangeProcessor());
    //System.out.println(UtilityXml.printXml(doc));
    UtilityXml.writeToFile(doc, "volatile/simple/step1.xml");
  }
}
