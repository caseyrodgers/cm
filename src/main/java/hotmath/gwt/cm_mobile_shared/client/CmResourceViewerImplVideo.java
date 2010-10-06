package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class CmResourceViewerImplVideo extends Composite implements CmMobileResourceViewer {
    
    interface VideoPanelBinder extends UiBinder<Widget, CmResourceViewerImplVideo> {}
    private static VideoPanelBinder uiBinder = GWT.create(VideoPanelBinder.class);
    
    @UiField VerticalPanel mainPanel;
   
    public CmResourceViewerImplVideo() {
        initWidget(uiBinder.createAndBindUi(this));
        
        Frame frame = new Frame();
        DOM.setElementProperty(frame.getElement(), "frameBorder", "no"); // disable
        DOM.setElementPropertyInt(frame.getElement(), "border", 0); // disable
        DOM.setElementPropertyInt(frame.getElement(), "frameSpacing", 0); // disable
        DOM.setElementProperty(frame.getElement(), "scrolling", "no"); // disable
        
        frame.setSize("500px","650px");
        frame.setUrl("/gwt-resources/mobile_view_video.html");
        mainPanel.add(frame);
    }
    
    private native void loadVideo() /*-{
}-*/;
    @Override
    public Widget getViewer(final InmhItemData item) {
        return this;
    }
}
