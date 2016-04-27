package com.danielbchapman.openspotlightdataexchange;

import java.io.File;

import org.junit.Test;
import org.w3c.dom.Document;

import com.danielbchapman.utility.FileUtil;
import com.danielbchapman.utility.Xml;

public class CreateTestFiles
{
  @Test
  public void Setup0TestEnvironment()
  {
    FileUtil.rmDir("volatile");
    FileUtil.copyDir("test/exchange/", "volatile/");
  }
}
