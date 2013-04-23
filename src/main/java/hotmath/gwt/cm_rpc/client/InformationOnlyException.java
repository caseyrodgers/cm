package hotmath.gwt.cm_rpc.client;

import hotmath.gwt.cm_rpc_core.client.CmExceptionDoNotNotify;

public class InformationOnlyException extends Exception implements CmExceptionDoNotNotify {

	public InformationOnlyException(String msg) {
		super(msg);
	}

}
