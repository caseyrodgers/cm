package hotmath.gwt.shared.client.data;

/** Provides interface to provide data from HTTP request
 * 
 * @author Casey
 *
 */
public interface CmAsyncRequest {
	void requestComplete(String requestData);
	void requestFailed(int code, String text);
}