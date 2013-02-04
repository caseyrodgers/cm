package hotmath.gwt.cm_tutor.client.event;

import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;

import com.google.gwt.event.shared.GwtEvent;

/** The whiteboard has been updated by some event
 * 
 * @author casey
 *
 */
public class UserTutorWidgetStatusUpdatedEvent extends GwtEvent<UserTutorWidgetStatusUpdatedHandler> {

    public static Type<UserTutorWidgetStatusUpdatedHandler> TYPE = new Type<UserTutorWidgetStatusUpdatedHandler>();
    private UserTutorWidgetStats userStats;

    @Override
    public Type<UserTutorWidgetStatusUpdatedHandler> getAssociatedType() {
        return TYPE;
    }
    
    public UserTutorWidgetStatusUpdatedEvent() {}
    
    public UserTutorWidgetStatusUpdatedEvent(UserTutorWidgetStats userStats) {
        this.userStats = userStats;
    }

    @Override
    protected void dispatch(UserTutorWidgetStatusUpdatedHandler handler) {
        handler.userStatsUpdate(userStats);
    }
}
