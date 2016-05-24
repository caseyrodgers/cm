package hotmath.gwt.solution_editor.client;

import com.google.gwt.user.client.ui.TextArea;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;

import hotmath.gwt.cm_tools.client.ui.GWindow;

public class MathMlTransformationDialog extends GWindow  {
	
    public MathMlTransformationDialog(String html, String logMessages) {
        super(true);
        
        setPixelSize(850,550);
        setHeadingText("Math ML Transformations");
        setClosable(true);
        setModal(true);
        setupUi();
        
        _logArea.setValue(logMessages);
        _textArea.setValue(html);
        
        setVisible(true);
    }
    
    
    TextArea _textArea = new TextArea();
    TextArea _logArea = new TextArea();
    
    private void setupUi() {
    	
        BorderLayoutData centerData = new BorderLayoutData();
        centerData.setSize(300);
        
        BorderLayoutContainer container = new BorderLayoutContainer();
        container.setBorders(true);
        
        _textArea.setReadOnly(true);
        _logArea.setReadOnly(true);
        
        container.setCenterWidget(_textArea);
        container.setNorthWidget(_logArea);
        setWidget(container);
    }

}