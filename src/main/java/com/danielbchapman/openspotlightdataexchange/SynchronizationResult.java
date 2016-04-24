package com.danielbchapman.openspotlightdataexchange;

import lombok.Getter;

/**
 * A simple Pair that specifics if the synchronization
 * was accepted and if there are any amendments to
 * the synchronization that need to be sent to the other application.
 * 
 * @author danielbchapman
 */
public class SynchronizationResult
{
  @Getter
  private boolean success;
  @Getter
  private SpotlightData updates;
  
  public SynchronizationResult(boolean success, SpotlightData updates)
  {
    this.success = success;
    this.updates = updates;
  }
}
