package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.shared.client.CmShared;
import pl.rmalinowski.gwt2swf.client.ui.SWFSettings;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;
import pl.rmalinowski.gwt2swf.client.utils.PlayerVersion;
import pl.rmalinowski.gwt2swf.client.utils.SWFObjectUtil;

import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplVideo extends ResourceViewerImplFlash {

    String STYLE_NAME="resource-viewer-impl-video";
    
    public ResourceViewerImplVideo() {
        addStyleName(STYLE_NAME);
    }

    public Widget getResourcePanel() {
        
        if(!SWFObjectUtil.isVersionIsValid(new PlayerVersion(CmShared.FLASH_MIN_VERSION))) {
            Html html = new Html(CmShared.FLASH_ALT_CONTENT);
            addResource(html,getResourceItem().getTitle());
        }
        else {
            String videoPath = "/help/flvs/tw/" + getResourceItem().getFile() + ".flv";
            SWFSettings s = new SWFSettings();
            s.setMinPlayerVersion(new PlayerVersion(9));
    
            SWFWidget swfWidget = new SWFWidget("flvplayer.swf?file=" + videoPath, "100%", "100%", s);
            swfWidget.addParam("file", videoPath);
            addResource(swfWidget, getResourceItem().getTitle(),null);
        }

        return this;
    }
    
    
    @Override
    public Integer getOptimalHeight() {
        return 240;
    }
    
    @Override
    public Integer getOptimalWidth() {
        return 320;
    }
}
