package edu.colorado.mpiper.client.examples;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;

import edu.colorado.mpiper.client.control.DataTransfer;

/**
 * Test for accessing a YAML parameters file in DevMode.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class TestParametersYAML extends TestTemplate {

  public TestParametersYAML() {

    String fileType = "yaml";
    String fileBaseName = "parameters";
    String fileName = fileBaseName + "." + fileType;
    String url = GWT.getHostPageBaseURL() + "data/" + fileName;
    DataTransfer.getParameters(this, url);
    
    header.setHTML(header.getHTML() + "parse <b>" + fileName + "</b>");
    grid.setWidget(0, 1, new HTML(url));
    grid.setWidget(1, 1, new HTML());
  }
}
