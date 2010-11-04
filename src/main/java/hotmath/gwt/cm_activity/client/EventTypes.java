package hotmath.gwt.cm_activity.client;

import hotmath.gwt.cm_mobile_shared.client.event.EventType;

public class EventTypes {
    public static EventType EVENT_INITIALIZE = new EventType("EVENT_INITIALIZE");
    
    /** Indicate it is time to move to the next question, or end of program
     * 
     */
    public static EventType EVENT_MOVE_TO_NEXT_QUESTION = new EventType("EVENT_MOVE_TO_NEXT_QUESTION");
    
    /** Fire event that signals GUI should allow fraction input
     * 
     */
    public static EventType EVENT_SHOW_INPUT_FRACTION = new EventType("EVENT_SHOW_INPUT_FRACTION");
    
    /** Setup display for inputing string equation
     * 
     */
    public static EventType EVENT_SHOW_INPUT_STRING_EQUATION = new EventType("EVENT_SHOW_INPUT_STRING_EQUATION");
    
    /** Reset input area to default/initial setup
     * 
     */
    public static EventType EVENT_SHOW_INPUT_RESET = new EventType("EVENT_SHOW_INPUT_RESET");
    
    /** Answer is ready to be checked
     * 
     */
    public static EventType EVENT_SHOW_INPUT_SUBMIT = new EventType("EVENT_SHOW_INPUT_SUBMIT");    
}
