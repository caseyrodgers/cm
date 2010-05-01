package hotmath.gwt.cm_mobile.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class CatchupMathMobileHistoryListener implements ValueChangeHandler<String> {
    public void onValueChange(ValueChangeEvent<String> event) {

        String historyToken = event.getValue();
        // do default action
        if(historyToken.equals("quiz")) {
            CatchupMathMobile.__instance.showQuizPanel();
        }
        else if(historyToken.equals("lesson")) {
            CatchupMathMobile.__instance.showPrescriptionPanel();
        }
        else {
            CatchupMathMobile.__instance.showLoginForm();
        }
    }
}
