package com.danielbchapman.openspotlightdataexchange;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemException;

import org.w3c.dom.Document;

import com.danielbchapman.code.NotImplementedException;
import com.danielbchapman.utility.UtilityXml;

public class DataExchangeMethods
{
  public static String APP_STAMP = "Lightwright";
  public static String APP_STAMP_VECTORWORKS = "Vectorworks";
  
  public static DataMappings defaultMappings()
  {
    DataMappings map = new DataMappings();
    map.put("Inst_Type", "Inst_Type");
    map.put("Device_Type", "Device_Type");
    map.put("Wattage", "Wattage");
    map.put("Purpose", "Purpose");
    map.put("Position", "Position");
    map.put("Unit_Number", "UnitNumber");
    map.put("Color", "Color");
    map.put("Dimmer", "Dimmer");
    map.put("Channel",  "Channel");
    map.put("Universe", "Universe");
    map.put("U_Dimmer", "U_Dimmer"); 
    
    //FIXME this is incomplete, but is enough to start.
    return map;
  }
  
  public static void syncDocument(File file, DataMappings mappings)
  {
    throw new NotImplementedException();
  }
  
	public static void importAction(Document xml, DataMappings mappings)
	{
	  
	}
	
	private static void checkPermissions(File file) throws FileNotFoundException, FileSystemException 
	{
	  if(!file.exists())
	    throw new FileNotFoundException(file.toString());
	  
	  if(!file.canWrite())
	    throw new FileSystemException(file.toString(), "unknown", "File can not be written to");
	  
	  if(!file.canRead())
      throw new FileSystemException(file.toString(), "unknown", "File can not be read from");
	}
}
