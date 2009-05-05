package hotmath.gwt.cm.client.util;

public class CmRpcException extends Exception implements java.io.Serializable {

	String message;

	public CmRpcException(Exception e) {
		message = e.getMessage();

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
