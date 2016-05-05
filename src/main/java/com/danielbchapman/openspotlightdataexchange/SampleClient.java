package com.danielbchapman.openspotlightdataexchange;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import org.w3c.dom.Document;

import com.danielbchapman.fx.builders.Fx;
import com.danielbchapman.logging.Log;
import com.danielbchapman.utility.Utility;
import com.danielbchapman.utility.Xml;

public class SampleClient extends javafx.application.Application
{
  Stage stage;
  File xml;
  VBox root;
  TableView<SpotlightData> table;
  
  DataExchangeInstance instance;
  DataExchangeProcessor processor;
  
  @SuppressWarnings("unchecked")
  @Override
  public void start(Stage stage) throws Exception
  {
    table = Fx.table(SpotlightData.class);
    //Add some useful columns
    table.getColumns().addAll(
        this.columnMap("Channel", "Channel")
        );
    
    table.setEditable(true);
    processor = new DataExchangeProcessor(DataExchangeMethods.defaultMappings(), null);
    processor.setOnNodeProcessed(
      (sl)->
      {
        Log.info("ON PROCESS" + sl.toString());
      });
    
    processor.setOnListProcessed(()-> 
    {
      final ArrayList<SpotlightData> data = processor.getData();
      data.sort((a, b) -> 
      {
        return Utility.compareToNullSafe(a.getUid(), a.getUid());
      });
      
      Platform.runLater(()->
      {
        table.getItems().clear();
        table.getItems().addAll(data);
      });
    });
    this.stage = stage;
    root = new VBox();
    root.getChildren().add(table);
    
    stage.setScene(new Scene(root));
    stage.show();
    openAndStart();
  }
  
  public void openAndStart()
  {
    FileChooser open = new FileChooser();
    open.setInitialDirectory(new File("."));
    open.getExtensionFilters().add(new ExtensionFilter("*.xml", "*.xml"));
    File source = open.showOpenDialog(stage.getOwner());
    
    if(source == null)
    {
     alert("No data!", "You must choose a file to proceed");
     openAndStart(); //Terrible, this should have a cancel/exit
    }
    new Thread()
    {
      public void run()
      {
        try
        {
          instance = new DataExchangeInstance(DataExchangeMethods.defaultMappings(), source);
          instance.start(f -> process(f));
        }
        catch (IOException e)
        {
          
          alert("Error trying to process!", e.getMessage());
          e.printStackTrace();
          System.exit(-1);
        }
      }
    }.start();
  }
  
  public void process(File file)
  {
    Document xml = Xml.readDocument(file);
    if(xml == null)
    {
      alert("Unable to read file!", "The file " + file + " could not be read.");
      return;
    }
      
    Platform.runLater(
      () -> 
      {
        DataExchangeMethods.importAction(xml, processor);
        processor.getOnListProcessed().run();
      });
  }
  public void alert(String title, String message)
  {
    Alert x = new Alert(AlertType.ERROR);
    x.setTitle(title);
    x.setHeaderText(title);
    x.setContentText(message);
    
    x.showAndWait();
  }

  public static void main(String ... args)
  {
    Application.launch(SampleClient.class);
  }
  
  
  public TableColumn<SpotlightData, String> columnMap(String name, String key)
  {
    TableColumn<SpotlightData, String> col = new TableColumn<>(name);
    
    col.setCellValueFactory(
        cell -> 
        {
          return new SimpleValue<SpotlightData, String>(
              ()->
              {
                return cell.getValue().get(key);
              }, 
              (v)->
              {
                cell.getValue().set(key, v);  //never really called      
              });
        });
    
    col.setCellFactory(TextFieldTableCell.forTableColumn());
    col.setEditable(true);
    col.setOnEditCommit(evt -> 
    {
      System.out.println(">>" + evt.getOldValue() +", " + evt.getNewValue() + "<<");
      evt.getRowValue().set(key, evt.getNewValue());
    });
    return col;
  }
  
  public static class SimpleValue<Source, T> implements ObservableValue<T>, WritableValue<T>
  {
    Supplier<T> get;
    Consumer<T> set;
    
    HashSet<InvalidationListener> invalidations = new HashSet<>();
    HashSet<ChangeListener<?>> changes = new HashSet<>();
//    Supplier<Source> source;
    public SimpleValue(Supplier<T> get, Consumer<T> set)
    {
//      this.source = source;
      this.get = get;
      this.set = set;
    }

    @Override
    public void addListener(InvalidationListener i)
    {
      invalidations.add(i);
    }

    @Override
    public void removeListener(InvalidationListener i)
    { 
      invalidations.remove(i);
    }

    @Override
    public void setValue(T t)
    {
      System.out.println("SET->" + t);
      set.accept(t);
    }

    @Override
    public void addListener(ChangeListener c)
    {
      changes.add(c);
    }

    @Override
    public T getValue()
    {
      return get.get();
    }

    @Override
    public void removeListener(ChangeListener c)
    { 
      changes.remove(c);
    }
    
  }

}
