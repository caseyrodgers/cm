package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceViewerState;

import java.util.List;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;


/** 
 *   Default resource container
 *   
 *   panels call addResource to insert component
 *   
 * @author casey
 *
 */
public class ResourceViewerImplWebLink extends SimpleContainer implements CmResourcePanel {
    
    InmhItemData item;
    
    
    public ResourceViewerImplWebLink() {
        addStyleName("resource-viewer-container");
    }
    

    /**
     * Add the resource to the center
     * 
     * @param w
     */
    public void addResource(Widget w, String title) {
        addResource(w, title, null);
    }

    public void addResource(Widget w, String title, String styleName) {
        clear();
        setWidget(w);
    }

    @Override
    public Boolean allowMaximize() {
        return true;
    }

    @Override
    public Integer getMinHeight() {
        return 400;
    }

    @Override
    public Integer getOptimalHeight() {
        return -1;
    }

    @Override
    public Integer getOptimalWidth() {
        return 500;
    }

    @Override
    public Boolean showContainer() {
        return true;
    }

    @Override
    public String getContainerStyleName() {
        return null;
    }

    @Override
    public InmhItemData getResourceItem() {
        return item;
    }
    
    @Override
    public Boolean allowClose() {
        return true;
    }

    @Override
    public ResourceViewerState getInitialMode() {
        return ResourceViewerState.MAXIMIZED;
    }


    @Override
    public Widget getResourcePanel() {
        
        Frame frame = new Frame();
        String url = item.getFile();
        frame.setUrl(url);
        
        addResource(frame,item.getTitle());
        
        return this;
    }

    @Override
    public void removeResourcePanel() {
        clear();
    }

    
    @Override
    public void setResourceItem(InmhItemData item) {
        this.item = item;
    }
    
    @Override
    public List<Widget> getContainerTools() {
        return null;
    }
    
    @Override
    public boolean shouldContainerBeTransparent() {
        return false;
    }
}
