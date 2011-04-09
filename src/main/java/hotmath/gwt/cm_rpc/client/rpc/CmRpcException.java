package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.shared.client.util.CmUserException;

public class CmRpcException extends Exception implements java.io.Serializable {

	String message;
	boolean isUserException;

	public CmRpcException(Exception e) {

	    // make sure stack trace ends up in log
	    e.printStackTrace();

		message = e.getMessage();
		
		isUserException = (e instanceof CmUserException);

		if(message == null) {
		    message = e.getClass().getName();
		}
	}

	public CmRpcException(String message, Throwable t) {
		this.message = message;

		// make sure stack trace ends up in log
	    t.printStackTrace();
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
	
	public boolean isUserException() {
		return isUserException;
	}
}