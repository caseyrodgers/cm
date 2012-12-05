package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

public class SolutionContextCreatorDialog extends GWindow{
    
    private String pid;



    VerticalLayoutContainer _main = new VerticalLayoutContainer();
    TextField configuration = new TextField();
    Frame contextsFrame = new Frame();
    public SolutionContextCreatorDialog(String pid) {
        super(true);
        this.pid = pid;
        
        setPixelSize(500,300);
        
        setHeadingText("Generate Solution Contexts");
        FramedPanel framedPanel = new FramedPanel();
        _main.add(framedPanel);
        
        configuration.setText("{limit: 10}");
        configuration.setWidth(200);
        framedPanel.add(new FieldLabel(configuration, "Configuration"));
        
        framedPanel.addButton(createGenerateButton());
        contextsFrame.setSize("100%","150px");  
        _main.add(contextsFrame);
        setWidget(_main);
        setVisible(true);
    }

    private Widget createGenerateButton() {
        return new TextButton("Generate", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doGenerateContexts();
            }
        });
    }
    
    
    
    private void doGenerateContexts() {
        String args= "generate_context=true&pid=" + pid + "&config="+configuration.getText();
        contextsFrame.setUrl("/tutor_viewer/TutorViewer.html?" + args);
    }
    
}
