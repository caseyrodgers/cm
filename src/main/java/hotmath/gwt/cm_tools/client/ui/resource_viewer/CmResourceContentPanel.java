package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper.WrapperType;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplActivity;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Class to define a standard container for all resources.
 * 
 * Will provide basic services, such as removing/minimizing,etc.
 * 
 * Restore current setting when new resource is added, unless resource does not
 * allow said state. In this case, set to OPTIMAL.
 * 
 */
public class CmResourceContentPanel extends ContentPanel {
    
    ResourceViewerState viewerState = null;
    TextButton _maximize;

    static String EXPAND_TEXT = "Expand";
    static String SHRINK_TEXT = "Shrink";

    /**
     * Keep a static representation of the current display state
     * 
     */
    static ResourceViewerState __currentDisplayState = ResourceViewerState.OPTIMIZED;

    /**
     * Create a new Resource Container with named container as the outer
     * container and panel as the child.
     * 
     * Provides a wrapper for the resource to expand/min and  provides place
     * for custom viewer specific tools.
     * 
     * 
     * 
     * Panel needs to support OPTIMIZED and MAXIMIZED states.  As determined
     * by the panel.
     * 
     * 
     * 
     * 
     * @param container
     * @param resourceViewer
     */
    public interface ResourceContentCallback {
        void closeResource();

        boolean isMaximized();
    }
    public CmResourceContentPanel(final CmResourcePanel resourceViewer, String title, final ResourceContentCallback callback) {
        this._panel = resourceViewer;
        ResourceContentCallback this_callback = callback;
        setHeadingHtml(title);
        addStyleName("cm-resource-viewer-container");
        if(resourceViewer.getContainerStyleName() != null) {
            addStyleName(resourceViewer.getContainerStyleName());
        }

        /**
         * add any resource specific tools to resource container header
         * 
         */
        List<Widget> tools = resourceViewer.getContainerTools();
        if (tools != null) {
            for (int i = 0, t = tools.size(); i < t; i++) {
                getHeader().addTool(tools.get(i));
            }
        }
        /**
        // how to know if system is currently expanded
        // global runtime options?
        // (CmMainPanel.__activeInstance._mainContentWrapper.getWrapperMode() == WrapperType.MAXIMIZED);
         * 
         */
        boolean isCurrentlyMaximized = callback.isMaximized();
        
        __currentDisplayState = isCurrentlyMaximized?ResourceViewerState.MAXIMIZED:ResourceViewerState.OPTIMIZED;
        viewerState = __currentDisplayState;

        /**
         * Maximize is an optional feature
         * 
         */
        if (resourceViewer.allowMaximize()) {
            String text = isCurrentlyMaximized?SHRINK_TEXT:EXPAND_TEXT;
            _maximize = new TextButton(text, new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    closeResource(event, resourceViewer);                
            }});
            
           getHeader().addTool(_maximize);
        }

        /**
         * Close is optional
         * 
         */
        if (resourceViewer.allowClose()) {
            getHeader().addTool(new TextButton("Close", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    resourceViewer.removeResourcePanel();
                    
                    callback.closeResource();
                    
                }
            }));
        }
        
        if (!resourceViewer.showContainer()) {
            getElement().setAttribute("style","background:transparent");
            setBorders(false);
            setBodyBorder(false);
            forceLayout();
        }
        /**
         * If user has already maximized resource viewer, try to open all
         * resources maximized
         * 
         */
        if (viewerState == ResourceViewerState.OPTIMIZED) {
            setupDisplayForOptimize();
        } else {
            setupDisplayForMaximized();
        }
        
        setPanelWidget(resourceViewer, false, false);

    }
    
    
    public void removeExpandButton() {
        if(_maximize != null) {
            _maximize.removeFromParent();
        }
    }
    
    public CmResourcePanel getResourcePanel () {
        return _panel;
    }

    public TextButton getMaximizeButton() {
        return _maximize;
    }

    /**
     * Configure container in OPTIMIZED configuration
     * 
     * @param panel
     */
    public void setOptimized(final CmResourcePanel panel) {
        if (viewerState == ResourceViewerState.OPTIMIZED)
            return;
        
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_OPTIMIZE_RESOURCE,panel));        
    }

    public boolean isMaximized() {
        return __currentDisplayState == ResourceViewerState.MAXIMIZED;
    }

    public void setMaximize(CmResourcePanel panel) {
        setMaximize(panel, true);
    }

    public void setMaximize(CmResourcePanel panel, boolean trackChange) {

        if (viewerState == ResourceViewerState.MAXIMIZED)
            return;
        
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MAXIMIZE_RESOURCE,panel));
    }

    private void closeResource(SelectEvent ce, CmResourcePanel panel) {
        boolean isMax = ((TextButton)ce.getSource()).getText().equals(EXPAND_TEXT);
        if (isMax) {
            setMaximize(panel);
        } else {
            setOptimized(panel);
        }
    }

    CmResourcePanel _panel;
    /**
     * Clean and add the resource panel, forcing a new layout
     * 
     * @param panel
     */
    public void setPanelWidget(CmResourcePanel panel, boolean trackChange, boolean fireCmEvent) {
        _panel = panel;
        clear();


        Widget lc = panel.getResourcePanel();


        /** if tutor, then make background transparent
         *  TODO: move from here..
         */
        if(panel.shouldContainerBeTransparent()) {
            getBody().applyStyles("background: transparent;border: none;");
        }


        setWidget(lc);



        if (trackChange) {
            /**
             * If resource is 'forced' to open maximum, do not allow it affect
             * the viewer for other resources.
             */
            if (panel.getInitialMode() == ResourceViewerState.MAXIMIZED && viewerState == ResourceViewerState.MAXIMIZED) {
                /** don't track change */
            }
            /**
             * IF resource is 'forced' to open optimized, do not allow it affect
             * ..
             * 
             */
            else if (panel.getInitialMode() == ResourceViewerState.OPTIMIZED && panel.allowMaximize() == false) {
                /** don't track change */
            } else if (panel.getInitialMode() == ResourceViewerState.OPTIMIZED && panel.allowMaximize() == false) {

            } else {
                __currentDisplayState = viewerState;
            }
        }

        if (fireCmEvent)
            EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_RESOURCE_CONTAINER_REFRESH));
    }

    public ResourceViewerState getCurrentViewerState() {
        return __currentDisplayState;
    }
    
    
    public ResourceViewerState getViewerState() {
        return viewerState;
    }

    public void setViewerState(ResourceViewerState viewerState) {
        this.viewerState = viewerState;
    }

    public enum ResourceViewerState {
        OPTIMIZED, MAXIMIZED
    }

    public void setupDisplayForMaximized() {
        
        
//      // maximize the resource area
//      //
//      // CmResourcePanelContainer.this.container.setLayout(new FitLayout());
//      
//      /**
//       * If container needs to be fully reset
//       * 
//       */
//      if (panel instanceof ResourceViewerImplActivity) {
//          clear();
//          add(panel.getResourcePanel());
//      }


      if (_maximize != null) { 
          _maximize.setText(SHRINK_TEXT); 
      }


      /** If a whiteboard, then do not allow Shrink
       * 
       */
      if(_panel instanceof CmResourcePanelImplWithWhiteboard) {
          if( ((CmResourcePanelImplWithWhiteboard)_panel).isWhiteboardActive() ) {
              /** Cannot shrink Whiteboard */
              //_maximize.setEnabled(false);
          }
      }
      
      /**
       * Reset the panel widget, if needed
       * 
       */
      //container.setDisplayMaximized();
      
      viewerState = ResourceViewerState.MAXIMIZED;
    }
    
    
    public void setupDisplayForOptimize() {

        setWidth(_panel.getOptimalWidth());
        setHeight(_panel.getOptimalHeight());
        
        //int height = CmMainResourceContainer.getCalculatedHeight(container, _panel);
        //setHeight(height);

        /**
         * If container needs to be fully reset
         * 
         */
        if (_panel instanceof ResourceViewerImplActivity) {
            clear();
            add(_panel.getResourcePanel());
            //container.forceLayout();
        }

        viewerState = ResourceViewerState.OPTIMIZED;
    }
}
