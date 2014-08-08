package hotmath.gwt.cm_rpc_core.client.rpc;

public class CmRpcException extends Exception implements com.google.gwt.user.client.rpc.IsSerializable {

	private static final long serialVersionUID = -5527419287522207562L;

	String message;
	boolean isUserException;

	public CmRpcException(Exception e) {

	    // make sure stack trace ends up in log
	    e.printStackTrace();

		message = e.getMessage();
		
		if(message == null) {
		    message = e.getClass().getName();
		}
	}

	public CmRpcException(Exception e, boolean isUserException) {

	    // make sure stack trace ends up in log
	    e.printStackTrace();

		message = e.getMessage();
		
		this.isUserException = isUserException;

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