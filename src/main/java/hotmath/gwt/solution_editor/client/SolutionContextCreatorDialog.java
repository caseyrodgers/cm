package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

public class SolutionContextCreatorDialog extends GWindow{
    
    private String pid;

    VerticalLayoutContainer _main = new VerticalLayoutContainer();
    TextField configuration = new TextField();
    Frame _contextsFrame; 
    public SolutionContextCreatorDialog(String pid) {
        super(true);
        this.pid = pid;
        setPixelSize(640,480);
        setHeadingText("Generate Solution Contexts");
        
        BorderLayoutContainer borderLayout = new BorderLayoutContainer();
        borderLayout.setNorthWidget(createHeaderPanel(),new BorderLayoutData(100));
        borderLayout.setCenterWidget(createFramePanel());

        setWidget(borderLayout);
        
        showExistingContexts();
        setVisible(true);
    }

    private IsWidget createFramePanel() {
        _contextsFrame = new Frame();
        _contextsFrame.setSize("100%","150px");  
        return _contextsFrame;
        
        
    }

    private IsWidget createHeaderPanel() {
        FramedPanel framedPanel = new FramedPanel();
        framedPanel.setHeaderVisible(false);
        configuration.setText("{limit: 10}");
        configuration.setWidth(200);
        framedPanel.add(new FieldLabel(configuration, "Configuration"));
        framedPanel.addButton(createGenerateButton());
        framedPanel.addButton(createValidateButton());
        framedPanel.addButton(createDetailsButton());
        return framedPanel;
    }

    private Widget createGenerateButton() {
        return new TextButton("Generate", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doGenerateContexts();
            }
        });
    }
        
    private Widget createValidateButton() {
         return new TextButton("Validate", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doValidateContexts();
            }
        });
    }
    
    
    private Widget createDetailsButton() {
        return new TextButton("Details", new SelectHandler() {
           @Override
           public void onSelect(SelectEvent event) {
               doShowExistingContextsDetails();
           }
       });
   }
    
    
    private void doShowExistingContextsDetails() {
        String args= "show_context_details=true&pid=" + pid;
        _contextsFrame.setUrl("/tutor_viewer/TutorViewer.html?" + args);
    }

    private void showExistingContexts() {
        String args= "show_context=true&pid=" + pid;
        _contextsFrame.setUrl("/tutor_viewer/TutorViewer.html?" + args);
    }
    
    private void doGenerateContexts() {
        String args= "generate_context=true&pid=" + pid + "&config="+configuration.getText();
        _contextsFrame.setUrl("/tutor_viewer/TutorViewer.html?" + args);
    }
    
    private void doValidateContexts() {
        String args= "validate_context=true&pid=" + pid + "&config="+configuration.getText();
        _contextsFrame.setUrl("/tutor_viewer/TutorViewer.html?" + args);
    }
    
}
