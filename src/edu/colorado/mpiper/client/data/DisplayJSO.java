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

  /**
   * Count the number of headings in the JSON.
   * 
   * @return the number of headings, an int
   */
  public final native int nHeadings() /*-{
		var headings = Object.keys(this);
		return headings.length;
  }-*/;

  /**
   * Get heading keys from the JSON.
   * 
   * @return the headings, as a {@link JsArrayString}
   */
  public final native JsArrayString getHeadings() /*-{
		var headings = Object.keys(this);
		return headings;
  }-*/;

  /**
   * Does this JSON have items to ignore?
   * 
   * @return true, if <code>ignore</code> key exists
   */
  public final native boolean hasIgnore() /*-{
		return (typeof this.ignore != 'undefined');
  }-*/;

  /**
   * The number of items in the ignore array.
   * 
   * @return the number of items to ignore, an int
   */
  public final native int nIgnore() /*-{
		return this.ignore.length;
  }-*/;
  
  /**
   * Get the items to ignore when displaying parameters in WMT.
   * 
   * @return parameter names as a {@link JsArrayString}
   */
  public final native JsArrayString getIgnore() /*-{
		return this.ignore;
  }-*/;

  /**
   * Does this JSON have "extra" parameters?
   * 
   * @return true, if <code>extras</code> key exists
   */
  public final native boolean hasExtras() /*-{
		return (typeof this.extras != 'undefined');
  }-*/;

  /**
   * The number of "extra" items.
   * 
   * @return the number of extra items, an int
   */
  public final native int nExtras() /*-{
		var extras = Object.keys(this.extras);
		return extras.length;
  }-*/;

  /**
   * Get the "extra" parameters (needs a better name) that CSDMS introduces to
   * the list of parameters displayed in WMT.
   * 
   * @return a {@link ParameterJSO} containing one or more parameters
   */
  public final native ParameterJSO getExtras() /*-{
		return this.extras;
  }-*/;

  /**
   * Does this JSON have groups?
   * 
   * @return true, if <code>groups</code> key exists
   */
  public final native boolean hasGroups() /*-{
		return (typeof this.groups != 'undefined');
  }-*/;
  
  /**
   * The number of groups.
   * 
   * @return the number of groups, an int
   */
  public final native int nGroups() /*-{
		var names = Object.keys(this.groups);
		return names.length;
  }-*/;
  
  /**
   * Get the names of all the groups.
   * 
   * @return the names of groups, as a {@link JsArrayString}
   */
  public final native JsArrayString getGroupNames() /*-{
		var names = Object.keys(this.groups);
		return names;
  }-*/;
  
  /**
   * Get the members of a group, using the group name. Groups are unordered,
   * unlike Sections.
   * 
   * @param groupName the name of the group
   * @return the members of the group as a {@link JsArrayString}
   */
  public final native JsArrayString getGroupByName(String groupName) /*-{
		return this.groups[groupName]
  }-*/;

  /**
   * Does this JSON have sections?
   * 
   * @return true, if <code>sections</code> key exists
   */
  public final native boolean hasSections() /*-{
		return (typeof this.sections != 'undefined');
  }-*/;

  /**
   * The number of sections.
   * 
   * @return the number of sections, an int
   */
  public final native int nSections() /*-{
		return this.sections.length;
  }-*/;

  /**
   * Get the titles, ordered, of all the sections.
   * 
   * @return the names of sections, as a {@link JsArrayString}
   */
  public final native JsArrayString getSectionTitles() /*-{
		var titles = [];
		for (var i = 0; i < this.sections.length; i++) {
		  titles = titles.concat(this.sections[i].title);;
		}
		return titles;
  }-*/;
  
  /**
   * Get the members of a section, by index. Sections are ordered, unlike
   * Groups.
   * 
   * @param sectionIndex the index into the <code>sections</code> array
   * @return the members of the section as a {@link JsArrayString}
   */
  public final native JsArrayString getSectionMembers(Integer sectionIndex) /*-{
		return this.sections[sectionIndex].members;
  }-*/;
    
  /**
   * Returns a formatted String for display.
   */
  public final String toPrettyString() {
    String pretty = "";
    String names = "headings: " + this.getHeadings().join(", ") + "<br>";
    Boolean hasIgnore = this.hasIgnore();
    String doesHaveIgnore =
        "has ignore items? " + hasIgnore.toString() + "<br>";
    String nIgnore = "number of ignore items: " + this.nIgnore() + "<br>";
    String ignores = "ignore items: " + this.getIgnore().join(", ") + "<br>";
    Boolean hasExtras = this.hasExtras();
    String doesHaveExtras = "has extras? " + hasExtras.toString() + "<br>";
    String nExtras = "number of extras: " + this.nExtras() + "<br>";
    String run_duration =
        "run_duration: "
            + this.getExtras().getValue("run_duration").getDefault() + "<br>";
    Boolean hasSections = this.hasSections();
    String doesHaveSections =
        "has sections? " + hasSections.toString() + "<br>";
    String nSections = "number of sections: " + this.nSections() + "<br>";
    String sections =
        "sections: " + this.getSectionTitles().join(", ") + "<br>";
    String runSection =
        "members of <tt>Run</tt> section: "
            + this.getSectionMembers(0).join(", ") + "<br>";
    Boolean hasGroups = this.hasGroups();
    String doesHaveGroups = "has groups? " + hasGroups.toString() + "<br>";
    String nGroups = "number of groups: " + this.nGroups() + "<br>";
    String groups = "groups: " + this.getGroupNames().join(", ") + "<br>";
    String groupMembers =
        "members of <tt>rho_air_group</tt>: "
            + getGroupByName("rho_air_group").join(", ") + "<br>";

    return pretty.concat(names).concat(doesHaveIgnore).concat(nIgnore).concat(
        ignores).concat(doesHaveExtras).concat(nExtras).concat(run_duration)
        .concat(doesHaveSections).concat(nSections).concat(sections).concat(
            runSection).concat(doesHaveGroups).concat(nGroups).concat(groups)
        .concat(groupMembers);
  }
}
