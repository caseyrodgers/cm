package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.shared.client.CmShared;
import pl.rmalinowski.gwt2swf.client.ui.SWFSettings;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;
import pl.rmalinowski.gwt2swf.client.utils.PlayerVersion;
import pl.rmalinowski.gwt2swf.client.utils.SWFObjectUtil;

import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplVideo extends ResourceViewerContainer {

    String STYLE_NAME="resource-viewer-impl-video";
    
    public ResourceViewerImplVideo() {
        addStyleName(STYLE_NAME);
    }

    public Widget getResourcePanel(InmhItemData resource) {
        if(!SWFObjectUtil.isVersionIsValid(new PlayerVersion(CmShared.FLASH_MIN_VERSION))) {
            Html html = new Html(CmShared.FLASH_ALT_CONTENT);
            addResource(html,resource.getTitle());
        }
        else {
            String videoPath = "/help/flvs/tw/" + resource.getFile() + ".flv";
            SWFSettings s = new SWFSettings();
            s.setMinPlayerVersion(new PlayerVersion(9));
    
            SWFWidget swfWidget = new SWFWidget("flvplayer.swf?file=" + videoPath, 320, 240, s);
            swfWidget.setStyleName("flvplayer-widget");
            swfWidget.addParam("file", videoPath);
            Label title = new Label(resource.getTitle());
            title.setStyleName("resource-viewer-impl-video-title");
            addResource(swfWidget, resource.getTitle(),null);
        }

        return this;
    }

    public void removeResourcePanel() {

    }

    /** The viewo player has static height
     * 
     */
    public boolean shouldSetResourceContinerHeight() {
        return false;
    }

}
