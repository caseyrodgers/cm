package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceViewerState;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

 public class ResourceViewerImplFlash extends SimpleContainer implements CmResourcePanel {
    
    private InmhItemData item;

    public ResourceViewerImplFlash() {
        
    }
    
    @Override
    public Integer getOptimalHeight() {
        return 450;
    }
    
    
    @Override
    public Integer getOptimalWidth() {
        return 530;
    }

    @Override
    public void addResource(Widget w, String title) {
        setWidget(w);
    }

    @Override
    public Boolean allowMaximize() {
        return true;
    }

    @Override
    public Boolean allowClose() {
        return true;
    }

    @Override
    public Boolean showContainer() {
        return true;
    }

    @Override
    public Integer getMinHeight() {
        return 500;
    }

    @Override
    public Widget getResourcePanel() {
        return this;
    }

    @Override
    public void removeResourcePanel() {
        // empty
    }

    @Override
    public String getContainerStyleName() {
        return null;
    }

    @Override
    public InmhItemData getResourceItem() {
        return this.item;
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
    public ResourceViewerState getInitialMode() {
        return ResourceViewerState.MAXIMIZED;
    }

    @Override
    public boolean shouldContainerBeTransparent() {
        return false;
    }
}
