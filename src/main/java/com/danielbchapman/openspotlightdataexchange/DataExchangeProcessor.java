package com.danielbchapman.openspotlightdataexchange;

import lombok.Getter;
import lombok.Setter;

import org.w3c.dom.Node;

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
   * Constructs a Data Exchange Listener with default mappings.
   * 
   * You probably do not want to use this method, but it can be useful if 
   * you modify the mappings after construction.
   */
  public DataExchangeProcessor()
  {
    this(DataExchangeMethods.defaultMappings());
  }
  
  /**
   * Construct a listener with a specific map
   * @param mappings
   */
  public DataExchangeProcessor(DataMappings mappings)
  {
    if(mappings == null || mappings.isEmpty())
      throw new IllegalArgumentException("Mappings can not be empty for this listener!");
    this.mappings = mappings;
  }
  
  /**
   * @param node
   * @return 0 if there is no error, otherwise the error code.
   */
  public ErrorCode execute(Node node)
  {
    SpotlightData data = getNodeAsSpotlightData(node);
    
    //Process this node
    SpotlightData out = data.copyValues();
    if(out.get("UnitNumber").equals("1")) //CANARY FOR TESTING
      out.set("Purpose", "Unit #1 UPDATED");
    
    if(out != null)
    {
      return DataExchangeMethods.updateNode(node, data, out, "JUnit", DataExchangeMethods.currentTimestamp(), mappings);
    }
    else
      return ErrorCode.FAILURE; //This will eventually be checked.
    
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
