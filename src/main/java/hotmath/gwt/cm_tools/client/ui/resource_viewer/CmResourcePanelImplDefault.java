package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceViewerState;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;


/** 
 *   Default resource container
 *   
 *   panels call addResource to insert component
 *   
 * @author casey
 *
 */
public class CmResourcePanelImplDefault extends FlowLayoutContainer implements CmResourcePanel {
    
    InmhItemData item;
    
    
    public CmResourcePanelImplDefault() {
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
        add(w);
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_FORCE_GUI_REFRESH));
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
        String url = item.getFile();
        String html = "<iframe frameborder='no' width='100%' height='400px' src='" + url + "'></iframe>";
        
        addResource(new HTML(html),item.getTitle());
        
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
