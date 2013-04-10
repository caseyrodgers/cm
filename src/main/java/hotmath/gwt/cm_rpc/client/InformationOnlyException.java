package hotmath.gwt.cm_rpc.client;

public class InformationOnlyException extends Exception implements CmExceptionDoNotNotify {

	public InformationOnlyException(String msg) {
		super(msg);
	}

}
