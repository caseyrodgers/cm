package hotmath.gwt.cm_mobile_assignments.client.view;

import com.google.gwt.user.client.ui.IsWidget;

public interface BaseView extends IsWidget {
    void setMain(MainView main);
    MainView getMain();
}
