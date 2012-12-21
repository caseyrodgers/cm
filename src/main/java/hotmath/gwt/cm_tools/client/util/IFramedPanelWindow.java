package hotmath.gwt.cm_tools.client.util;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;

public class IFramedPanelWindow extends GWindow {

    public IFramedPanelWindow(String url) {
        super(true);
        setModal(true);
        setPixelSize(500,400);
        setResizable(true);
        setMaximizable(true);
        
        
        Frame frame = new Frame(url);
        DOM.setElementProperty(frame.getElement(), "frameBorder", "no"); // disable
        DOM.setElementPropertyInt(frame.getElement(), "border", 0); // disable
        DOM.setElementPropertyInt(frame.getElement(), "frameSpacing", 0); // disable
        DOM.setElementProperty(frame.getElement(), "scrolling", "yes"); // disable
        setWidget(frame);
    }

}
