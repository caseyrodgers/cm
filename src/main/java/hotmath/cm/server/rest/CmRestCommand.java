package hotmath.cm.server.rest;

/** Encapsulates the handling of executing a REST operation 
 * 
 * @author casey
 *
 */
public interface CmRestCommand {
	String execute() throws Exception;
}
