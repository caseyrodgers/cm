package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;

public class RecentUpdatesWindow extends CmWindow {

    public  RecentUpdatesWindow() {
        setAutoHeight(true);
        setSize(640,480);

        setModal(true);
        setResizable(false);
        setStyleName("help-window");
        setHeading("Catchup Math Recent Updates");

        Frame frame = new Frame("/gwt-resources/cm-admin-recent-updates.html");
        DOM.setElementProperty(frame.getElement(), "frameBorder", "no"); // disable
        DOM.setElementPropertyInt(frame.getElement(), "border", 0); // disable
        DOM.setElementPropertyInt(frame.getElement(), "frameSpacing", 0); // disable
        frame.setSize("100%", "450px");
        
        
        Button closeBtn = new Button("Close");
        closeBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                RecentUpdatesWindow.this.close();
            }
        });
        addButton(closeBtn);

        add(frame);

        setVisible(true);
    }

    public void onClick(ClickEvent event) {
    }
}

