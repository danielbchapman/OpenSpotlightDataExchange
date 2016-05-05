package com.danielbchapman.fx.builders;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.util.StringConverter;

import com.danielbchapman.international.MessageUtility;
import com.danielbchapman.international.MessageUtility.Instance;
import com.danielbchapman.text.Text;
import com.danielbchapman.utility.Utility;



/**
 * Methods from https://github.com/danielbchapman/FxApplication
 * 
 * This maintains licenses based in the 
 * OpenSpotlightDataExchange project, not FxApplication
 */
public class Fx
{
  public final static String PATTERN_INTEGER = "^[0-9]";
  public final static String PATTERN_DECIMAL = "^[0-9,.\\-]";
  public final static String PATTERN_DATE = "^[0-9\\/]";
  
  public final static String FORMAT_INTEGER = ",###";
  public final static String FORMAT_DECIMAL = ",###.###]";
  public final static String FORMAT_DATE = "MM/dd/yy";
  
  public final static Instance MSG = MessageUtility.getInstance(Fx.class);
  private final static Logger log = Logger.getLogger(Fx.class.getName());
  
  public static TextField promptText(String s)
  {
    TextField f = new TextField();
    f.setPromptText(s);
    return f;
  }
  
  public static TextField promptText(String value, String prompt)
  {
    TextField f = promptText(prompt);
    if(value != null)
      f.setText(value);
    return f;
  }
  
  public static TextArea promptArea(String text)
  {
    TextArea area = new TextArea();
    area.setPromptText(text);
    return area;
  }
  
  public static TextArea promptArea(String value, String prompt)
  {
    TextArea ret = promptArea(prompt);
    if(value != null)
      ret.setText(value);
    return ret;
  }
  
  public static Label label(String text)
  {
    Label ret = new Label(text);
    ret.setStyle("-fx-background-color: yellow;");
    return ret;
  }
  
  /**
   * A placeholder method to apply styles to 
   * any labels that need to be made.
   * @param text the text
   * @return a new label with the proper CSS class  
   */
  public static Label labelFor(String text)
  {
    return new Label(text);
  }
  public static HBox hbox(final Node ... nodes)
  {
    HBox box = new HBox();
    box.getChildren().addAll(nodes);
    return box;
  }
  
  public static HBox hbox(double width, Node ... nodes)
  {
    HBox ret = hbox(nodes);
    ret.setMaxWidth(width);
    return ret;
  }
  
  public static TilePane group(int prefColumns, Insets padding, Insets margin, Node ... nodes)
  {
    TilePane ret = new TilePane();
    ret.setPrefColumns(prefColumns);
    ret.getChildren().addAll(nodes);
    
    if(padding != null)
      ret.setPadding(padding);
    
    for(Node n : nodes)
      if(margin != null)
        TilePane.setMargin(n, margin);
    
    return ret; 
  }
  
  public static TilePane group(Node ... nodes)
  {
    int columns = nodes.length;
    return group(columns, null, null, nodes);
  }
  
  public static TilePane group(int col, Node ... nodes)
  {
    return group(col, null, null, nodes);
  }
  
  public static TextField input()
  {
    return new TextField();
  }
  
  public static TextField input(String value)
  {
    if(value == null)
      return input();
    
    return new TextField(value);
  }
  
  public static TextArea area()
  {
    return new TextArea();
  }
  
  public static TextArea area(String value)
  {
    if(value == null)
      return area();
    
    return new TextArea(value);
  }
  
  public static TextField prompt(String prompt)
  {
    TextField text = input();
    text.setPromptText(prompt);
    return text;
  }
  
  public static <T> ComboBox<T> comboBoxEditable(String prompt, List<T> values)
  {
    ObservableList<T> list = FXCollections.observableList(values);
    ComboBox<T> box = new ComboBox<T>(list);
    box.setPromptText(prompt);
    box.setEditable(true);
    return box;
  }
  
  public static <T> ComboBox<T> comboBox(List<T> values)
  {
    ObservableList<T> list = FXCollections.observableList(values);
    ComboBox<T> box = new ComboBox<T>(list);
    box.setEditable(true);
    return box;
  }
 
  @SuppressWarnings("this feels like a hot mess, I think the Observable shoudl be doing this work")
  public static <S,T> TableColumn<S, T> column(Class<T> clazz, String fieldName)
  {
	  //Reflect for types
	  Method getter = null;
	  Method setter = null;
	  Field field = null;
	  
	  Method[] methods = clazz.getMethods();
	  Field[] fields = clazz.getFields();
	  
	  String capitalized = Text.capitalize(fieldName);
	  
	  String get = "get" + capitalized;
	  String set = "set" + capitalized;
	  
	  //This could use with better detection
	  for(Method m : methods)
	  {
		  int params = m.getParameterCount();
		  if(params == 0 && Utility.equalsNullSafe(m.getName(), get))
			  getter = m;
		  else if(params == 1 && Utility.equalsNullSafe(m.getName(), set))
			  setter = m;
	  }
	  
	  for(Field f : fields)
	  {
		  if(f.getName().equals(fieldName))
		  {
			  field = f;
			  break;
		  }
	  }
	  
	  TableColumn<S, T> col = new TableColumn<>(fieldName);
	  
	   
	  if(getter == null )
	  {
		  col.setCellValueFactory(new PropertyValueFactory<S, T>(fieldName)); 
		  return col; //Can't edit this column...
	  }
	  
	  Class<?> type = getter.getReturnType();
	  boolean knownType = (
			  type == Integer.class ||
			  type == Float.class ||
			  type == Double.class ||
			  type == BigDecimal.class ||
			  type == Date.class ||
			  type == Boolean.class ||
			  type == String.class
			);
	  
	  if(!knownType)
	  {
		  col.setCellValueFactory(new PropertyValueFactory<S, T>(fieldName)); 
		  return col;
	  }
	  
//	  Callback<CellDataFeatures<S, T>, ObservableValue<T>> edit = (cell -> {});
	  final Method fGetter = getter;
	  final Method fSetter = setter;
	  	  
	  
	  col.setCellValueFactory(
			  cell -> 
			  {
			 	Object val = null;
				try 
				{
					val = (T) fGetter.invoke(cell.getValue());
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					val = "";
				}
				if(val == null)
					return (ObservableValue<T>) new ReflectedObservableValue("", cell.getValue(), fSetter);
				else
					return (ObservableValue<T>) new ReflectedObservableValue(val.toString(),cell.getValue(), fSetter);
			  });
	  
  		col.setCellFactory(TextFieldTableCell.forTableColumn( 
  			new StringConverter<T>(){

			@Override
			public String toString(T object) {
				return object.toString();
			}

			@Override
			public T fromString(String string) {
				return (T) string;
		}}));
  		
  		col.setOnEditCommit(evt -> 
  		{
  			System.out.println(">>" + evt.getOldValue() +", " + evt.getNewValue() + "<<");
  			col.setEditable(true);
  			
  		});

  		//FIXME This feels wrong, I think that the property is not set up correctly.
  		if(setter != null)
  		{
  			col.setEditable(true);
  			col.setOnEditCommit( 
  				edit -> 
  				{
  					System.out.println("COMMIT>>" + edit.getOldValue() + ", " + edit.getNewValue() + "<<");
  					int row = edit.getTablePosition().getRow();
  					ObservableValue<T> cell = col.getCellObservableValue(row);
  					if(cell instanceof WritableValue)
  					{
  						((WritableValue<T>) cell).setValue(edit.getNewValue());
  					}
  				});
  		}
//	  col.setCellValueFactory(
//		  cell -> 
//		  {
//			  return new Simple(getter.invoke(cell.getValue()));
//		  });

	  
//	  col.setCe
	  return col;
  }

  public static <T> TableView<T> table(Class<T> clazz, String ... fields)
  {
	  log.info("Building table for " + clazz + " for fields " + fields);
	  TableView<T> table = new TableView<>();
	  
	  if(fields == null || fields.length < 1)
	  {
		  HashSet<String> props = new HashSet<>();
		  Method[] methods = clazz.getMethods();
		  
		  //Clear properties
		  for(Method m : methods)
		  {
			  String name = m.getName();
			  if(name.startsWith("get") || name.startsWith("set"))
			  {
				if(!Text.containsIgnoreCase(name, "property", "class"))
				{
					name = name.substring(3, name.length());
					props.add(name);
					log.info("adding prop: " + name);
				}
			  }
		  }
		  
		  ArrayList<String> sorted = Utility.list(props);
		  Collections.sort(sorted);
		  
		  for(String s: sorted)
		  {
			  log.info("AUTO Building column: " + s);
			  TableColumn<T,?> col = column(clazz, s);
			  table.getColumns().add(col);
		  }
	  }
	  else
	  {
		  for(String s : fields)
		  {
			  log.info("Building column: " + s);
			  TableColumn<T,?> col = column(clazz, s);
			  table.getColumns().add(col);
		  }  
	  }
	  
	  table.setEditable(true);
	  return table;
  }
  
  public static <T> TableColumn<T, Consumer<T>> columnAction(
		  final String name, 
		  final String buttonLabel, 
		  final Consumer<T> proc)
  {
	  TableColumn<T, Consumer<T>> column = new TableColumn<T, Consumer<T>>(name);
	  
	  column.setCellFactory( 
		  cell -> 
		  {
			  return new TableCell<T, Consumer<T>>()
			  {{
				  HBox box = new HBox();
			        Button button = new Button(buttonLabel);
			        button.setOnAction(
		        		on -> 
		        		{
		        			this.getTableView().getSelectionModel().select(getIndex());
		        			T target = this.getTableView().getSelectionModel().getSelectedItem();
		        			proc.accept(target);
		        		});;
			        box.getChildren().add(button);
			        setGraphic(box);
			  }};
		  });

	  return column;
  }
  
  public static class ReflectedObservableValue<T> implements ObservableValue, WritableValue
  {
	  private Method set;
	  private Object instance;
	  
	  public ReflectedObservableValue(T t, Object instance, Method set)
	  {
		  this.t = t;
		  this.instance = instance;
		  this.set = set;
	  }
	  private T t;

	  @Override
	  public void addListener(InvalidationListener listener) {
		  // TODO Auto-generated method stub
	  }

	  @Override
	  public void removeListener(InvalidationListener listener) {
		  // TODO Auto-generated method stub
	  }

	  @Override
	  public Object getValue() {
		  System.out.println("GET>>" + t + "<<");
		  return t;
	  }

	  @Override
	  public void addListener(ChangeListener listener) {
		  // TODO Auto-generated method stub

	  }
	  @Override
	  public void removeListener(ChangeListener listener) {
		  // TODO Auto-generated method stub

	  }

	  @Override
	  public void setValue(Object value) {
		  System.out.println("SET>>" + value + "<<");
		  t = (T) value;
		  try {
			set.invoke(instance, value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
  }
}

