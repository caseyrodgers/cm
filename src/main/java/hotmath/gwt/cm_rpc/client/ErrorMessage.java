package hotmath.gwt.cm_rpc.client;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class ErrorMessage implements Response {

	private String message;

	public ErrorMessage() {
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
    public String toString() {
        return "ErrorMessage [message: " + message + "]";
    };
}
