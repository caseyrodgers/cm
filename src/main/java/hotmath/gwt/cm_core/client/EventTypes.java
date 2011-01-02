package hotmath.gwt.cm_core.client;


public class EventTypes {
    
    /** Fired after a solution's HTML has been 
     *  loaded into the dom and any post processing
     *  should be completed. Such as widget init
     *  or mathjax rendering.
     * 
     */
    public static EventType POST_SOLUTION_LOAD = new EventType("POST_SOLUTION_LOAD");
    
    
    /** Fired after the solution is 100% ready to be viewed
     * 
     */
    public static EventType SOLUTION_LOAD_COMPLETE = new EventType("SOLUTION_LOAD_COMPLETE");
    
    
    /* Display message in a app dependent fasion 
     * 
     */
    public static EventType STATUS_MESSAGE = new EventType("STATUS_MESSAGE");
    
}
