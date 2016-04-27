package com.danielbchapman.openspotlightdataexchange;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystemException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.danielbchapman.code.NotImplementedException;
import com.danielbchapman.utility.Utility;
import com.danielbchapman.utility.UtilityText;
import com.danielbchapman.utility.Xml;

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
  /**
   * @return the current stimestamp for this format
   */
  public static String currentTimestamp()
  {
    return new SimpleDateFormat("YYYYMMddHHmmss").format(new Date());
  }
  
  public static ErrorCode updateNode(final Node node, SpotlightData original, SpotlightData update, String appStamp, String timeStamp, DataMappings mapping)
  {
    NodeList children = node.getChildNodes();
    HashMap<String, String> data = new HashMap<>();
    
    HashSet<String> keysUsed = new HashSet<String>();
    
    Consumer<Node> removeChild = (n)->
    {
      //Maybe clear some whitespace when we're done?
      node.removeChild(n);
      if(n.getNodeType() == Node.TEXT_NODE)
        System.out.println("Removing " + "TEXT_NODE" + " " + n.getNodeName());
      else
        System.out.println("Removing " + n.getNodeValue() + " " + n.getNodeName());
    };
    
    Node action = null;
    boolean performUpdate = false;
    int count = children.getLength();
    for(int i = 0; i < count; i++)
    {
      Node child = children.item(i);
      
      if(child.getNodeType() == Node.TEXT_NODE)
      {
        removeChild.accept(child);
        i--;
        count--;
        continue;
      }
      
      String text = child.getTextContent();
      String name = child.getNodeName();
      keysUsed.add(name);
      switch(name)
      {
        case "Action":
          action = child;
          child.setTextContent(""); //Remove the action
          break;
        case "AppStamp":
          child.setTextContent(appStamp);
          break;
        case "TimeStamp":
          child.setTextContent(timeStamp);
          break;
        case "UID":
          //ignore
          break;
        case "Accessories": 
          //FIXME Implement accessories
          //REMOVE for now
          node.removeChild(child);
          i--;
          count--;
          break;
        default:
        {
          String map = mapping.get(name);
          if(map != null)
          {
            String a = original.get(map);
            String b = update.get(map);
            if(Utility.compareToNullSafe(a, b) == 0)
            {
              removeChild.accept(child);
              i--;
              count--;
            }
            else
            {
              performUpdate = true;
              child.setTextContent(b);
            }
          }
          else
          {
            //This isn't mapped so remove it (we "consume it")
            removeChild.accept(child);
            i--;
            count--;
          }
          break;
        }
      }
    }
    
    //Add new or missing nodes
    HashMap<String, String> diff = original.diff(update, keysUsed);
    
    for(String key : diff.keySet())
    {
      String k = Utility.ifEmpty(mapping.get(key), key);
      Node newElement = node.getOwnerDocument().createElement(k);
      newElement.setTextContent(diff.get(k));
      node.appendChild(newElement);
      performUpdate = true;
    }
    
    if(performUpdate) //If we need to perform an update
    {
      if(action == null)
      {
        action = node.getOwnerDocument().createElement("Action");
        node.appendChild(action);
      }
      
      action.setTextContent("Update2");
    }
    else
    {
      if(action != null)
        node.removeChild(action);
    }
    
    return ErrorCode.SUCCESS; //FIXME, actually check for errors.
  }
  
  public static SpotlightData speedyImporter(Node node, String currentTime, DataMappings mapping)
  {
    String action = null;
    String appStamp = null;
    String timeStamp = null;
    String uid = null;
    
    NodeList children = node.getChildNodes();
    HashMap<String, String> data = new HashMap<>();
    
    for(int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if(child.getNodeType() != Node.ELEMENT_NODE)
        continue;
      
      String text = child.getTextContent();
      String name = child.getNodeName();
      switch(name)
      {
        case "Action":
          action = text;
          break;
        case "AppStamp":
          appStamp = text;
          break;
        case "TimeStamp":
          timeStamp = text;
          break;
        case "UID":
          uid = text;
          break;
        case "Accessories":
          //FIXME we need to parse in accessories here.
          break;
        default:
        {
          String map = mapping.get(name);
          if(map == null){
            //System.err.println(String.format("Unable to map %s", name));
            break;
          }
          
          data.put(map, text);
        }
      }
    }

    SpotlightData result = new SpotlightData(action, uid, timeStamp, appStamp, data);   
    return result;
  }
  
  public static SpotlightData defaultMapper(Node node)
  {
    String action = Xml.text(node, "Action");
    String appStamp = Xml.text(node, "AppStamp");
    String timeStamp = Xml.text(node, "TimeStamp");
    String uid = Xml.text(node, "UID");
    
    SpotlightData result = new SpotlightData(action, uid, timeStamp, appStamp);
    return result;
  }
  
  public static void importAction(Document xml, DataMappings map, DataExchangeProcessor processor)
  {
    final String current = currentTimestamp();
    Function<Node, SpotlightData> importer = n -> DataExchangeMethods.speedyImporter(n, current, map);
    importAction(xml, processor);
  }
  
	public static void importAction(Document xml, DataExchangeProcessor processor)
	{ 
	  Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	  utc.setTime(new Date());
	  String importTime = new SimpleDateFormat("YYYYMMddHHmmss").format(utc.getTime());
	  
	  Node appStamp = Xml.node(xml, "/SLData/InstrumentData/AppStamp");
	  Node action = Xml.node(xml, "/SLData/InstrumentData/Action");
	  Node vwVersion = Xml.node(xml, "/SLData/InstrumentData/VWVersion");
	  Node vwBuild = Xml.node(xml, "/SLData/InstrumentData/VWBuild");
	  Node rotate2D = Xml.node(xml, "/SLData/InstrumentData/AutoRot2D");
	  
	  //LOGS
	  System.out.println("AppStamp: " + appStamp.getTextContent());
	  System.out.println("Action: " + action.getTextContent());
	  System.out.println("VWVersion: " + vwVersion.getTextContent());
	  System.out.println("VWBuild: " + vwBuild.getTextContent());
	  System.out.println("AutoRot2D: " + rotate2D.getTextContent());
	  System.out.println("--------------------");
	  
	  NodeList list = Xml.nodeList(xml, "/SLData/InstrumentData/*[starts-with(name(),'UID')]");
	  if(list == null)
	    throw new RuntimeException("Unable to import document, the list is null");
	  
	  ErrorCode code = ErrorCode.UNKNOWN;
	  for(int i = 0; i < list.getLength(); i++)
	  {
	    Node node = list.item(i);
	    ErrorCode c = processor.execute(node);
	    
	    if(c != ErrorCode.SUCCESS)
	      code = c;
	  }
	  
	  //System.out.println(UtilityXml.printXml(xml));
	  System.out.println(String.format("In Error? %s", code.toString()));
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
