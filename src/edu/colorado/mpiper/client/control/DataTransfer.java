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
   * Converts a stringified YAML object into a JavaScriptObject using the
   * <a href="https://github.com/nodeca/js-yaml">JS-YAML</a> library.
   * <p>
   * See <a href="http://stackoverflow.com/a/4437724/1563298">this</a>.
   *  
   * @param yamlStr
   * @return
   */
  public final native static <T> T yamlLoad(String yamlStr) /*-{
		return $wnd.jsyaml.safeLoad(yamlStr);
  }-*/;
  
  
  public static String getExtension(String fileName) {
    String ext = "";
    int i = fileName.lastIndexOf(".");
    if (i >0) {
      ext = fileName.substring(i+1);
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
   * Makes an asynchronous HTTP GET request to retrieve the list of components
   * stored in the WMT database.
   * <p>
   * Note that on completion of the request,
   * {@link #getComponent(DataManager, String)} starts pulling data for
   * individual components from the server.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static void getComponentList(TestTemplate tester, String url) {

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(url));
    GWT.log(url);

    try {
      @SuppressWarnings("unused")
      Request request =
          builder
              .sendRequest(null, new ComponentListRequestCallback(tester, url));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTP GET request to the server to retrieve the data
   * for a single component.
   * 
   * @param data the DataManager object for the WMT session
   * @param componentId the identifier of the component in the database, a
   *          String
   */
  // public static void getComponent(DataManager data, String componentId) {
  //
  // String url = DataURL.showComponent(data, componentId);
  // GWT.log(url);
  //
  // RequestBuilder builder =
  // new RequestBuilder(RequestBuilder.GET, URL.encode(url));
  //
  // try {
  // @SuppressWarnings("unused")
  // Request request =
  // builder.sendRequest(null, new ComponentRequestCallback(data, url,
  // componentId));
  // } catch (RequestException e) {
  // Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
  // }
  // }

  /**
   * Makes an asynchronous HTTP GET request to retrieve the list of models
   * stored in the WMT database.
   * 
   * @param data the DataManager object for the WMT session
   */
  // public static void getModelList(DataManager data) {
  //
  // String url = DataURL.listModels(data);
  // GWT.log(url);
  //
  // RequestBuilder builder =
  // new RequestBuilder(RequestBuilder.GET, URL.encode(url));
  //
  // try {
  // @SuppressWarnings("unused")
  // Request request =
  // builder.sendRequest(null, new ModelListRequestCallback(data, url));
  // } catch (RequestException e) {
  // Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
  // }
  // }

  /**
   * Makes a pair of asynchronous HTTP GET requests to retrieve model data and
   * metadata from a server.
   * 
   * @param data the DataManager object for the WMT session
   * @param modelId the unique identifier for the model in the database, an
   *          Integer
   */
  // public static void getModel(DataManager data, Integer modelId) {
  //
  // // The "open" URL returns metadata (name, owner) in a ModelMetadataJSO,
  // // while the "show" URL returns data in a ModelJSO.
  // String openURL = DataURL.openModel(data, modelId);
  // GWT.log(openURL);
  // String showURL = DataURL.showModel(data, modelId);
  // GWT.log(showURL);
  //
  // RequestBuilder openBuilder =
  // new RequestBuilder(RequestBuilder.GET, URL.encode(openURL));
  // try {
  // @SuppressWarnings("unused")
  // Request openRequest =
  // openBuilder.sendRequest(null, new ModelRequestCallback(data, openURL,
  // Constants.MODELS_OPEN_PATH));
  // } catch (RequestException e) {
  // Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
  // }
  //
  // RequestBuilder showBuilder =
  // new RequestBuilder(RequestBuilder.GET, URL.encode(showURL));
  // try {
  // @SuppressWarnings("unused")
  // Request showRequest =
  // showBuilder.sendRequest(null, new ModelRequestCallback(data, showURL,
  // Constants.MODELS_SHOW_PATH));
  // } catch (RequestException e) {
  // Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
  // }
  // }

  /**
   * Makes an asynchronous HTTP POST request to save a new model, or edits to an
   * existing model, or a duplicate of an existing model, to the server.
   * 
   * @param data the DataManager object for the WMT session
   * @param saveType new model, edit existing model, or model save as
   */
  // public static void postModel(DataManager data, String saveType) {
  //
  // Integer modelId = data.getMetadata().getId();
  //
  // GWT.log("this model id: " + modelId.toString());
  //
  // String url;
  // if (saveType.equals(Constants.MODELS_NEW_PATH)) {
  // url = DataURL.newModel(data);
  // } else if (saveType.equals(Constants.MODELS_EDIT_PATH)) {
  // url = DataURL.editModel(data, modelId);
  // } else if (saveType.equals(Constants.MODELS_SAVEAS_PATH)) {
  // url = DataURL.saveasModel(data, modelId);
  // } else {
  // Window.alert("No match found for save action.");
  // return;
  // }
  // GWT.log(url);
  // GWT.log(data.getModelString());
  //
  // RequestBuilder builder =
  // new RequestBuilder(RequestBuilder.POST, URL.encode(url));
  //
  // HashMap<String, String> entries = new HashMap<String, String>();
  // entries.put("name", data.getModel().getName());
  // entries.put("json", data.getModelString());
  // String queryString = buildQueryString(entries);
  //
  // try {
  // builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
  // @SuppressWarnings("unused")
  // Request request =
  // builder.sendRequest(queryString, new ModelRequestCallback(data, url,
  // saveType));
  // } catch (RequestException e) {
  // Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
  // }
  // }

  /**
   * A RequestCallback handler class that provides the callback for a GET
   * request of the list of available components in the WMT database. On
   * success, the list of component ids are stored in the {@link DataManager}
   * object for the WMT session. Concurrently,
   * {@link DataTransfer#getComponent(DataManager, String)} is called to
   * download and store information on the listed components.
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
        
        String jsotxt = jso.getComponents().join(", ");
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
   * A RequestCallback handler class that provides the callback for a GET
   * request of a component. On success,
   * {@link DataManager#addComponent(ComponentJSO)} and
   * {@link DataManager#addModelComponent(ComponentJSO)} are called to store the
   * (class) component and the model component in the DataManager object for the
   * WMT session.
   */
  // public static class ComponentRequestCallback implements RequestCallback {
  //
  // private DataManager data;
  // private String url;
  // private String componentId;
  //
  // public ComponentRequestCallback(DataManager data, String url,
  // String componentId) {
  // this.data = data;
  // this.url = url;
  // this.componentId = componentId;
  // }
  //
  // @Override
  // public void onResponseReceived(Request request, Response response) {
  // if (Response.SC_OK == response.getStatusCode()) {
  //
  // String rtxt = response.getText();
  // GWT.log(rtxt);
  // ComponentJSO jso = parse(rtxt);
  // data.addComponent(jso); // "class" component
  // data.addModelComponent(copy(jso)); // "instance" component, for model
  //
  // // Replace the associated placeholder ComponentSelectionMenu item.
  // ((ComponentSelectionMenu) data.getPerspective().getModelTree()
  // .getDriverComponentCell().getComponentMenu()).replaceMenuItem(jso
  // .getId());
  //
  // } else {
  //
  // // If the component didn't load, try to reload it RETRY_ATTEMPTS times.
  // // If that fails, display an error message in a window.
  // Integer attempt = data.retryComponentLoad.get(componentId);
  // data.retryComponentLoad.put(componentId, attempt++);
  // if (attempt < Constants.RETRY_ATTEMPTS) {
  // getComponent(data, componentId);
  // } else {
  // String msg =
  // "The URL '" + url + "' did not give an 'OK' response."
  // + " Response code: " + response.getStatusCode();
  // Window.alert(msg);
  // }
  // }
  // }
  //
  // @Override
  // public void onError(Request request, Throwable exception) {
  // Window.alert(Constants.REQUEST_ERR_MSG + exception.getMessage());
  // }
  // }

  /**
   * A RequestCallback handler class that provides the callback for a GET
   * request of the list of available models in the WMT database. On success,
   * the list of model names and ids are stored in the {@link DataManager}
   * object for the WMT session.
   */
  // public static class ModelListRequestCallback implements RequestCallback {
  //
  // private DataManager data;
  // private String url;
  //
  // public ModelListRequestCallback(DataManager data, String url) {
  // this.data = data;
  // this.url = url;
  // }
  //
  // @Override
  // public void onResponseReceived(Request request, Response response) {
  // if (Response.SC_OK == response.getStatusCode()) {
  //
  // String rtxt = response.getText();
  // GWT.log(rtxt);
  // ModelListJSO jso = parse(rtxt);
  // data.modelList = jso;
  //
  // } else {
  // String msg =
  // "The URL '" + url + "' did not give an 'OK' response. "
  // + "Response code: " + response.getStatusCode();
  // Window.alert(msg);
  // }
  // }
  //
  // @Override
  // public void onError(Request request, Throwable exception) {
  // Window.alert(Constants.REQUEST_ERR_MSG + exception.getMessage());
  // }
  // }

  /**
   * A RequestCallback handler class that provides the callback for a model GET
   * or POST request.
   * <p>
   * On a successful GET, {@link DataManager#deserialize()} is called to
   * populate the WMT GUI. On a successful POST,
   * {@link DataTransfer#getModelList(DataManager)} is called to refresh the
   * list of models available on the server.
   */
  // public static class ModelRequestCallback implements RequestCallback {
  //
  // private DataManager data;
  // private String url;
  // private String type;
  //
  // public ModelRequestCallback(DataManager data, String url, String type) {
  // this.data = data;
  // this.url = url;
  // this.type = type;
  // }
  //
  // /*
  // * A helper for processing the return from models/show.
  // */
  // private void showActions(String rtxt) {
  // ModelJSO jso = parse(rtxt);
  // data.setModel(jso);
  // data.modelIsSaved(true);
  // data.deserialize();
  // }
  //
  // /*
  // * A helper for processing the return from models/open.
  // */
  // private void openActions(String rtxt) {
  // ModelMetadataJSO jso = parse(rtxt);
  // data.setMetadata(jso);
  // getModelLabels(data, data.getMetadata().getId());
  // }
  //
  // /*
  // * A helper for processing the return from models/new and models/edit.
  // */
  // private void editActions() {
  // data.updateModelSaveState(true);
  // DataTransfer.getModelList(data);
  // updateSelectedLabels(data.getMetadata().getId());
  // }
  //
  // /*
  // * A helper for attaching all selected labels to (and detaching all
  // * unselected labels from) a model.
  // */
  // private void updateSelectedLabels(Integer modelId) {
  // // XXX This may be slow.
  // for (Map.Entry<String, LabelJSO> entry : data.modelLabels.entrySet()) {
  // if (entry.getValue().isSelected()) {
  // addModelLabel(data, modelId, entry.getValue().getId());
  // } else {
  // removeModelLabel(data, modelId, entry.getValue().getId());
  // }
  // }
  // }
  //
  // @Override
  // public void onResponseReceived(Request request, Response response) {
  //
  // data.showDefaultCursor();
  // if (Response.SC_OK == response.getStatusCode()) {
  //
  // String rtxt = response.getText();
  // GWT.log(rtxt);
  //
  // if (type.matches(Constants.MODELS_SHOW_PATH)) {
  // showActions(rtxt);
  // } else if (type.matches(Constants.MODELS_OPEN_PATH)) {
  // openActions(rtxt);
  // } else if (type.matches(Constants.MODELS_NEW_PATH)) {
  // Integer modelId = Integer.valueOf(rtxt);
  // data.getMetadata().setId(modelId);
  // data.getMetadata().setOwner(data.security.getWmtUsername());
  // editActions();
  // } else if (type.matches(Constants.MODELS_EDIT_PATH)) {
  // editActions();
  // } else if (type.matches(Constants.MODELS_SAVEAS_PATH)) {
  // Integer modelId = Integer.valueOf(rtxt);
  // data.getMetadata().setId(modelId);
  // editActions();
  // } else if (type.matches(Constants.MODELS_DELETE_PATH)) {
  // DataTransfer.getModelList(data);
  // Window.alert("Model deleted.");
  // } else {
  // Window.alert(Constants.RESPONSE_ERR_MSG);
  // }
  //
  // } else {
  // String msg =
  // "The URL '" + url + "' did not give an 'OK' response. "
  // + "Response code: " + response.getStatusCode();
  // Window.alert(msg);
  // }
  // }
  //
  // @Override
  // public void onError(Request request, Throwable exception) {
  // Window.alert(Constants.REQUEST_ERR_MSG + exception.getMessage());
  // }
  // }

}
