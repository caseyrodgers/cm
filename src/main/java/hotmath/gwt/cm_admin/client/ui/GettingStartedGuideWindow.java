package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Frame;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class GettingStartedGuideWindow extends GWindow {

    public  GettingStartedGuideWindow() {
    	super(true);
        //setAutoHeight(true);
        setPixelSize(640,480);

        setModal(true);
        setResizable(true);
        setMaximizable(true);
        
        setHeadingText("Catchup Math Administrator Getting Started Guide");

        Frame frame = new Frame("/gwt-resources/cm-admin-getting-started-guide.html");
        frame.setSize("100%", "450px");
        
        TextButton webinar = new TextButton("Teaching with Catchup Math Videos", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showTrainingVideosPage();
            }
        });
        //webinar.setStyleAttribute("margin","10px;");
        webinar.setToolTip("These videos supplement our Getting Started Guide for teachers and administrators");

        addTool(webinar);
        setWidget(frame);
        setVisible(true);
    }
    
    private native void showTrainingVideosPage() /*-{
        var tv = window.open('/training-videos');
        tv.focus();
    }-*/;

    public void onClick(ClickEvent event) {
    }
}

