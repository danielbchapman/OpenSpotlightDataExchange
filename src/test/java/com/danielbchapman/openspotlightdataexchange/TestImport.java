package com.danielbchapman.openspotlightdataexchange;

import java.io.File;

import org.junit.Test;
import org.w3c.dom.Document;

import com.danielbchapman.utility.FileUtil;
import com.danielbchapman.utility.UtilityXml;

public class TestImport
{
  @Test
  public void TestDataSpoolUp()
  {
    Document xml = UtilityXml.readDocument(new File("test/turco-test.xml"));
    DataMappings mapping = DataExchangeMethods.defaultMappings();
    
    long start = System.currentTimeMillis();
    DataExchangeMethods.importAction(xml, new DataExchangeProcessor());
    long end = System.currentTimeMillis();
    System.out.println("SPOOL UP FOR TESTS-------------RUN TIME IS: " + (end - start));     
  }
  
  @Test
  public void TestDataImportMethod()
  {
    Document xml = UtilityXml.readDocument(new File("test/turco-test.xml"));
    DataMappings mapping = DataExchangeMethods.defaultMappings();
    
    long start = System.currentTimeMillis();
    DataExchangeMethods.importAction(xml, new DataExchangeProcessor());
    long end = System.currentTimeMillis();
    System.out.println("DOM BASED-------------RUN TIME IS: " + (end - start));  
  }
  
  public void SetupTest()
  {
    FileUtil.copyDir("test/exchange/", "volitile/");
  }
  public void AttemptTest()
  {
    
  }
}
