package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.util.ServerRequest;
import hotmath.gwt.cm.client.util.UserInfo;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

public class FooterPanel extends LayoutContainer {

    public FooterPanel() {
        setStyleName("footer-panel");
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);

        String html = "<ul class='h-menu'>" + 
                      "<li><a href='#' onclick='showFeedbackPanel_Gwt();'>Feedback</a></li>" +
                      "<li><a href='#' onclick='resetProgram_Gwt();'>Reset</a></li>" + 
                      "<li>About Us</li>" + 
                      "<li>Privacy</li>" + 
                      "<li>Press</li>" + 
                      "<li>Contact</li>" + 
                      "</ul>" + 
                      "<div>Brought to you by Hotmath.com</div>";

        add(new HTML(html));
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
    
    /** Define JSNI methods to expose feedback services
     * 
     */
    static private native void publishNative() /*-{
       $wnd.showFeedbackPanel_Gwt = @hotmath.gwt.cm.client.ui.FooterPanel::showFeedbackPanel_Gwt();
       $wnd.resetProgram_Gwt = @hotmath.gwt.cm.client.ui.FooterPanel::resetProgram_Gwt();
    }-*/;
    
    static {
        publishNative();
    }

}
