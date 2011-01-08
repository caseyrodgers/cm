
package hotmath.gwt.solution_editor.client;


import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.solution_editor.client.rpc.ServerFlusherAction;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
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
    public SolutionViewerFrame(String pid) {
        this.pid = pid;
        setSize(600,600);
        setHeading("Solution Viewer: " + pid);
        

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
        Frame frame = new Frame("/gwt-resources/solution_editor/tutor_view.html?pid=" + pid);
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
