package edu.colorado.mpiper.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * A GWT JavaScript overlay (JSO) type that describes the values of a parameter
 * for a component.
 */
public class ValueJSO extends JavaScriptObject {

  // Overlay types have protected, no-arg, constructors.
  protected ValueJSO() {
  }

  /**
   * A JSNI method to access the "type" attribute of a ValueJSO. May not be
   * present, though ignored without an exception; is a string.
   */
  public final native String getType() /*-{
		return this.type;
  }-*/;

  /**
   * A JSNI method to access the "default" attribute of a ValueJSO. May not be
   * present. Coerce result to string.
   */
  public final native String getDefault() /*-{
		// "default" is reserved in JavaScript; use hash notation to access.
		return this["default"].toString();
  }-*/;

  /**
   * A generic JSNI method to set the "default" attribute of a ValueJSO.
   * 
   * @param value the value to set, of type String, Integer or Double
   */
  public final native <T> void setDefault(T value) /*-{
		// "default" is reserved in JavaScript; use hash notation to access.
		this["default"] = value;
  }-*/;

  /**
   * A JSNI method to access the "units" attribute of a ValueJSO. May not be
   * present, though ignored without an exception; is a string.
   */
  public final native String getUnits() /*-{
		return this.units;
  }-*/;

  /**
   * JSNI method to access the "range.min" attribute of a ValueJSO. May not be
   * present. The undefined check on "range" attribute is necessary. I'm
   * choosing to cast value to string, because long integers aren't supported.
   */
  public final native String getMin() /*-{
		if (typeof this.range == 'undefined') {
			return null;
		} else {
			return this.range.min.toString();
		}
  }-*/;

  /**
   * JSNI method to access the "range.max" attribute of a ValueJSO. May not be
   * present. The undefined check on "range" attribute is necessary. I'm
   * choosing to cast value to string, because long integers aren't supported.
   */
  public final native String getMax() /*-{
		if (typeof this.range == 'undefined') {
			return null;
		} else {
			return this.range.max.toString();
		}
  }-*/;

  /**
   * A JSNI method to access the "choices" attribute of a ValueJSO. May not be
   * present, though ignored without an exception; is an array of strings,
   * represented by a JsArrayString object.
   */
  public final native JsArrayString getChoices() /*-{
		return this.choices;
  }-*/;

  /**
   * A JSNI method to access the "files" attribute of a ValueJSO. May not be
   * present, though ignored without an exception; is an array of strings,
   * represented by a JsArrayString object.
   */
  public final native JsArrayString getFiles() /*-{
		return this.files;
  }-*/;

  /**
   * A JSNI method to set the "files" attribute of a ValueJSO.
   * 
   * @param files a JsArrayString of filenames
   */
  public final native void setFiles(JsArrayString files) /*-{
		this.files = files;
  }-*/;

  /**
   * A non-JSNI method for stringifying the attributes of a ValueJSO. Must be
   * final.
   */
  public final String toPrettyString() {
    String pretty = "";
    String valDefault = "default: " + this.getDefault() + "<br>";
    String valType = "type: " + this.getType() + "<br>";
    String valUnits = "units: " + this.getUnits() + "<br>";
    return pretty.concat(valDefault).concat(valType).concat(valUnits);
  }
}
