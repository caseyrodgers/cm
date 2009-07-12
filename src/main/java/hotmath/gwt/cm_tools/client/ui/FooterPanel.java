package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.util.UserInfo;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FooterPanel extends LayoutContainer {

    public FooterPanel() {
        setStyleName("footer-panel");
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);

        String html = "<ul class='h-menu'>" + 
                      "<li><a href='#' onclick='showFeedbackPanel_Gwt();'>Feedback</a></li>" +
                      "<li>About Us</li>" + 
                      "<li>Privacy</li>" + 
                      "<li>Press</li>" + 
                      "<li>Contact</li>";
        
        if(CmShared.getQueryParameter("debug") != null) {
            html +=   "<li><a href='#' onclick='resetProgram_Gwt();'>Reset</a></li>";
            html += "<li><a href='#' onclick='showPrescriptionData_Gwt();'>prescription data</a>";
            html += "<li><a href='#' onclick='showPrescriptionSession_Gwt();'>prescription sessions</a>";
            html += "<li><a href='#' onclick='startAutoTest_Gwt();'>Auto Test</a>";
        }
        
        html += "</ul>" + 
                "<div>Brought to you by Hotmath.com</div>";

        add(new Html(html));
    }

    static public void showFeedbackPanel_Gwt() {
        MessageBox.prompt("Feedback","Enter Catchup-Math feedback.",true,new Listener<MessageBoxEvent> () {
            public void handleEvent(MessageBoxEvent be) {
                String value = be.getValue();
                if(value == null || value.length() == 0)
                    return;
                
                PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
                s.saveFeedback(value, "", getFeedbackStateInfo(),new AsyncCallback() {
                    public void onSuccess(Object result) {
                        System.out.println("Feedback saved");
                    }
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }
                });
                        
            }
        });
    }

    /** Return string that represents current state of CM
     * 
     */
    static private String getFeedbackStateInfo() {
       return ContextController.getInstance().toString(); 
    }
    
    /** Reset the current user's path through CM
     * 
     */
    static public void resetProgram_Gwt() {
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.resetUser(UserInfo.getInstance().getUid(),new AsyncCallback() {
            public void onSuccess(Object result) {
                refreshPage();
            }
            public void onFailure(Throwable caught) {
                CatchupMathTools.showAlert(caught.getMessage());
            }
        });
    }
    
    /** Reload current page
     * 
     */
    static public void refreshPage() {
        Window.Location.reload();        
    }
    
    
    private static void showPrescriptionSession_Gwt() {
        if(UserInfo.getInstance() == null || UserInfo.getInstance().getRunId() == 0) {
            CatchupMathTools.showAlert("No user prescription");
            return;
        }
        String url = "http://hotmath.kattare.com/testsets/util/_get_assessment_data.jsp?run_id=" + UserInfo.getInstance().getRunId();
        Window.open(url, "_new", "height=480,width=640,status=yes");
    }
    
    private static void showPrescriptionData_Gwt() {
        if(UserInfo.getInstance() == null || UserInfo.getInstance().getRunId() == 0) {
            CatchupMathTools.showAlert("No user prescription");
            return;
        }
        String url = "http://hotmath.kattare.com/testsets/util/_get_prescription.jsp?run_id=" + UserInfo.getInstance().getRunId();
        Window.open(url, "_blank", "height=480,width=640,status=yes");        
    }
    
    private static void startAutoTest_Gwt() {
        AutoTestWindow.getInstance().setVisible(true);
        
        UserInfo.getInstance().setAutoTestMode(true);
        ContextController.getInstance().getTheContext().runAutoTest();
    }
    
    /** Define JSNI methods to expose feedback services
     * 
     */
    static private native void publishNative() /*-{
       $wnd.showFeedbackPanel_Gwt = @hotmath.gwt.cm_tools.client.ui.FooterPanel::showFeedbackPanel_Gwt();
       $wnd.resetProgram_Gwt = @hotmath.gwt.cm_tools.client.ui.FooterPanel::resetProgram_Gwt();
       $wnd.showPrescriptionData_Gwt = @hotmath.gwt.cm_tools.client.ui.FooterPanel::showPrescriptionData_Gwt();
       $wnd.showPrescriptionSession_Gwt = @hotmath.gwt.cm_tools.client.ui.FooterPanel::showPrescriptionSession_Gwt();
       $wnd.startAutoTest_Gwt = @hotmath.gwt.cm_tools.client.ui.FooterPanel::startAutoTest_Gwt();
    }-*/;
    
    static {
        publishNative();
    }

}
