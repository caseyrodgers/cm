package hotmath.gwt.cm_activity.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class HistoryListener implements ValueChangeHandler<String> {
    public void onValueChange(ValueChangeEvent<String> event) {
        String historyToken = event.getValue();
        
        if(historyToken.equals("main")) {
            Controller.navigateToMain();
        }
    }
}
