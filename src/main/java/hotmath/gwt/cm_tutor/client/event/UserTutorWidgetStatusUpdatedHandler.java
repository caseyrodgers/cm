package hotmath.gwt.cm_tutor.client.event;

import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;

import com.google.gwt.event.shared.EventHandler;

public interface UserTutorWidgetStatusUpdatedHandler extends EventHandler {
    void userStatsUpdate(UserTutorWidgetStats userStats);
}
