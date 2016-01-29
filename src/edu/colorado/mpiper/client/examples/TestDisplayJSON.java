package edu.colorado.mpiper.client.examples;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;

import edu.colorado.mpiper.client.control.DataTransfer;

/**
 * Test for accessing the WMT display metadata from a JSON file in DevMode.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class TestDisplayJSON extends TestTemplate {

  public TestDisplayJSON() {

    String fileType = "json";
    String fileBaseName = "wmt";
    String fileName = fileBaseName + "." + fileType;
    String url = GWT.getHostPageBaseURL() + "data/" + fileName;
    DataTransfer.getDisplay(this, url);

    header.setHTML(header.getHTML() + "parse <b>" + fileName + "</b>");
    grid.setWidget(0, 1, new HTML(url));
    grid.setWidget(1, 1, new HTML());
  }
}
