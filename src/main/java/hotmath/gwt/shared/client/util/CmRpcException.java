package hotmath.gwt.shared.client.util;

public class CmRpcException extends Exception implements java.io.Serializable {

	String message;

	public CmRpcException(Exception e) {
	    
	    
	    // make sure stack trace ends up in log
	    e.printStackTrace();
	    
		message = e.getMessage();

		if(message == null) {
		    message = e.getClass().getName();
		}
	}

	public CmRpcException() {
	}

	public CmRpcException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
