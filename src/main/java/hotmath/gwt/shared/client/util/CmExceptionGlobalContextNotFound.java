package hotmath.gwt.shared.client.util;


public class CmExceptionGlobalContextNotFound extends CmException {
    String pid;
	public CmExceptionGlobalContextNotFound(String pid) {
        super("Global Solution Context not found for '" + pid + "'");
        this.pid = pid;
    }
	
	/** Return the pid that does not have a Global
	 * Solution Context.
	 * 
	 * @return
	 */
	public String getPid() {
	    return pid;
	}
}
