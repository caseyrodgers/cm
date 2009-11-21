package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelContainer.ResourceViewerState;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Widget;

public class CmResourcePanelImplDefault extends LayoutContainer implements CmResourcePanel {
    
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
        
        removeAll();
        
        setLayout(new FitLayout());
        add(w);
        setTitle(title);
        
        layout();
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
        
        addResource(new Html(html),item.getTitle());
        
        return this;
    }

    @Override
    public void removeResourcePanel() {
        removeAll();
    }

    
    @Override
    public void setResourceItem(InmhItemData item) {
        this.item = item;
    }
    
    @Override
    public List<Component> getContainerTools() {
        return null;
    }
}
