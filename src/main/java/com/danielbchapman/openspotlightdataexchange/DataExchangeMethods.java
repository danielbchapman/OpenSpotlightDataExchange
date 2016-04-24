package com.danielbchapman.openspotlightdataexchange;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.danielbchapman.code.NotImplementedException;
import com.danielbchapman.utility.UtilityXml;

public class DataExchangeMethods
{
  public static String APP_STAMP = "Lightwright";
  public static String APP_STAMP_VECTORWORKS = "Vectorworks";
  
  public static String ACTION_ENTIRE_PLOT = "Entire Plot";
  
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
  
	public static void importAction(Document xml, DataMappings mappings, DataExchangeListener listener)
	{
	  Node appStamp = UtilityXml.node(xml, "/SLData/InstrumentData/AppStamp");
	  Node action = UtilityXml.node(xml, "/SLData/InstrumentData/Action");
	  Node vwVersion = UtilityXml.node(xml, "/SLData/InstrumentData/VWVersion");
	  Node vwBuild = UtilityXml.node(xml, "/SLData/InstrumentData/VWBuild");
	  Node rotate2D = UtilityXml.node(xml, "/SLData/InstrumentData/AutoRot2D");
	  
	  System.out.println("AppStamp: " + appStamp.getTextContent());
	  System.out.println("Action: " + action.getTextContent());
	  System.out.println("VWVersion: " + vwVersion.getTextContent());
	  System.out.println("VWBuild: " + vwBuild.getTextContent());
	  System.out.println("AutoRot2D: " + rotate2D.getTextContent());
	  System.out.println("--------------------");
	  
	  NodeList list = UtilityXml.nodeList(xml, "/SLData/InstrumentData/*[starts-with(name(),'UID')]");
	  if(list == null)
	    throw new RuntimeException("Unable to import document, the list is null");
	  
	  for(int i = 0; i < list.getLength(); i++)
	  {
	    Node node = list.item(i);
	    System.out.println(node.toString());
	  }
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
