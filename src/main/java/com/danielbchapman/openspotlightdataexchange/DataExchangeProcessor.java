package com.danielbchapman.openspotlightdataexchange;

import org.w3c.dom.Node;

import lombok.Getter;
import lombok.Setter;

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
    SpotlightData data = importSLData(node);
    SpotlightData post = process(data, node);
    
    if(post != null)
    {
      return DataExchangeMethods.updateNode(node, data, post, "JUnit", DataExchangeMethods.currentTimestamp(), mappings);
    }
    else
      return ErrorCode.FAILURE; 
    
  }
  /**
   * This method imports the node value, currently this
   * just uses the "speedy import" method
   * @param node the node to process
   * @return this method imports
   */
  public SpotlightData importSLData(Node node)
  {
    return DataExchangeMethods.speedyImporter(node, DataExchangeMethods.currentTimestamp(), mappings);
  }
  
  /**
   * The process method does the actual mapping, if you have a custom
   * implementation this is the method you want to override. The Node is
   * provided as a courtesy to the signature, but is actually not needed
   * for this particular implementation. This could occur entirely
   * in the execute method.
   *  
   * 
   * @param value the SpotlightData to process
   * @param node the node that was used to obtain the data if needed
   * @return A new SpotlightDataObject indicating the changes to this 
   * node after processing
   */
  public SpotlightData process(SpotlightData value, Node node)
  {
    //Just append a 1 to purpose each time this runs.
    SpotlightData out = value.copyValues();
    if(out.get("UnitNumber").equals("1"))
      out.set("Purpose", "Unit #1 UPDATED");
    return out;
  }
}
