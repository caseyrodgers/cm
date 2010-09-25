package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;

public class GettingStartedGuideWindow extends CmWindow {

    public  GettingStartedGuideWindow() {
        setAutoHeight(true);
        setSize(640,480);

        setModal(true);
        setResizable(false);
        setStyleName("help-window");
        setHeading("Catchup Math Administrator Getting Started Guide");

        Frame frame = new Frame("/gwt-resources/cm-admin-getting-started-guide.html");
        DOM.setElementProperty(frame.getElement(), "frameBorder", "no"); // disable
        DOM.setElementPropertyInt(frame.getElement(), "border", 0); // disable
        DOM.setElementPropertyInt(frame.getElement(), "frameSpacing", 0); // disable
        frame.setSize("100%", "450px");
        
        
        Button closeBtn = new Button("Close");
        closeBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                GettingStartedGuideWindow.this.close();
            }
        });
        addButton(closeBtn);

        
        Button webinar = new Button("Teaching with Catchup Math");
        webinar.setStyleAttribute("margin","10px;");
        webinar.setToolTip("This supplement to our Getting Started Guide is for teachers and administrators.  It is divided into sections.");
        webinar.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                new WebinarWindow();
            }
        });
        add(webinar);
        add(frame);

        setVisible(true);
    }

    public void onClick(ClickEvent event) {
    }
}

