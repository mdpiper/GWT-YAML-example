package edu.colorado.mpiper.client.control;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

import edu.colorado.mpiper.client.data.ComponentListJSO;
import edu.colorado.mpiper.client.data.DisplayJSO;
import edu.colorado.mpiper.client.data.ParameterJSO;
import edu.colorado.mpiper.client.examples.TestTemplate;

/**
 * A class that defines static methods for accessing, modifying and deleting,
 * through asynchronous HTTP GET and POST requests, the JSON files used to set
 * up, configure and run WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DataTransfer {

  private static final String ERR_MSG = "An error occurred: ";

  /**
   * A JSNI method for creating a String from a JavaScriptObject.
   * <p>
   * See <a href=
   * "http://stackoverflow.com/questions/4872770/excluding-gwt-objectid-from-json-stringifyjso-in-devmode"
   * >this</a> discussion of '__gwt_ObjectId'.
   * 
   * @param jso a JavaScriptObject
   * @return a String representation of the JavaScriptObject
   */
  public final native static <T> String stringify(T jso) /*-{
		return JSON.stringify(jso, function(key, value) {
			if (key == '__gwt_ObjectId') {
				return;
			}
			return value;
		});
  }-*/;

  /**
   * A JSNI method for evaluating JSONs. This is a generic method. It returns a
   * JavaScript object of the type denoted by the type parameter T.
   * <p>
   * See <a
   * href="http://docs.oracle.com/javase/tutorial/extra/generics/methods.html"
   * >Generic Methods</a> and <a
   * href="http://stackoverflow.com/questions/1843343/json-parse-vs-eval"
   * >JSON.parse vs. eval()</a>.
   * 
   * @param jsonStr a trusted String
   * @return a JavaScriptObject that can be cast to an overlay type
   */
  public final native static <T> T parse(String jsonStr) /*-{
		return JSON.parse(jsonStr);
  }-*/;

  /**
   * Converts a stringified YAML object into a JavaScriptObject using the <a
   * href="https://github.com/nodeca/js-yaml">JS-YAML</a> library.
   * <p>
   * See <a href="http://stackoverflow.com/a/4437724/1563298">this</a>.
   * 
   * @param yamlStr a trusted String
   * @return a JavaScriptObject that can be cast to an overlay type
   */
  public final native static <T> T yamlLoad(String yamlStr) /*-{
		return $wnd.jsyaml.safeLoad(yamlStr);
  }-*/;

  /**
   * A utility method for extracting the extension from a file name.
   * 
   * @param fileName a file name
   * @return the file extension, a String
   */
  private static String getExtension(String fileName) {
    String ext = "";
    int i = fileName.lastIndexOf(".");
    if (i > 0) {
      ext = fileName.substring(i + 1);
    }
    return ext;
  }

  /**
   * Returns a deep copy of the input JavaScriptObject.
   * <p>
   * This is the public interface to {@link #copyImpl(JavaScriptObject)}, which
   * does the heavy lifting.
   * 
   * @param jso a JavaScriptObject
   */
  @SuppressWarnings("unchecked")
  public static <T extends JavaScriptObject> T copy(T jso) {
    return (T) copyImpl(jso);
  }

  /**
   * A recursive JSNI method for making a deep copy of an input
   * JavaScriptObject. This is the private implementation of
   * {@link #copy(JavaScriptObject)}.
   * <p>
   * See <a
   * href="http://stackoverflow.com/questions/4730463/gwt-overlay-deep-copy"
   * >This</a> example code was very helpful (thanks to the author, <a
   * href="http://stackoverflow.com/users/247462/javier-ferrero">Javier
   * Ferrero</a>!)
   * 
   * @param obj
   */
  private static native JavaScriptObject copyImpl(JavaScriptObject obj) /*-{

		if (obj == null)
			return obj;

		var copy;

		if (obj instanceof Date) {
			copy = new Date();
			copy.setTime(obj.getTime());
		} else if (obj instanceof Array) {
			copy = [];
			for (var i = 0, len = obj.length; i < len; i++) {
				if (obj[i] == null || typeof obj[i] != "object")
					copy[i] = obj[i];
				else
					copy[i] = @edu.colorado.mpiper.client.control.DataTransfer::copyImpl(Lcom/google/gwt/core/client/JavaScriptObject;)(obj[i]);
			}
		} else {
			copy = {};
			for ( var attr in obj) {
				if (obj.hasOwnProperty(attr)) {
					if (obj[attr] == null || typeof obj[attr] != "object")
						copy[attr] = obj[attr];
					else
						copy[attr] = @edu.colorado.mpiper.client.control.DataTransfer::copyImpl(Lcom/google/gwt/core/client/JavaScriptObject;)(obj[attr]);
				}
			}
		}
		return copy;
  }-*/;

  /**
   * A worker that builds a HTTP query string from a HashMap of key-value
   * entries.
   * 
   * @param entries a HashMap of key-value pairs
   * @return the query, as a String
   */
  private static String buildQueryString(HashMap<String, String> entries) {

    StringBuilder sb = new StringBuilder();

    Integer entryCount = 0;
    for (Entry<String, String> entry : entries.entrySet()) {
      if (entryCount > 0) {
        sb.append("&");
      }
      String encodedName = URL.encodeQueryString(entry.getKey());
      sb.append(encodedName);
      sb.append("=");
      String encodedValue = URL.encodeQueryString(entry.getValue());
      sb.append(encodedValue);
      entryCount++;
    }

    return sb.toString();
  }

  /**
   * Makes an asynchronous HTTP GET request to retrieve a list of components.
   */
  public static void getComponentList(TestTemplate tester, String url) {

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(url));
    GWT.log(url);

    try {
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(null, new ComponentListRequestCallback(tester,
              url));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
    }
  }

  /**
   * A RequestCallback handler class that provides the callback for a GET
   * request of the list of available components.
   */
  public static class ComponentListRequestCallback implements RequestCallback {

    private TestTemplate tester;
    private String url;
    private Boolean isYAML;

    public ComponentListRequestCallback(TestTemplate tester, String url) {
      this.tester = tester;
      this.url = url;

      String ext = getExtension(url);
      GWT.log(ext);
      this.isYAML = ext.contentEquals("yaml");
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
      if (Response.SC_OK == response.getStatusCode()) {

        String rtxt = response.getText();
        GWT.log(rtxt);

        ComponentListJSO jso;
        if (isYAML) {
          jso = yamlLoad(rtxt);
        } else {
          jso = parse(rtxt);
        }

        String jsotxt = jso.toPrettyString();
        tester.setResponse(jsotxt);

      } else {
        String msg =
            "The URL '" + url + "' did not give an 'OK' response. "
                + "Response code: " + response.getStatusCode();
        Window.alert(msg);
      }
    }

    @Override
    public void onError(Request request, Throwable exception) {
      Window.alert(ERR_MSG + exception.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTP GET request to retrieve the parameters for a
   * component.
   */
  public static void getParameters(TestTemplate tester, String url) {

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(url));
    GWT.log(url);

    try {
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(null, new ParametersRequestCallback(tester, url));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
    }
  }

  /**
   * A RequestCallback handler class that provides the callback for a GET
   * request of the parameters of a component.
   */
  public static class ParametersRequestCallback implements RequestCallback {

    private TestTemplate tester;
    private String url;
    private Boolean isYAML;

    public ParametersRequestCallback(TestTemplate tester, String url) {
      this.tester = tester;
      this.url = url;

      String ext = getExtension(url);
      GWT.log(ext);
      this.isYAML = ext.contentEquals("yaml");
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
      if (Response.SC_OK == response.getStatusCode()) {

        String rtxt = response.getText();
        GWT.log(rtxt);

        ParameterJSO jso;
        if (isYAML) {
          jso = yamlLoad(rtxt);
        } else {
          jso = parse(rtxt);
        }

        String jsotxt = jso.toPrettyString();
        tester.setResponse(jsotxt);

      } else {
        String msg =
            "The URL '" + url + "' did not give an 'OK' response. "
                + "Response code: " + response.getStatusCode();
        Window.alert(msg);
      }
    }

    @Override
    public void onError(Request request, Throwable exception) {
      Window.alert(ERR_MSG + exception.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTP GET request to retrieve the display metadata for
   * a component.
   * 
   * @param tester a TestTemplate instance
   * @param url the location of the file to access
   */
  public static void getDisplay(TestTemplate tester, String url) {

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(url));
    GWT.log(url);

    try {
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(null, new DisplayRequestCallback(tester, url));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
    }
  }

  /**
   * A RequestCallback handler class that provides the callback for a GET
   * request of the display metadata of a component.
   */
  public static class DisplayRequestCallback implements RequestCallback {

    private TestTemplate tester;
    private String url;
    private Boolean isYAML;

    public DisplayRequestCallback(TestTemplate tester, String url) {
      this.tester = tester;
      this.url = url;

      String ext = getExtension(url);
      GWT.log(ext);
      this.isYAML = ext.contentEquals("yaml");
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
      if (Response.SC_OK == response.getStatusCode()) {

        String rtxt = response.getText();
        GWT.log(rtxt);

        DisplayJSO jso;
        if (isYAML) {
          jso = yamlLoad(rtxt);
        } else {
          jso = parse(rtxt);
        }

        String jsotxt = jso.toPrettyString();
        tester.setResponse(jsotxt);

      } else {
        String msg =
            "The URL '" + url + "' did not give an 'OK' response. "
                + "Response code: " + response.getStatusCode();
        Window.alert(msg);
      }
    }

    @Override
    public void onError(Request request, Throwable exception) {
      Window.alert(ERR_MSG + exception.getMessage());
    }
  }

}
