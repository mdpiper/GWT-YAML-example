package edu.colorado.mpiper.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * A GWT JavaScript overlay (JSO) type that describes the JSON returned on a
 * HTTP GET call to components/list.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentListJSO extends JavaScriptObject {

  protected ComponentListJSO() {
  }    
  
  /**
   * Gets the JsArrayString of component ids.
   */
  public final native JsArrayString getComponents() /*-{
		return this;
  }-*/;

  /**
   * Returns a formatted String for display.
   */
  public final String toPrettyString() {
    return this.getComponents().join(", ");
  }
}
