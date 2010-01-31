package hotmath.gwt.shared.client.eventbus;

/** Represents each uniuqe event that can be 
 * fired through the EventBus
 * 
 * @author casey
 *
 */
public enum EventType{
    /** Solution's FIF has been entered as correct
     * 
     */
    EVENT_TYPE_SOLUTION_FIF_CORRECT,
    
    /** Fired when the browser window is resized
     * 
     */
    EVENT_TYPE_WINDOW_RESIZED,
    

    /** Fired each time a solution is viewed
     * 
     */
    EVENT_TYPE_SOLUTION_SHOW,
    
    
    /** Fired when a solution has been completed (ie, moved to last step)
     * 
     */
    EVENT_TYPE_SOLUTIONS_COMPLETE,
    
    /** Fired when a resource viewer is manually closed/removed
     * 
     */
    EVENT_TYPE_RESOURCE_VIEWER_CLOSE,
    
    
    /** Fired every time a new resource is viewed
     * 
     */
    EVENT_TYPE_RESOURCE_VIEWER_OPEN,
    
    
    /** Fired when the student data needs to be updated
     *  
     */
    EVENT_TYPE_REFRESH_STUDENT_DATA,
    
    
    /** Fired whenever a new session topic is set
     * 
     */
    EVENT_TYPE_TOPIC_CHANGED,
    
    
    /** After a user's program has been updated/changed
     * 
     * data should contain boolean indicating if change included
     * a change to the user's program or simply a user/password type change
     * 
     */
    EVENT_TYPE_USER_PROGRAM_CHANGED,
    
    /** Whenever a whiteboard is edited.
     * 
     */
    EVENT_TYPE_WHITEBOARDUPDATED,
    
    
    /** When a Modal window is closed
     * 
     */
    EVENT_TYPE_MODAL_WINDOW_CLOSED,
    
    
    /** When a Modal window is opened
     * 
     */
    EVENT_TYPE_MODAL_WINDOW_OPEN,
    
    
    /** Whenever the main context is changed
     * 
     */
    EVENT_TYPE_CONTEXTCHANGED,
    
    
    /** Whenever a user is set or changed
     * 
     */
    EVENT_TYPE_USERCHANGED,
    
    /** Fired when a new context tooltip needs to be shown
     * 
     */
    EVENT_TYPE_CONTEXT_TOOLTIP_SHOW,

    /** Fired when the Student Grid is filtered, used to keep 
     * a child window in sync with the student's current filtered set.
     */
    EVENT_TYPE_STUDENT_GRID_FILTERED
}
