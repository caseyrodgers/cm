package hotmath.gwt.shared.client.data;

abstract public class CmAsyncRequestImplDefault implements CmAsyncRequest {

    abstract public void requestComplete(String requestData);
    
    public void requestFailed(int code, String text) {
        System.out.println("CmAsyncRequest Failed: " + code + ", " + text);
    }
}
