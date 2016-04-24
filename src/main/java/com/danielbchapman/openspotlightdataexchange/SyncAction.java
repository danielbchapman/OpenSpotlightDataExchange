package com.danielbchapman.openspotlightdataexchange;

/**
 * A simple enum listing the known actions. The listener needs to respond to each.
 * @author danielbchapman
 *
 */
public enum SyncAction
{
  ENTIRE_PLOT("Entire Plot");
  
  String name;
  SyncAction(String name)
  {
    this.name = name;
  }
  
  public String toString()
  {
    return name;
  }
}
