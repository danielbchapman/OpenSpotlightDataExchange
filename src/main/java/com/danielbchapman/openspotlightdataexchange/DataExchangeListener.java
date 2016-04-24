package com.danielbchapman.openspotlightdataexchange;

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
public abstract class DataExchangeListener
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
  public DataExchangeListener()
  {
    this(DataExchangeMethods.defaultMappings());
  }
  
  /**
   * Construct a listener with a specific map
   * @param mappings
   */
  public DataExchangeListener(DataMappings mappings)
  {
    if(mappings == null || mappings.isEmpty())
      throw new IllegalArgumentException("Mappings can not be empty for this listener!");
    this.mappings = mappings;
  }
  
  /**
   * @param data the data to synchronize
   * @param time the time of the synchronization
   * @return a result object that specifies what happened on this synchronization
   */
  public abstract SynchronizationResult notifyDataIncoming(SpotlightData data, long time);
}
