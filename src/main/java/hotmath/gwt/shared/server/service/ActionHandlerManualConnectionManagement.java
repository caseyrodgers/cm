package hotmath.gwt.shared.server.service;



/** Marks a given class as requesting auto connection management
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
