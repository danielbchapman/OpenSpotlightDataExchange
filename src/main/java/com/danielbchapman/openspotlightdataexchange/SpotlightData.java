package com.danielbchapman.openspotlightdataexchange;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.danielbchapman.text.Text;
import com.danielbchapman.utility.Utility;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class SpotlightData
{
	private HashMap<String, String> mappable = new HashMap<>();
	//FIXME Add in accessories to this 
	@Getter
	private String uid;
	@Getter
	@Setter
	private String timeStamp;
	@Getter
	@Setter
	private String appStamp;
	@Getter
	@Setter
	private String action;
	
	public SpotlightData(String uid)
	{
	  this.uid = uid;
	}
	
	public SpotlightData(String action, String uid, String timeStamp, String appStamp)
	{
	  this(uid);
	  this.action = action;
	  this.timeStamp = timeStamp;
	  this.appStamp = appStamp;
	}
	
	public SpotlightData(String action, String uid, String timeStamp, String appStamp, Map<String, String> data)
	{
	  this(action, uid, timeStamp, appStamp);
		if(data != null)
		{
			for(String s : data.keySet())
				this.mappable.put(s,  data.get(s));
		}
	}
	
	public String get(String key)
	{
		String ret = mappable.get(key);
		
		if(!Text.isEmpty(ret))
		  return ret.trim();
		else
		  return null;
	}
	
	public void set(String key, String value)
	{
	  mappable.put(key, value);
	}
	
	public Set<String> getKeys()
	{
	  return mappable.keySet();
	}

	/**
	 * @param other the other data to diff against
	 * @param exclude a collection to exclude from this comparison
	 * @return a HashMap containing all the differences in the mappable 
	 * values where the OTHER takes priority.
	 */
	public HashMap<String, String> diff(SpotlightData other, Collection<String> exclude)
	{
	  HashMap<String, String> ret = new HashMap<>();
	  
	  HashSet<String> allKeys = new HashSet<String>();
	  getKeys().stream().forEach(s-> allKeys.add(s));
	  allKeys.addAll(other.getKeys());
	  
	  if(exclude != null)
	    allKeys.removeAll(exclude);
	  
	  for(String key : allKeys)
	  {
	    String a = get(key);
	    String b = other.get(key);
	    if(Utility.compareToNullSafe(a, b) != 0)
	      ret.put(key, b);
	  }
	  
	  return ret;
	}
	
	public SpotlightData copyValues()
	{
	  SpotlightData copy = new SpotlightData(this.uid);
	  copy.action = "";
	  copy.appStamp = "";
	  copy.timeStamp = "";
	  
	  mappable
	    .entrySet()
	    .stream()
	    .forEach(e -> copy.set(e.getKey(),  e.getValue()));
	  
	  return copy;
	}
}
