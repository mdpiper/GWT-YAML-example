package edu.colorado.mpiper.client.examples;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;

import edu.colorado.mpiper.client.control.DataTransfer;

/**
 * Test for accessing a JSON file in DevMode using HTTP GET.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class TestComponentListYAML extends TestTemplate {

  public TestComponentListYAML() {

    String url = GWT.getHostPageBaseURL() + "data/components.yaml";
    DataTransfer.getComponentList(this, url);
    
    header.setHTML(header.getHTML() + "parse <b>components.yaml</b>");
    grid.setWidget(0, 1, new HTML(url));
    grid.setWidget(1, 1, new HTML());
  }
}
