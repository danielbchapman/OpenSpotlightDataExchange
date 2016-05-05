package com.danielbchapman.openspotlightdataexchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

import org.w3c.dom.Node;

import com.danielbchapman.code.Pair;
import com.danielbchapman.logging.Log;
import com.danielbchapman.text.Text;

/**
 * The DataExchangeListener is a class that notifies an application of synchronization.
 * 
 * It is a blocking listener which means that it must complete all actions successfully in 
 * order to function properly. That means that this listener expects a single transaction that
 * can be rolled back in the application that is notified. 
 *   
 * @author danielbchapman
 *
 */
public class DataExchangeProcessor
{
  @Getter
  @Setter
  private DataMappings mappings;
  
  @Getter
  @Setter
  private String appStamp = "JUnit";
  
  /**
   * An event that is fired on each execute success (not 
   * fired on a failure)
   */
  @Getter
  @Setter
  private Consumer<SpotlightData> onNodeProcessed;
  
  /**
   * An event fired when the list is completed
   */
  @Getter
  @Setter
  private Runnable onListProcessed;
  /**
   * Maps the UID of the element to the data
   */
  @Getter
  private ConcurrentHashMap<String, SpotlightData> uidDataMap = new ConcurrentHashMap<>();
  /**
   * Constructs a Data Exchange Listener with default mappings.
   * 
   * You probably do not want to use this method, but it can be useful if 
   * you modify the mappings after construction.
   */
  public DataExchangeProcessor()
  {
    this(DataExchangeMethods.defaultMappings(), null);
  }
  
  /**
   * Construct a listener with a specific map
   * @param mappings
   */
  public DataExchangeProcessor(DataMappings mappings, Collection<SpotlightData> existingData)
  {
    if(mappings == null || mappings.isEmpty())
      throw new IllegalArgumentException("Mappings can not be empty for this listener!");
    this.mappings = mappings;
    
    if(existingData != null)
      existingData
        .stream()
        .filter(x -> x != null)
        .filter(x -> !Text.isEmptyOrNull(x.getUid()))
        .forEach(x -> uidDataMap.put(x.getUid(), x));
  }
  
  /**
   * @param node
   * @return 0 if there is no error, otherwise the error code.
   */
  public Pair<ErrorCode, SpotlightData> execute(Node node)
  {
    SpotlightData data = getNodeAsSpotlightData(node);
    
    //Process this node
    //FIXME This is unfinished
    SpotlightData out = data.copyValues();
    if(out.get("UnitNumber").equals("1")) //CANARY FOR TESTING
      out.set("Purpose", "Unit #1 UPDATED");
    
    if(out != null)
    {
      ErrorCode error = DataExchangeMethods.updateNode(node, data, out, "JUnit", DataExchangeMethods.currentTimestamp(), mappings);
      if(ErrorCode.SUCCESS == error && !Text.isEmptyOrNull(out.getUid()))
      {
        uidDataMap.put(out.getUid(), out);
        Log.info(String.format("Running against: %s", out.toString()));
      }
      else
        Log.info(String.format("Returning an error %s\n for SL Data: \n%s", error.toString(), out.toString()));
      
      return Pair.create(error, out);
    }
    else
      return Pair.create(ErrorCode.FAILURE, null); //This will eventually be checked.
    
  }
  
  /**
   * @return the data in this map  
   */
  public ArrayList<SpotlightData> getData()
  {
    ArrayList<SpotlightData> ret = new ArrayList<SpotlightData>();
    uidDataMap.forEach((k, v)-> ret.add(v));
    return ret;
  }
  /**
   * This method imports the node value, currently this
   * just uses the "speedy import" method
   * @param node the node to process
   * @return this method imports
   */
  public SpotlightData getNodeAsSpotlightData(Node node)
  {
    return DataExchangeMethods.speedyImporter(node, mappings);
  }
}
