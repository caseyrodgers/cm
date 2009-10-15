package hotmath.gwt.cm.client;

import hotmath.gwt.cm.client.history.CatchupMathHistoryListener;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.CmLoginAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.util.UserInfo;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;

public class WelcomePanel extends LayoutContainer {
    
    public WelcomePanel() {
        setLayout(new BorderLayout());
        Frame f = new Frame("/gwt-resources/first-time-visitor.html");
        add(f, new BorderLayoutData(LayoutRegion.CENTER,200));
        
        add(new Label("Welcome to Catchup Math"));
        Button go = new Button("Begin Catchup Math");
        go.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                startup();
            }
        });
        
        add(go, new BorderLayoutData(LayoutRegion.SOUTH,30));
        
        
        CmShared.handleLoginProcessAsync(new CmLoginAsync() {
            public void loginSuccessful(Integer uid) {
                processLoginComplete(uid);
            }
        });
    }
    
    private void startup() {
        try {
            CatchupMathTools.setBusy(true);
            
            // ok, we are good to go
            /** Add a history listener to manage the state changes
             * and get things moving.
             * 
             */
            History.addValueChangeHandler(new CatchupMathHistoryListener());
            History.fireCurrentHistoryState();
        }
        finally {
            CatchupMathTools.setBusy(false);
        }
    }
    
    
    /** Call when successfully determined users uid
     * 
     * @param uid
     */
    private void processLoginComplete(final Integer uid) {
        
        
        UserInfo.loadUser(uid,new CmAsyncRequest() {
            public void requestComplete(String requestData) {
                
                if(UserInfo.getInstance().isSingleUser())
                    Window.setTitle("Catchup Math: Student");

                String ac=CmShared.getQueryParameter("type");
                if(ac != null && ac.equals("ac")) {
                    
                    /** 
                     * self registration
                     * 
                     * mark as not owner, since this is a
                     */
                    UserInfo.getInstance().setActiveUser(false);
                    CatchupMath.__thisInstance.showAutoRegistration_gwt();
                }
                
                CatchupMathTools.showAlert("Login complete!");
                
            }
            public void requestFailed(int code, String text) {
                CatchupMathTools.showAlert("There was a problem reading user information from server" );
            }
        });        
    }
    
   

}
