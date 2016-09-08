package hotmath.gwt.tutor_viewer.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.tutor_viewer.client.ui.ValidateTutorContext.ValidateTutorContextCallback;

public class ValidateTutorContextPanel extends Composite {
    
    TutorWrapperPanel _tutorWrapper;
    String pid;
    FlowPanel _mainPanel = new FlowPanel();
    TextArea _textArea = new TextArea();
    final static String DEFAULT_CONFIG = "{\"limit\":10}";
    public String _jsonConfig;
    public ValidateTutorContextPanel() {
    	this.pid = pid;
    	
        _textArea.setSize("100%", "100px");
        
        initWidget(_mainPanel);
        _mainPanel.add(_textArea);
        
        addLogMessage("Validating solution all Global Solution Contexts ...");
    }
    
    private native String _nativeGenerateContext(String pid, String js, String jsonConfig) /*-{
    
        $wnd.gwt_solutionHasBeenInitialized = function(tutorWrapper) {};
        
        var that = this;
        return $wnd.TutorManager.generateContext(pid, js, jsonConfig);
    }-*/;
    
    
    protected void addLogMessage(String msg) {
        _textArea.setText(_textArea.getText() + "\n" + msg);
        _textArea.getElement().setScrollTop(_textArea.getElement().getScrollHeight());
    }
    
    
    
    
    public Widget validateContexts(final String pid) {

        addLogMessage("Validating solution contexts: " + pid + ", " + _jsonConfig);
        new ValidateTutorContext(pid, new ValidateTutorContextCallback() {
            @Override
            public void contextCreated() {
                
            }
            
            @Override
            public void logMessage(String msg) {
            	addLogMessage(msg);
            }
        });
        return this;
    }
}
