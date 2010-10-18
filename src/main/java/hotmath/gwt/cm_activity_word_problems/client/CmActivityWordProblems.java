package hotmath.gwt.cm_activity_word_problems.client;

import hotmath.gwt.cm_activity.client.CmActivity;
import hotmath.gwt.cm_activity.client.EventTypes;
import hotmath.gwt.cm_activity.client.WordProblemSet;
import hotmath.gwt.cm_activity.client.WordProblemsPanel;
import hotmath.gwt.cm_activity.client.rpc.GetWordProblemSetAction;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.ScreenOrientation;
import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.util.Screen;
import hotmath.gwt.cm_mobile_shared.client.util.Screen.OrientationChangedHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Provide minimal CM for mobile access.
 * 
 * @author casey
 * 
 */
public class CmActivityWordProblems implements EntryPoint, OrientationChangedHandler {

    RootPanel _rootPanel;
    static CmActivityWordProblems __instance;
    public void onModuleLoad() {
        __instance = this;
        
        _rootPanel = RootPanel.get("main-content");
        _rootPanel.add(createApplicationPanel());
        
        Screen screen = new Screen();
        screen.addHandler(this);
        orientationChanged(screen.getScreenOrientation());
        
        CatchupMathMobileShared.__instance.hideBusyPanel();

        CatchupMathMobileShared.__instance.user = new CmMobileUser();
        
        orientationChanged(ScreenOrientation.Portrait);
    }
    
    /** call global JS function to initialize any external resources
     * 
     */
    private native void initializeExternalJs()/*-{
        $wnd.initializeExternalJs();
    }-*/;    

    /**
     * Create a panel with a static header and scrollable body area.
     * 
     * The header will contain buttons and meta data. The body will container
     * the app data.
     * 
     * @return
     */
    private Widget createApplicationPanel() {
        WordProblemsPanel wordProblems = new WordProblemsPanel();
        return wordProblems;
    }
    
    @Override
    public void orientationChanged(ScreenOrientation newOrientation) {
        if (newOrientation == ScreenOrientation.Portrait) {
            _rootPanel.removeStyleName("landscape");
            _rootPanel.addStyleName("portrait");
        } else {
            _rootPanel.addStyleName("landscape");
            _rootPanel.removeStyleName("portrait");
        }
    }
    
    
}
