package com.danielbchapman.openspotlightdataexchange;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.function.Consumer;

import com.danielbchapman.utility.Xml;

import lombok.Getter;
import lombok.Setter;


/**
 * The Exchange instance targets a specific file
 * and listens for changes. It also provides method to 
 * consume SpotlightData changes and will write to the file
 * until it is consumed.
 * 
 * It is intended to be used as a thread.
 */

public class DataExchangeInstance
{
  public static void main(String ... args)
  {
    try
    {
      DataExchangeInstance inst = new DataExchangeInstance(DataExchangeMethods.defaultMappings(), new File("volatile/test.xml"));
      inst.registerKillThread(120000);
      inst.start((f)->{});
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  @Getter
  File file;
  @Getter
  boolean isRunning = false;
  
  long lastUpdated = -1L;
  
  @Getter
  @Setter
  DataMappings mappings;
  private Path path;
  
  private WatchService watch;
  
  public DataExchangeInstance(DataMappings mappings, File file) throws IOException
  {
    this.file = file;
    this.path = file.toPath(); 
    this.watch = FileSystems.getDefault().newWatchService();
  }
  
  public void registerKillThread(long duration)
  {
    new Thread(()->
    {
      try
      {
        Thread.sleep(duration);  
      }
      catch(Throwable t)
      {
        t.printStackTrace();
      }
      
      System.out.println("KILLING THREAD");
      isRunning = false;
    }, 
    "Kill-" + duration/1000L).start();
  }
  
  public void shutdown()
  {
    isRunning = false;
  }
  
  /**
   * <JavaDoc>
   * @param onFileChanged <Return Description>  
   * 
   */
  public void start(final Consumer<File> onFileChanged)
  {

      isRunning = true;
      System.out.println("Registering service");
      Path dir = path.getParent();
      System.out.println(dir.toAbsolutePath());
      //Fire before we start
      onFileChanged.accept(file);
      while(isRunning)
      {
        try
        {
          WatchKey key = path.getParent().register(watch, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_CREATE);
          for(WatchEvent<?> e : key.pollEvents())
          {
            WatchEvent.Kind<?> kind = e.kind();
            System.out.println("Polling...");
            if(kind == StandardWatchEventKinds.OVERFLOW)
            {
              continue; //Ignore it
            }
            else
            {
              onFileChanged.accept(file);
            }
          }
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }

    System.out.println("Exiting Start");
  }
  
  /**
   * Stop this file listener
   */
  public void stopListener()
  {
    System.out.println("KILLING THREAD");
    isRunning = false;
  }
}