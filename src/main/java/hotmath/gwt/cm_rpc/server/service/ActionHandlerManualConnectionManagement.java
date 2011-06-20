package hotmath.gwt.cm_rpc.server.service;



/** Marks a given class as handling it's own connection management.
 * 
 *  If a class implements this interface, that Command object 
 *  will not have a DB connection automatically created, thus
 *  it will need to manually manage the DB connections. 
 *  
 *  This is useful is Command object is using caches and might not
 *  need the connection on each invocation.
 *  
 * @author casey
 *
 */
public interface ActionHandlerManualConnectionManagement {

}
