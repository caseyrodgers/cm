package hotmath.gwt.cm2.client;



import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CatchupMath implements EntryPoint {
    static {
        publishNative();
    }

    /**
     * Return last create instance
     * 
     * @return
     */
    public static CatchupMath getThisInstance() {
        return __thisInstance;
    }

    Panel _mainPort;

    static CatchupMath __thisInstance;


    /**
     * Flag to indicate message about show work has been given (one per login)
     * 
     */
    public static boolean __hasBeenInformedAboutShowWork;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
    	HelpWindow hm;
    	
    	RootPanel.get().add(new Label("Test Login Panel"));
    }


    /**
     * Startup the history and initial history state check
     * 
     * This should happen just once during program operation
     * 
     */
    public void startNormalOperation() {
    }

    /**
     * Helper page to create the Login page
     * 
     * @TODO: get out of main
     * 
     */
    public void showLoginPage() {
    }

    /**
     * Helper page to create the Quiz page
     * 
     * @TODO: get out of main
     * 
     */
    public void showQuizPanel() {
    }

    public void showQuizPanel_gwt() {
    }

    public void showWelcomePanel() {
    }

    /**
     * Helper page to create the Prescription page
     * 
     * @TODO: get out of main
     * 
     */
    public void showPrescriptionPanel() {
    }

    public void showPrescriptionPanel_gwt() {
    }

    /**
     * Display the Auto Registration panel
     * 
     * Does not push onto history stack.
     * 
     */
    public void showAutoRegistration_gwt() {
    }

    /**
     * Provides helper method to load a resource into the current
     * PrespccriptionContext
     * 
     * @param type
     * @param file
     */
    static private void doResourceLoad(final String type, final String file) {
    }

    static public void showMotivationalVideo_Gwt() {
    }

    /**
     * Push a GWT method onto the global space for the app window
     * 
     * This will be called from CatchupMath.js:doResourceLoad
     * 
     */
    static private native void publishNative() /*-{
                                               $wnd.doLoadResource_Gwt = @hotmath.gwt.cm2.client.CatchupMath::doResourceLoad(Ljava/lang/String;Ljava/lang/String;);
                                               
                                               $wnd.showMotivationalVideo_Gwt = @hotmath.gwt.cm2.client.CatchupMath::showMotivationalVideo_Gwt();

                                               // Set global variable to signal that CM system has been initialized.
                                               // This is checked in CatchupMath.html to indicate that a loading error occurred.
                                               $wnd.__cmInitialized = true;
                                               }-*/;
}
