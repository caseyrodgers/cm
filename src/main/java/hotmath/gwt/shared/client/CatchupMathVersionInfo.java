package hotmath.gwt.shared.client;

/** class that is used to define this build
 *  of Catchup Math.  Allowing it to be compiled
 *  into the output .. no external property.
 *  
 *  @TODO: create this file dynamically during builds.
 *  
 *  If client version does not match server call, then client 
 *  is out date and a message is shown to request client to 
 *  refresh.
 *  
 *  NOTE: This much match the client.version property
 *  in ~/cm.properties.
 *  
 *  @SEE SystemVersionUpdateChecker.
 *  
 * @author casey
 *
 */
public class CatchupMathVersionInfo {
    static public int getBuildVersion() {
        return 23;
    }
}
