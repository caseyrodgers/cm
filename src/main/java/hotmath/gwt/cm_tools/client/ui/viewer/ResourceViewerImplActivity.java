package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.shared.client.CmShared;
import pl.rmalinowski.gwt2swf.client.ui.SWFSettings;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;
import pl.rmalinowski.gwt2swf.client.utils.PlayerVersion;
import pl.rmalinowski.gwt2swf.client.utils.SWFObjectUtil;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplActivity extends ResourceViewerContainer {
    
    public ResourceViewerImplActivity() throws Exception {
        addStyleName("resource-viewer-impl-activity");
        setScrollMode(Scroll.AUTOY);
    }
	
	
	public Widget getResourcePanel(InmhItemData resource) {
	    if(!SWFObjectUtil.isVersionIsValid(new PlayerVersion(CmShared.FLASH_MIN_VERSION))) {
             Html html = new Html(CmShared.FLASH_ALT_CONTENT);
             addResource(html,resource.getTitle());
	    }
	    else {
    	    SWFSettings s = new SWFSettings();
    	    s.setMinPlayerVersion(new PlayerVersion(CmShared.FLASH_MIN_VERSION));
    	    SWFWidget swfWidget = new SWFWidget(resource.getFile(),"530px","450px",s);
    	    swfWidget.setStyleName("activity-widget");
    	    addResource(swfWidget,resource.getTitle());
	    }
 	   
	    return this;
	}
	
	public double getAllowedVerticalSpace() {
        return .90;
    }
}

