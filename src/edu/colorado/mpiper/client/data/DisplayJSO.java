package edu.colorado.mpiper.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * A GWT JavaScript overlay (JSO) type with methods that access descriptions of
 * how the parameters of a component should be displayed in WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DisplayJSO extends JavaScriptObject {

  protected DisplayJSO() {
  }

  public final native int nHeadings() /*-{
		var headings = Object.keys(this);
		return headings.length;
  }-*/;

  public final native JsArrayString getHeadings() /*-{
		var headings = Object.keys(this);
		return headings;
  }-*/;

  public final native JsArrayString getIgnore() /*-{
		return this.ignore;
  }-*/;

  public final native ParameterJSO getExtras() /*-{
		return this.extras;
  }-*/;
  
  /**
   * Returns a formatted String for display.
   */
  public final String toPrettyString() {
    String pretty = "";
    String names = "headings: " + this.getHeadings().join(", ") + "<br>";
    String ignores = "ignore items: " + this.getIgnore().join(", ") + "<br>";
    String run_duration = "run_duration: " + this.getExtras().getValue("run_duration").getDefault() + "<br>";
    
    return pretty.concat(names).concat(ignores).concat(run_duration);
  }
}
