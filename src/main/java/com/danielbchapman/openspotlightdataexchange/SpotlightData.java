package com.danielbchapman.openspotlightdataexchange;

import java.util.HashMap;
import java.util.Map;

public class SpotlightData
{
	private HashMap<String, String> data = new HashMap<>();
	
	public SpotlightData()
	{	
	}
	
	public SpotlightData(Map<String, String> data)
	{
		if(data != null)
		{
			for(String s : data.keySet())
				this.data.put(s,  data.get(s));
		}
	}
	
	public String get(String key)
	{
		return data.get(key);
	}
	
	public void set(String key, String value)
	{
		data.put(key, value);
	}
}
