package hotmath.gwt.cm_core.client.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;

public class LoggerWindow extends DialogBox {

    FlowPanel flowPanel;
    
    public LoggerWindow() {
        // Set the dialog box's caption.
        setText("My First Dialog");

        // Enable animation.
        setAnimationEnabled(true);

        // Enable glass background.
        setGlassEnabled(true);

        // DialogBox is a SimplePanel, so you have to set its widget property to
        // whatever you want its contents to be.
        Button ok = new Button("OK");
        ok.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        setWidget(ok);
        
        
        flowPanel = new FlowPanel();
        ScrollPanel scroller = new ScrollPanel(flowPanel);
        setWidget(scroller);
    }

    private static LoggerWindow __instance;
    public static LoggerWindow getInstance() {
        if(__instance == null) {
            __instance = new LoggerWindow();
        }
        return __instance;
    }
    
    
    public void info(String msg) {
        flowPanel.add(new HTML(msg));
    }
}
