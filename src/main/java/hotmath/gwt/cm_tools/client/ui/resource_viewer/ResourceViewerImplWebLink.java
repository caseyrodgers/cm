package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceViewerState;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;


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
    
    @Override
    public boolean needForcedUiRefresh() {
        return false;
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
        if(item.getType() == CmResourceType.WEBLINK_EXTERNAL) {
            showExternalLink();
        }
        else {
            Frame frame = new Frame();
            String url = item.getFile();
            frame.setUrl(url);
            
            addResource(frame,item.getTitle());
        }
        return this;
    }

    private void showExternalLink() {
        Window.open(item.getFile(), "CmWebLink", null);
        CenterLayoutContainer clc = new CenterLayoutContainer();
        clc.setWidget(new HTML("<span style='font-size: 1.5em;font-weight:bold;background: transparent'>'" + item.getTitle() + "' opened in external window</span>"));
        addResource(clc, item.getTitle());
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
        List<Widget> tools = new ArrayList<Widget>();
        TextButton openExternal = new TextButton("Alternative View", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showExternalLink();
                forceLayout();
            }
        });
        openExternal.setToolTip("Open web link in a external web window");
        tools.add(openExternal);
        return tools;
    }
    
    @Override
    public boolean shouldContainerBeTransparent() {
        return false;
    }
}
