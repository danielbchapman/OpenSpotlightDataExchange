package com.danielbchapman.openspotlightdataexchange;

import java.io.File;

import org.junit.Test;
import org.w3c.dom.Document;

import com.danielbchapman.utility.UtilityXml;

public class TestExchangeMethods
{
  @Test
  public void TestDataImportMethod()
  {
    Document xml = UtilityXml.readDocument(new File("test/turco-test.xml"));
    DataMappings mapping = DataExchangeMethods.defaultMappings();
    
    DataExchangeMethods.importAction(xml, mapping);
  }
}
