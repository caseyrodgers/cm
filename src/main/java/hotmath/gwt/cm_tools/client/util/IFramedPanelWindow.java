package hotmath.gwt.cm_tools.client.util;

import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;

public class IFramedPanelWindow extends GWindow {

    public IFramedPanelWindow(String url) {
        super(true);
        setModal(true);
        setPixelSize(500,400);
        setResizable(true);
        setMaximizable(true);
        
        
        if(url.startsWith("/")) {
        	url = CmShared.getHostName() +  url;
        }
        
        // url = "http://127.0.0.1:8080/cm_admin/launch.jsp?load=student_registration:28001";
        url = "http://127.0.0.1:8080/cm_admin/launch.jsp";
        Window.alert(url);
        Frame frame = new Frame(url);
        DOM.setElementProperty(frame.getElement(), "frameBorder", "no"); // disable
        DOM.setElementPropertyInt(frame.getElement(), "border", 0); // disable
        DOM.setElementPropertyInt(frame.getElement(), "frameSpacing", 0); // disable
        DOM.setElementProperty(frame.getElement(), "scrolling", "yes"); // disable
        
        
        setWidget(frame);
    }

}
