package hotmath.gwt.shared.client.util;

public class CmException extends Exception {

	private static final long serialVersionUID = 4580519068231055465L;


	public CmException() {
        super();
    }
    
    public CmException(Exception e) {
        super(e);
    }

    public CmException(String message, Exception e) {
        super(message, e);
    }
    
    
    public CmException(String message) {
        super(message);
    }

}
