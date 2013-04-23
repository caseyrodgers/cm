package hotmath.gwt.tutor_viewer.client.ui;

import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.tutor_viewer.client.rpc.SaveSolutionContextsAction;
import hotmath.gwt.tutor_viewer.client.ui.GenerateTutorContext.GenerateTutorContextCallback;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GenerateTutorContextPanel extends Composite {
    
    TutorWrapperPanel _tutorWrapper;
    String pid;
    FlowPanel _mainPanel = new FlowPanel();
    TextArea _textArea = new TextArea();
    final static String DEFAULT_CONFIG = "{\"limit\":10}";
    public String _jsonConfig;
    public GenerateTutorContextPanel() {
        _jsonConfig = CmGwtUtils.getQueryParameter("config");
        if(_jsonConfig == null) {
            _jsonConfig = DEFAULT_CONFIG;
        }

        _textArea.setSize("100%", "100px");
        _textArea.setReadOnly(true);
        _mainPanel.add(_textArea);
        
        initWidget(_mainPanel);
    }
    
    public Widget createContexts(final String pid) {

        addLogMessage("Creating solution contexts: " + pid + ", " + _jsonConfig);
        new GenerateTutorContext(pid,  _jsonConfig, new GenerateTutorContextCallback() {
            @Override
            public void contextsCreated(List<String> contexts) {
                saveContexts(pid, contexts);
            }
        });
        return this;
    }
    

    public void saveContexts(final String pid, final List<String> contexts) {
        
        SaveSolutionContextsAction action = new SaveSolutionContextsAction(pid, contexts);
        CmTutor.getCmService().execute(action, new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData result) {
                addViewerLinks(pid, contexts.size());
                
                addLogMessage("Saved " + contexts.size() + " contexts for " + pid);
            }
            @Override
            public void onFailure(Throwable caught) {
                caught.toString();
                Window.alert(caught.getMessage());
            }
        });
    }

    protected void addViewerLinks(String pid, int count) {
        VerticalPanel vp = new VerticalPanel();
        for(int i=0;i<count;i++) {
            String p = pid + "$" + (i+1);
            Anchor a = new Anchor(p);
            a.setHref("/tutor_viewer/TutorViewer.html?pid=" + p);
            a.setTarget("_blank");
            vp.add(a);
        }
        
        _mainPanel.add(vp);
    }
    
    private native String _nativeGenerateContext(String pid, String js, String jsonConfig) /*-{
    
        $wnd.gwt_solutionHasBeenInitialized = function() {};
        
        var that = this;
        return $wnd.TutorManager.generateContext(pid, js, jsonConfig);
    }-*/;
    
    
    protected void addLogMessage(String msg) {
        _textArea.setText(_textArea.getText() + "\n" + msg);
        _textArea.getElement().setScrollTop(_textArea.getElement().getScrollHeight());
    }
    
}
