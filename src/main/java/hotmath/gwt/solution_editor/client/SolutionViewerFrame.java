
package hotmath.gwt.solution_editor.client;


import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.solution_editor.client.rpc.ServerFlusherAction;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;


/** Loads a single solution as a complete tutor environment
 * 
 * @author casey
 *
 */
public class SolutionViewerFrame extends Window {
    String pid;
    static String _config;
    public SolutionViewerFrame(String pid) {
        this.pid = pid;
        setSize(600,600);
        setHeading("Solution Viewer: " + pid);
        
        getHeader().addTool(new Button("Configure",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                MessageBox mb = MessageBox.prompt("Solution Config", "Enter solution config JSON (" + _config + ")");
                mb.addCallback(new Listener<MessageBoxEvent>() {
                    @Override
                    public void handleEvent(MessageBoxEvent be) {
                        _config = be.getValue();
                    }
                });
            }
        }));

        setLayout(new FitLayout());
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        }));
        
        flushServerThenShowFrame();
        
        setVisible(true);
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
                add(new Html("Error loading solution: " + arg0.getMessage()));
            }
        });
    }
    
    private void showFrame() {
        String url = "/gwt-resources/solution_editor/tutor_view.html?pid=" + pid;
        if(_config != null && _config.length() > 0) {
            url += "&config=" + _config;
        }
        Frame frame = new Frame(url);
        frame.setSize("100%", "99%");
        DOM.setElementProperty(frame.getElement(), "frameBorder", "no"); // disable
        DOM.setElementPropertyInt(frame.getElement(), "border", 0); // disable
        DOM.setElementPropertyInt(frame.getElement(), "frameSpacing", 0); // disable
        DOM.setElementProperty(frame.getElement(), "scrolling", "yes"); // disable   
        
        removeAll();
        add(frame); 
        
        layout();
    }
}
