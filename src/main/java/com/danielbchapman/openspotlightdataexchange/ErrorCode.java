package com.danielbchapman.openspotlightdataexchange;

/**
 * A simple enum to indicate whatever errors we find
 * for the imports. This feature is entirely undocumented and
 * it makes sense to use error codes rather than exceptions for 
 * flow control. My guess is that reconciliation will be simple in
 * the future.
 * 
 * This may eventuall be refactored into a try/catch framework.
 * 
 * @author danielbchapman
 */
public enum ErrorCode
{
  SUCCESS,
  FAILURE,
  UNKNOWN,
  UNSET
}
