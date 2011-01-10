package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.EventType;

public class EventTypes extends hotmath.gwt.cm_core.client.EventTypes{
    
    /** Fired when a solution container is made active/selected
     * 
     */
    public static EventType STEP_CONTAINER_SELECTED = new EventType("STEP_CONTAINER_SELECTED");
    
    
    /** Fired whenever any change is made to the loaded solution
     * 
     */
    public static EventType SOLUTION_EDITOR_CHANGED = new EventType("SOLUTION_EDITOR_CHANGED");
    
    
    /** Fired when the solution has been successfully saved on the server.
     * 
     */
    public static EventType SOLUTION_EDITOR_SAVED = new EventType("SOLUTION_EDITOR_SAVED");
}
