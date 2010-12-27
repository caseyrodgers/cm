
package hotmath.gwt.solution_editor.client;


import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.GetMobileSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.solution_editor.client.rpc.ServerFlusherAction;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


/** Loads a single solution as a complete tutor environment
 * 
 * @author casey
 *
 */
public class SolutionViewer extends LayoutContainer {
    
    String pid;
    
    public SolutionViewer() {
        add(new Label("No solution loaded"));
    }
    
    public void loadSolution(final String pid) {
        this.pid = pid;
        
        ServerFlusherAction serverFlushAction = new ServerFlusherAction(null);
        SolutionEditor.getCmService().execute(serverFlushAction, new AsyncCallback<RpcData>() {
            
            @Override
            public void onSuccess(RpcData arg0) {
                showSolution(pid);
            }
            
            @Override
            public void onFailure(Throwable arg0) {
                arg0.printStackTrace();
                Window.alert(arg0.getMessage());
            }
        });
    }
    
    private void showSolution(String pid) {
        CmResourceViewerImplSolution tutor = new CmResourceViewerImplSolution();
        
        InmhItemData iid = new InmhItemData("practice",pid,"");
        removeAll();
        add(tutor.getViewer(iid));
        setScrollMode(Scroll.AUTO);
        
        layout();
        
    }
    
    
    static private void showMessage(String msg) {
        Window.alert("GWT: " + msg);
    }    
    
}

class CmResourceViewerImplSolution extends LayoutContainer  {
   
    public CmResourceViewerImplSolution() {
        
    }
    
    public Widget getViewer(final InmhItemData item) {
        SolutionEditor._status.setText("Loading solution: " + item.getFile());
        GetMobileSolutionAction action = new GetMobileSolutionAction(UserInfo.getInstance().getUid(),item.getFile());
        SolutionEditor.getCmService().execute(action, new AsyncCallback<SolutionResponse>() {
            @Override
            public void onSuccess(SolutionResponse result) {
                SolutionEditor._status.setText("");
                HTML html = new HTML(result.getTutorHtml());
                add(html);
                layout();
                initializeTutor(item.getFile(),result.getSolutionData(),"Solution", false,false);
            }

            @Override
            public void onFailure(Throwable caught) {
                SolutionEditor._status.setText("");
                caught.printStackTrace();
                add(new HTML("Could not load solution: " + caught.getMessage()));               
            }
        });
        
        return this;
    }

    
    private native void initializeTutor(String pid, String solutionDataJs, String title, boolean hasShowWork,boolean shouldExpandSolution) /*-{
                                          $wnd.TutorManager.initializeTutor(pid, solutionDataJs,title,hasShowWork,shouldExpandSolution);
                                          $wnd.gwt_showMessage = @hotmath.gwt.solution_editor.client.SolutionViewer::showMessage(Ljava/lang/String;);
                                          }-*/;
}
