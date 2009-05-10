package hotmath.gwt.cm.client.util;

import hotmath.gwt.shared.client.data.CmAsyncRequest;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

public class ServerRequest {

	static public void makeHttpRequest(String requestUrl,final CmAsyncRequest callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL	.encode(requestUrl));
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					exception.printStackTrace();
				}

				public void onResponseReceived(Request request,
						Response response) {
					if (200 == response.getStatusCode()) {
						String text = response.getText();
						callback.requestComplete(text);
					} else {
						String error = response.getStatusText();
						int code = response.getStatusCode();
						callback.requestFailed(code, error);
					}
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}
	
	/** Return the absolute URL to the hotmath server
	 * 
	 * @param url should be an absolute path, starting with '/' (no server)
	 * 
	 * @return  The absolute path to this resource on the active hotmath server
	 * 
	 */
    static public String getHotmathUrl(String url) {
    	// return "http://hotmath.kattare.com" + url;
    	return "http://hotmath.kattare.com" + url;
    	//return "http://192.168.0.11:8081" + url;
    }	

}
