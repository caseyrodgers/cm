package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class CmResourceViewerImplVideo extends Composite implements CmMobileResourceViewer {
    
    interface VideoPanelBinder extends UiBinder<Widget, CmResourceViewerImplVideo> {}
    private static VideoPanelBinder uiBinder = GWT.create(VideoPanelBinder.class);
    
    @UiField VerticalPanel mainPanel;
   
    public CmResourceViewerImplVideo() {
        initWidget(uiBinder.createAndBindUi(this));
        
        // String html = "<object width=\"480\" height=\"320\"><param name=\"movie\" value=\"http://www.youtube.com/v/s80J2dAUUyI?fs=1&amp;hl=en_US\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/s80J2dAUUyI?fs=1&amp;hl=en_US\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"480\" height=\"385\"></embed></object>";
        
        
        String html = "<iframe height='335' width='360' src='/gwt-resources/mobile_view_video.html'></iframe>";
        mainPanel.add(new HTML(html));
    }
    
    private native void loadVideo() /*-{
    }-*/;
    
    
    @Override
    public Widget getViewer(final InmhItemData item) {
        return this;
    }
}
