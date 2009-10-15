package hotmath.gwt.cm.client;

import hotmath.gwt.cm.client.history.CatchupMathHistoryListener;
import hotmath.gwt.cm_tools.client.CatchupMathTools;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Label;

public class WelcomePanel extends LayoutContainer {
    
    public WelcomePanel() {
        
        add(new Label("Welcome to Catchup Math"));
        Button go = new Button("Begin Catchup Math");
        go.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                startup();
            }
        });
        
        add(go);
    }
    
    private void startup() {
        try {
            CatchupMathTools.setBusy(true);
            // ok, we are good to go
            /** Add a history listener to manage the state changes
             * 
             */
            History.addValueChangeHandler(new CatchupMathHistoryListener());
            History.fireCurrentHistoryState();
        }
        finally {
            CatchupMathTools.setBusy(false);
        }
    }

}
