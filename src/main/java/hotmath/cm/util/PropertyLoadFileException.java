package hotmath.cm.util;

/** Thrown when a configuration property file 
 *  fails to load.
 *  
 * @author casey
 *
 */
public class PropertyLoadFileException extends Exception {
    
    public PropertyLoadFileException() {}
    public PropertyLoadFileException(Exception e) {
        super("Error reading property configuration", e);
    }
}
