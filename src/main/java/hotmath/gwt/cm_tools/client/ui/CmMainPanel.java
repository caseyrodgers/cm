package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceContainer;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplFlash;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;

import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.ui.Widget;

public class CmMainPanel extends LayoutContainer {

    public static CmMainPanel __lastInstance;

    public CmMainResourceContainer _mainContent;

    // @TODO: setup event/listener to manager
    // state change
    public CmGuiDefinition cmGuiDef;
    // west panel is static to allow access
    // to the title.
    public ContentPanel _westPanel;
    
    CmResourcePanel _lastResourceViewer;

    /**
     * Main Catchup Math application area.
     * 
     * Including a west component with a title and a main center area with user
     * settable background image.
     * 
     * Each form/context is responsible for calling updateGui(context) when the
     * context is full ready Meaning, any async data has been fetched and
     * parsed.
     * 
     * Each time the context is changed, updateGui(context) must be called to
     * keep the UI in sync.
     * 
     * @param cmModel
     *            The GUI model to use
     */
    public CmMainPanel(final CmGuiDefinition cmGuiDef) {

        __lastInstance = this;

        setScrollMode(Scroll.NONE);
        this.cmGuiDef = cmGuiDef;
        setLayout(new BorderLayout());
        _mainContent = new CmMainResourceContainer();
        
        _westPanel = new ContentPanel();
        _westPanel.setStyleName("main-panel-west");
        _westPanel.setLayout(new BorderLayout());
        
        _westPanel.getHeader().addStyleName("cm-main-panel-header");

        addTools();

        _westPanel.setBorders(false);
        BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 226);
        westData.setSplit(false);
        westData.setCollapsible(false);
        _westPanel.add(cmGuiDef.getWestWidget(), new BorderLayoutData(LayoutRegion.CENTER));
        
        add(_westPanel, westData);
        
        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        //centerData.setMargins(new Margins(1, 0,1, 1));
        centerData.setSplit(false);
        add(_mainContent, centerData);

        layout();
        
        Widget w = cmGuiDef.getCenterWidget();
        if (w != null) {
                _mainContent.add(w);
        }
        
        
        
        /** Any modal window, should hide the resource window to allow
         *  for Flash widgets to be hidden
         *  
         */
        EventBus.getInstance().addEventListener(new CmEventListener() {
            
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventName().equals(EventBus.EVENT_TYPE_MODAL_WINDOW_OPEN)) {
                    // we must remove any resource viewer that contains
                    // flash, otherwise the z-order gets screwed up and
                    // the dialog will bleed through the flash.
                    if(_lastResourceViewer instanceof ResourceViewerImplFlash) {
                        CmMainPanel.__lastInstance._mainContent.removeResource();
                    }
                }
                else if(event.getEventName().equals(EventBus.EVENT_TYPE_RESOURCE_VIEWER_OPEN)) {
                    _lastResourceViewer = (CmResourcePanel)event.getEventData();
                }
                else if(event.getEventName().equals(EventBus.EVENT_TYPE_WINDOW_RESIZED)) {
                    _mainContent.fireWindowResized();
                }
            }
            
            @Override
            public String[] getEventsOfInterest() {
                // TODO Auto-generated method stub
                String types[] = {EventBus.EVENT_TYPE_MODAL_WINDOW_OPEN, EventBus.EVENT_TYPE_RESOURCE_VIEWER_OPEN, EventBus.EVENT_TYPE_WINDOW_RESIZED};
                return types;
            }
        });
    }

    
    /** Request controls from Context
     * 
     */

    private void addTools() {
        List<Component> comps = this.cmGuiDef.getContext().getTools();

        // Add the special prev/next buttons to horizontal panel
        LayoutContainer lc = new LayoutContainer();
        lc.setStyleName("cm-main-panel-button-panel");

        for(Component c:comps) {
            lc.add(c);
        }
        _westPanel.add(lc, new BorderLayoutData(LayoutRegion.NORTH, 50));
    }

    /**
     * Remove any resource
     * 
     */
    public void removeResource() {
        CmMainPanel.__lastInstance._mainContent.removeResource();
    }

}
