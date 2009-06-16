package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Frame;


public class HelpWindow extends CmWindow {

    public  HelpWindow() {
        setAutoHeight(true);
        setSize(640,480);
        
        setModal(true);
        setResizable(false);
        setStyleName("help-window");
        setHeading("Catchup Math Administration Help Window");
        
        
        Frame frame = new Frame("/gwt-resources/cm-admin-help.html");
        frame.setSize("100%", "450px");
        
        Button closeBtn = new Button("Close");
        closeBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                HelpWindow.this.close();
            }
        });
        addButton(closeBtn);

        add(frame);
        
        setVisible(true);
    }

    public void onClick(ClickEvent event) {
    }

    
    /** Return the current version number
     * 
     * @todo: externalize this parameter 
     * 
     * 
     * @return
     */
    private String getVersion() {
        return "1.2b";
    }
}

