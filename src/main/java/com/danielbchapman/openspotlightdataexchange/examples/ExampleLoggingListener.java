package com.danielbchapman.openspotlightdataexchange.examples;

import com.danielbchapman.openspotlightdataexchange.DataExchangeListener;
import com.danielbchapman.openspotlightdataexchange.SpotlightData;
import com.danielbchapman.openspotlightdataexchange.SynchronizationResult;

public class ExampleLoggingListener extends DataExchangeListener
{

  @Override
  public SynchronizationResult notifyDataIncoming(SpotlightData data, long time)
  {
    System.out.println(String.format("[READ] Incomming Spotlight Data @%ld", time));
    System.out.println(data);
    return new SynchronizationResult(true, null); //no updates
  }

}
