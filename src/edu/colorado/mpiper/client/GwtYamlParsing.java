package edu.colorado.mpiper.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.mpiper.client.examples.TestComponentListJSON;
import edu.colorado.mpiper.client.examples.TestComponentListYAML;

/**
 * Can the WMT client work with YAML instead of JSON?
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class GwtYamlParsing implements EntryPoint {

  public void onModuleLoad() {

    VerticalPanel contents = new VerticalPanel();
    RootPanel.get().add(contents);
    
    HTML title = new HTML("<h1>GWT YAML Parsing</h1>");
    contents.add(title);
    
    // The tests.
    contents.add(new TestComponentListJSON());
    contents.add(new TestComponentListYAML());
  }
}
