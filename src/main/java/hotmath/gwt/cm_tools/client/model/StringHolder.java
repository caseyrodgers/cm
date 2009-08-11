package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.shared.client.rpc.Response;

/*
 * Holder for <code>String</code> <code>Response</code>
 * 
 * @author Bob
 */

public class StringHolder implements Response {

	private static final long serialVersionUID = 8252925231357266815L;

	private String response;

	public StringHolder() {
	}

    public StringHolder(String response) {
    	this.response = response;
    }

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

}
