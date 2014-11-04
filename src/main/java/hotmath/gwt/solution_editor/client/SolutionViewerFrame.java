
package hotmath.gwt.solution_editor.client;


import hotmath.gwt.cm_core.client.util.CmAlertify.PromptCallback;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.solution_editor.client.rpc.ServerFlusherAction;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;


/** Loads a single solution as a complete tutor environment
 * 
 * @author casey
 *
 */
public class SolutionViewerFrame extends GWindow {
    String pid;
    static String _config;
    public SolutionViewerFrame(String pid) {
        super(false);
        this.pid = pid;
        setPixelSize(600,600);
        setTitleLocal();
        
        getHeader().addTool(new TextButton("Configure",new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                CmMessageBox.prompt("Solution Config", "Enter solution config JSON (" + _config + ")","",new PromptCallback() {
                    @Override
                    public void promptValue(String value) {
                        _config = value;
                        setTitleLocal();
                        showFrame();
                    }
                });
            }
        }));

        
        addButton(new TextButton("Close", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        
        flushServerThenShowFrame();
        
        setVisible(true);
    }
    
    private void setTitleLocal() {
        setHeadingText("Solution Viewer: " + pid + "(config=" + _config + ")");
    }
    
    
    private void flushServerThenShowFrame() {
        ServerFlusherAction serverFlushAction = new ServerFlusherAction(null);
        SolutionEditor.getCmService().execute(serverFlushAction, new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData arg0) {
                showFrame();
            }
            @Override
            public void onFailure(Throwable arg0) {
                arg0.printStackTrace();
                add(new HTML("Error loading solution: " + arg0.getMessage()));
            }
        });
    }
    
    private void showFrame() {
        String url = "/tutor_viewer/TutorViewer.html?pid=" + pid;
        if(_config != null && _config.length() > 0) {
            url += "&config=" + _config;
        }
        Frame frame = new Frame(url);
        frame.setSize("100%", "99%");
        DOM.setElementProperty(frame.getElement(), "frameBorder", "no"); // disable
        DOM.setElementPropertyInt(frame.getElement(), "border", 0); // disable
        DOM.setElementPropertyInt(frame.getElement(), "frameSpacing", 0); // disable
        DOM.setElementProperty(frame.getElement(), "scrolling", "yes"); // disable   
        
        clear();
        setWidget(frame); 
        forceLayout();
    }
}
