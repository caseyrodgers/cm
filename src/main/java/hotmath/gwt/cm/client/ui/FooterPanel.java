package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.util.ServerRequest;
import hotmath.gwt.cm.client.util.UserInfo;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.http.client.URL;
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
        }
        
        html += "</ul>" + 
                "<div>Brought to you by Hotmath.com</div>";

        add(new Html(html));
    }

    static public void showFeedbackPanel_Gwt() {
        MessageBox.prompt("Feedback","Enter Catchup-Math feedback.",true,new Listener<MessageBoxEvent> () {
            public void handleEvent(MessageBoxEvent be) {
                String value = be.getValue();
                
                
                String url = "/testsets/util/_save_feedback.jsp?comment=" + URL.encode(value) +
                    "&state_info=" + getFeedbackStateInfo();
                ServerRequest.makeHttpRequest(url, new CmAsyncRequestImplDefault() {
                    public void requestComplete(String requestData) {
                        //CatchupMath.getThisInstance().showAlert("Feedback success");
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
    static private void resetProgram_Gwt() {
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.resetUser(UserInfo.getInstance().getUid(),new AsyncCallback() {
            public void onSuccess(Object result) {
             MessageBox.confirm("User Reset", "Are you sure you want to reset this user's program?",new Listener<MessageBoxEvent>() {
                  @Override
                public void handleEvent(MessageBoxEvent be) {
                    if(be.getButtonClicked().getText().equalsIgnoreCase("Yes"))
                       Window.Location.reload();
                }
             });
            }
            public void onFailure(Throwable caught) {
                CatchupMath.showAlert(caught.getMessage());
            }
        });
    }
    
    private static void showPrescriptionSession_Gwt() {
        if(UserInfo.getInstance() == null || UserInfo.getInstance().getRunId() == 0) {
            CatchupMath.showAlert("No user prescription");
            return;
        }
        String url = "http://hotmath.kattare.com/testsets/util/_get_assessment_data.jsp?run_id=" + UserInfo.getInstance().getRunId();
        Window.open(url, "_new", "height=480,width=640,status=yes");
    }
    
    private static void showPrescriptionData_Gwt() {
        if(UserInfo.getInstance() == null || UserInfo.getInstance().getRunId() == 0) {
            CatchupMath.showAlert("No user prescription");
            return;
        }
        String url = "http://hotmath.kattare.com/testsets/util/_get_prescription.jsp?run_id=" + UserInfo.getInstance().getRunId();
        Window.open(url, "_blank", "height=480,width=640,status=yes");        
    }
    
    
    /** Define JSNI methods to expose feedback services
     * 
     */
    static private native void publishNative() /*-{
       $wnd.showFeedbackPanel_Gwt = @hotmath.gwt.cm.client.ui.FooterPanel::showFeedbackPanel_Gwt();
       $wnd.resetProgram_Gwt = @hotmath.gwt.cm.client.ui.FooterPanel::resetProgram_Gwt();
       $wnd.showPrescriptionData_Gwt = @hotmath.gwt.cm.client.ui.FooterPanel::showPrescriptionData_Gwt();
       $wnd.showPrescriptionSession_Gwt = @hotmath.gwt.cm.client.ui.FooterPanel::showPrescriptionSession_Gwt();
    }-*/;
    
    static {
        publishNative();
    }

}
