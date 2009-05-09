package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.ui.CmMainPanel;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplVideo extends ResourceViewerContainer {

    String STYLE_NAME="resource-viewer-impl-video";
    
    public ResourceViewerImplVideo() {
        addStyleName(STYLE_NAME);
    }

    public Widget getResourcePanel(InmhItemData resource) {
        String videoPath = "/help/flvs/tw/" + resource.getFile() + ".flv";
        SWFWidget swfWidget = new SWFWidget("flvplayer.swf?file=" + videoPath, 320, 240);
        swfWidget.setStyleName("flvplayer-widget");
        swfWidget.addParam("file", videoPath);
        Label title = new Label(resource.getTitle());
        title.setStyleName("resource-viewer-impl-video-title");
        addResource(swfWidget, resource.getTitle(),null);

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
