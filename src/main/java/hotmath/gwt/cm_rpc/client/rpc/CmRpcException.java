package hotmath.gwt.cm_rpc.client.rpc;

import org.apache.log4j.Logger;

public class CmRpcException extends Exception implements java.io.Serializable {

	String message;

	private static final Logger logger = Logger.getLogger(CmRpcException.class);

	public CmRpcException(Exception e) {

	    // make sure stack trace ends up in log
	    logger.error(e);

		message = e.getMessage();

		if(message == null) {
		    message = e.getClass().getName();
		}
	}

	public CmRpcException(String message, Throwable t) {
		this.message = message;

		// make sure stack trace ends up in log
	    logger.error(message, t);		
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