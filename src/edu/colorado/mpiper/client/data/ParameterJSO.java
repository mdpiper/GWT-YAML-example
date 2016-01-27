package edu.colorado.mpiper.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * A GWT JavaScript overlay (JSO) type with methods for describing a single
 * parameter of a component, as well as the set of parameters that the component
 * defines.
 */
public class ParameterJSO extends JavaScriptObject {

  // Overlay types have protected, no-arg, constructors.
  protected ParameterJSO() {
  }

  /**
   * Count the number of parameters in a "parameters.json" file.
   * 
   * @return the parameter count, an int
   */
  public final native int nParameters() /*-{
		var keys = Object.keys(this);
		return keys.length;
  }-*/;

  /**
   * Get the names of the parameters in a "parameters.json" file.
   * 
   * @return the parameter names, a JsArrayString
   */
  public final native JsArrayString getParameterNames() /*-{
		var names = Object.keys(this);
		return names;
  }-*/;

  /**
   * A JSNI method to access the "description" attribute of a parameter.
   * 
   * @param name the name of the parameter, a string
   * @return the description attribute, a string
   */
  public final native String getDescription(String name) /*-{
		return this[name].description;
  }-*/;

  /**
   * JSNI method to get the "value" attribute of a parameter, a {@link ValueJSO}
   * object. It's always present, but it may be empty.
   * 
   * @param name the name of the parameter, a string
   * @return the value attribute, a ValueJSO
   */
  public final native ValueJSO getValue(String name) /*-{
		return this[name].value;
  }-*/;

  /**
   * A non-JSNI method for stringifying the attributes of a
   * {@link ParameterJSO}. Must be final.
   * 
   * @return a string formatted for display
   */
  public final String toPrettyString() {
    String pretty = "";

    Integer nParameters = this.nParameters();
    String count = "count: " + nParameters.toString() + "<br>";

    String names = "names: " + this.getParameterNames().join(", ") + "<br>";

    String firstName = this.getParameterNames().get(0);
    String description =
        "description: " + this.getDescription(firstName) + "<br>";
    ValueJSO value = getValue(firstName);
    String firstParameter =
        firstName + ":<br>" + description + "<br>" + value.toPrettyString();

    return pretty.concat(count).concat(names).concat(firstParameter);
  }
}
