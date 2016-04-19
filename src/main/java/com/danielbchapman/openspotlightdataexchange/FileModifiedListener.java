package com.danielbchapman.openspotlightdataexchange;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * The FileModifiedListener class implements a thread that will
 * watch a specific data exchange file for changes and notify the 
 * application that changes have been made. This then executes an Exchange. 
 * 
 * All files are read to memory and then locked in a single transaction assuming 
 * the hash is the same. This prevents synchronization errors that are common with
 * this format.
 */
public class FileModifiedListener implements Runnable 
{
	private File automatedDataExchangeFile;
	
	public FileModifiedListener(File automatedDataExchangeFile) throws FileNotFoundException
	{
		if(automatedDataExchangeFile == null)
			throw new FileNotFoundException("The file is null");
		
		if(!automatedDataExchangeFile.exists())
			throw new FileNotFoundException(String.format("The file [%s] does not exist", automatedDataExchangeFile.toString()));
		
		this.automatedDataExchangeFile = automatedDataExchangeFile;	
	}
	
	public void run()
	{
		
	}

}
