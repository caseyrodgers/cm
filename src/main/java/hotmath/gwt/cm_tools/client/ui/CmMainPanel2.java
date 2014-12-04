package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper.ResourceWrapperCallback;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper.WrapperType;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceContentCallback;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceViewerState;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard.WhiteboardResourceCallback;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;

public class CmMainPanel2 extends CmMainPanelShared {

    public CmMainResourceWrapper _mainContentWrapper;

    // @TODO: setup event/listener to manager
    // state change
    public CmGuiDefinition cmGuiDef;
    // west panel is static to allow access
    // to the title.
    public BorderLayoutContainer _westPanel;
    public ContentPanel _westPanelWrapper = new ContentPanel();

    CmResourcePanel _lastResourceViewer;
    CmResourceContentPanel _lastResourceContentPanel;

    /**
     * Main Catchup Math application area.
     * 
     * Including a west component with a title and a main center area with user
     * set-able background image.
     * 
     * Each form/context is responsible for calling updateGui(context) when the
     * context is full ready and any async data has been fetched and
     * parsed.
     * 
     * Each time the context is changed, updateGui(context) must be called to
     * keep the UI in sync.
     * 
     * @param cmGuiDef
     *            The GUI model to use
     *            
     */
    
    
    ResourceWrapperCallback _callback;
    
    public CmMainPanel2(final CmGuiDefinition cmGuiDef) {
        
        forceLayoutOnResize = true;
        
        
        _callback= new ResourceWrapperCallback() {
            @Override
            public ResizeContainer getResizeContainer() {
                // TODO Auto-generated method stub
                return CmMainPanel2.this;
            }
        };
        
        addStyleName("cm-main-panel");
        
        getElement().setAttribute("style","position:relative;");

        _mainContentWrapper = new CmMainResourceWrapper(WrapperType.OPTIMIZED, _callback);
        
        this.cmGuiDef = cmGuiDef;
        
        _westPanel = new BorderLayoutContainer();

        _westPanel.addStyleName("main-panel-west");
        _westPanelWrapper.addStyleName("main-panel-west-wrapper");      
        
        // _westPanel.setAnimCollapse(true);
        _westPanelWrapper.getHeader().addStyleName("cm-main-panel-header");
        _westPanelWrapper.setBorders(true);
        BorderLayoutData westData = new BorderLayoutData(250);
        westData.setSplit(false);
        westData.setCollapsible(true);

        BorderLayoutData westCenterData = new BorderLayoutData();
        westCenterData.setMargins(new Margins(10));
        
        _westPanel.setCenterWidget(cmGuiDef.getWestWidget(), westCenterData);
        
        _westPanelWrapper.setWidget(_westPanel);
        setWestWidget(_westPanelWrapper, westData);

        List<Widget> tools = this.cmGuiDef.getContext().getTools();
        if(tools != null && tools.size() > 0) {
            addTools(tools);   // will add north section to and push west's center down down
        }
        setMainPanelContainer();

        //_mainContent.getElement().setAttribute("style", "background: red");
        if(cmGuiDef.getCenterWidget() != null) {
            _mainContentWrapper.getResourceWrapper().add(cmGuiDef.getCenterWidget());
        }
        
        
        EventBus.getInstance().addEventListener(new CmEventListener() {
            
            @Override
            public void handleEvent(CmEvent event) {
                switch(event.getEventType()) {
                    case EVENT_TYPE_FORCE_GUI_REFRESH:
                        forceLayout();
                        break;
                        
                    default:
                        break;
                }
            }
        });
        
    }
    
    
    public void setMainPanelContainer() {

        BorderLayoutData centerData = new BorderLayoutData();
        centerData.setCollapsible(true);
        centerData.setMargins(new Margins(1, 0,1, 1));
        centerData.setSplit(true);
        
        setCenterWidget(_mainContentWrapper.getResourceWrapper(), centerData);
    }
    

    public void expandResourceButtons() {
        expand(LayoutRegion.WEST);
    }

    /**
     * Request controls from Context
     * 
     * Provide holder for the main buttons for context
     * 
     * Usually either next/prev or the quiz button.
     * 
     * 
     */
    private void addTools(List<Widget> comps) {
        // Add the special prev/next buttons to horizontal panel
        FlowLayoutContainer lc = new FlowLayoutContainer();
        lc.setStyleName("cm-main-panel-button-panel");
        for (Widget c : comps) {
            lc.add(c);
        }
        _westPanel.setNorthWidget(lc, new BorderLayoutData(75));
    }

    /**
     * Remove current resource
     * 
     */
    public void removeResource() {
        _mainContentWrapper.getResourceWrapper().clear();
        _lastResourceViewer = null;
        _lastResourceContentPanel = null;
    }
    
    /** Remove the resource viewer, onloy if it is
     *  a tutor viewer.  This to make sure that only one
     *  tutor is visable at one time due to shared dom ids.
     */
    public void removeResourceIfTutor() {
        if(_lastResourceViewer != null) {
            if(_lastResourceViewer instanceof ResourceViewerImplTutor2) {
                removeResource();
            }
        }
    }
    
    public CmResourcePanel getLastResource() {
        return _lastResourceViewer;
    }

    static private boolean _isWhiteboardVisible;

    static public boolean isWhiteboardVisible() {
        return _isWhiteboardVisible;
    }

    static {
        //publishNative();
        
        /**
         * Implement a static listener for performance reasons
         * 
         */
//        EventBus.getInstance().addEventListener(
//                new CmEventListenerImplDefault() {
//                    @Override
//                    public void handleEvent(CmEvent event) {
//                        switch (event.getEventType()) {
//
//                        case EVENT_TYPE_RESOURCE_VIEWER_OPEN:
//                            __activeInstance._lastResourceViewer = (CmResourcePanel) event.getEventData();
//                            break;
//
//                        case EVENT_TYPE_RESOURCE_VIEWER_CLOSE:
//                            __activeInstance.expandResourceButtons();
//                            __activeInstance._lastResourceViewer = (CmResourcePanel) event.getEventData();
//                            break;
//
//                        case EVENT_TYPE_WINDOW_RESIZED:
//                            __activeInstance._mainContentWrapper.fireWindowResized();
//                            break;
//                            
//                            
//                        case EVENT_TYPE_MAXIMIZE_RESOURCE:
//                            __activeInstance.maximizeResource();
//                            break;
//                            
//                            
//                        case EVENT_TYPE_OPTIMIZE_RESOURCE:
//                            __activeInstance.optimizeResource();
//                            break;
//                            
//
//                        case EVENT_TYPE_WHITEBOARD_READY:
//                            setWhiteboardIsVisible(true);
//                            setQuizQuestionDisplayAsActive(getLastQuestionPid());
//                            break;
//
//                        case EVENT_TYPE_WHITEBOARD_CLOSED:
//                            setWhiteboardIsVisible(false);
//                            setQuizQuestionDisplayAsActive(null);
//                            
//                            
//                            __activeInstance.maximizeResource();
//                            break;
//                            
//                            
//                        case EVENT_TYPE_RESOURCE_CONTAINER_REFRESH:
//                            //__lastInstance.maximizeResource();
//                            break;
//                                
//
//                        case EVENT_TYPE_MODAL_WINDOW_OPEN:
//                            /**
//                             * only hide windows if they might contain a
//                             * whiteboard
//                             */
//                            if (__activeInstance != null
//                                && __activeInstance._mainContentWrapper != null
//                                && __activeInstance._lastResourceViewer != null
//                                && (__activeInstance._lastResourceViewer instanceof CmResourcePanelImplWithWhiteboard
//                                     && ((CmResourcePanelImplWithWhiteboard)__activeInstance._lastResourceViewer).isWhiteboardActive()
//                                     || __activeInstance._lastResourceViewer instanceof ResourceViewerImplTutor)){
//                                    /**
//                                     * If the whiteboard is active
//                                     * hide the current resource to avoid Flash
//                                     * z-order issues
//                                     * 
//                                     */
//                                    __activeInstance._mainContentWrapper.getResourceWrapper().clear();
//                            }
//                            break;
//
//                        case EVENT_TYPE_MODAL_WINDOW_CLOSED:
//                            if (__activeInstance != null
//                                    && __activeInstance._mainContentWrapper != null
//                                    && __activeInstance._lastResourceViewer != null
//                                    && __activeInstance._lastResourceViewer instanceof CmResourcePanelImplWithWhiteboard
//                                    && (__activeInstance._lastResourceViewer instanceof CmResourcePanelImplWithWhiteboard
//                                            && ((CmResourcePanelImplWithWhiteboard)__activeInstance._lastResourceViewer).isWhiteboardActive()
//                                            || __activeInstance._lastResourceViewer instanceof ResourceViewerImplTutor)){
//                                       /** If whiteboard is active, restore any resources
//                                        * 
//                                        */
//                                        __activeInstance.showLastResource();
//                                        
//                                        /** Trigger a refresh to allow any viewer to reset any
//                                         *  data that might be reset if panel is removed/added.
//                                         *  This is mainly for IE.
//                                         */
//                                        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_RESOURCE_CONTAINER_REFRESH));
//                                    }
//                            break;
//                            
//                        case EVENT_TYPE_MODAL_WINDOW_CLEAR:
//                            if (__activeInstance != null) {
//                                __activeInstance._mainContentWrapper = null;
//                                __activeInstance = null;
//                            }
//                            break;
//                            
//                            
//                            
//                        case EVENT_TYPE_TOPIC_CHANGED:
//                            if(__activeInstance != null) {
//                                __activeInstance.removeResource();
//                            }
//                            break;
//                        }
//                    }
//                });
    }


    /** Have to create a new Wrapper each time .. other objects
     *  are reused, include the content panel and the resource viewer.
     */
    public void maximizeResource() {
//        _mainContentWrapper = new CmMainResourceWrapper(WrapperType.MAXIMIZED);
//        _mainContentWrapper.setContentPanel(new CmResourceContentPanel(_lastResourceViewer,_lastResourceContentPanel.getHeader().getText()));
//        setCenterWidget(_mainContentWrapper.getResourceWrapper());
//        
//        forceLayout();
//        
//        CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
    }
    
    protected void optimizeResource() {
//        _mainContentWrapper = new CmMainResourceWrapper(WrapperType.OPTIMIZED);
//        _mainContentWrapper.setContentPanel(new CmResourceContentPanel(_lastResourceViewer,_lastResourceContentPanel.getHeader().getText()));
//        setCenterWidget(_mainContentWrapper.getResourceWrapper());      
//        
//        forceLayout();
//        
//        CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
    }


    WhiteboardResourceCallback tutorCallback = new WhiteboardResourceCallback() {
        public void ensureMaximizeResource() {}
        public void ensureOptimizedResource() {}
        public ResizeContainer getResizeContainer() {
            return CmMainPanel2.this;
        }
    };

    
    /**
     * Display a single resource, remove any previous
     * 
     * Do not track its view
     * 
     * @param resourceItem
     */
    public void showResource(final InmhItemData resourceItem) {
        try {
            ResourceViewerFactory.ResourceViewerFactory_Client client = new ResourceViewerFactory.ResourceViewerFactory_Client() {
                @Override
                public void onUnavailable() {
                    CatchupMathTools.showAlert("Resource not available");
                }

                @Override
                public void onSuccess(ResourceViewerFactory instance) {
                    try {
                        CmResourcePanel viewer = instance.create(resourceItem);
                        if(viewer instanceof ResourceViewerImplTutor2) {
                            ((ResourceViewerImplTutor2)viewer).setCallback(tutorCallback);
                        }
                        showResource(viewer, resourceItem.getTitle(), true);
                    } catch (Exception e) {
                        CatchupMathTools.showAlert("Could not load resource: " + e.getLocalizedMessage());
                    }
                }
            };
            ResourceViewerFactory.createAsync(client);
        } catch (Exception hme) {
            hme.printStackTrace();
            CatchupMathTools.showAlert("Error: " + hme.getMessage());
        }
    }
    
    public void showLastResource() {
        showResource(_lastResourceViewer,_lastResourceContentPanel.getHeader().getText());
    }
    
    public void showResource(CmResourcePanel panel, String title) {
        showResource(panel, title, true);
    }
    public void showResource(CmResourcePanel panel, String title, boolean trackView) {
        
 
        if(_lastResourceViewer != null) {
            _lastResourceViewer.removeResourcePanel();
        }
        
        _lastResourceViewer = panel;
        
//        switch(panel.getInitialMode()) {
//            case MAXIMIZED:
//                _mainContentWrapper = new CmMainResourceWrapper(WrapperType.MAXIMIZED);
//                break;
//                
//            case OPTIMIZED:
//                _mainContentWrapper = new CmMainResourceWrapper(WrapperType.OPTIMIZED);
//                break;
//        }
        _mainContentWrapper = new CmMainResourceWrapper(WrapperType.MAXIMIZED,_callback);        
        
        _lastResourceContentPanel = new CmResourceContentPanel(panel, title, new ResourceContentCallback() {
            @Override
            public void closeResource() {
                removeResource();
            }
            
            @Override
            public boolean isMaximized() {
                return true;
            }
        });
        
        _lastResourceContentPanel.removeExpandButton();

        _mainContentWrapper.setContentPanel(_lastResourceContentPanel);
        
        
        _lastResourceContentPanel.setHeadingText(title);

        BorderLayoutData centerData = new BorderLayoutData();
        centerData.setCollapsible(true);
        centerData.setMargins(new Margins(1, 0,1, 1));
        centerData.setSplit(true);
        
        //setCenterWidget(new TextButton("Test"));
        
        //panel.getResourcePanel().setVisible(false);
        
        setCenterWidget(_mainContentWrapper.getResourceWrapper(), centerData);
        
        if(trackView) {
            EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_RESOURCE_VIEWER_OPEN, panel));
        }

        super.makeSureUiIsRefreshed(panel);
    }

    public void showCenterMessage(HTML ohtml) {
        _mainContentWrapper = new CmMainResourceWrapper(WrapperType.OPTIMIZED, _callback);
        _mainContentWrapper.getResourceWrapper().add(ohtml);
        setCenterWidget(_mainContentWrapper.getResourceWrapper());
        forceLayout();
    }

    /** make sure current display is in maximized mode
     * 
     */
    public void ensureMaximizeResource() {
       if(_lastResourceContentPanel != null && _lastResourceContentPanel.getCurrentViewerState() != ResourceViewerState.MAXIMIZED) {
           CmLogger.debug("Changing to MAXIMIZED mode");
           maximizeResource();           
       }
    }
    
    public void ensureOptimizedResource() {
        if(_lastResourceContentPanel != null && _lastResourceContentPanel.getCurrentViewerState() != ResourceViewerState.OPTIMIZED) {
            CmLogger.debug("Changing to OPTIMIZED mode");
            optimizeResource();           
        }
     }

    /** Return true if the quiz resource is active
     * 
     * @return
     */
    public boolean isResourceQuiz() {
        if(_lastResourceViewer != null) {
            return hasQuizInDom(); 
        }
        return false;
    }

    native private boolean hasQuizInDom() /*-{
        var td = $doc.getElementById("testset_div");
        if(td != null) {
            var cn = td.getElementsByTagName("input");
            if(cn.length > 0) {
                return true;   // has input elements is alive
            }
        }
        
        return false;
    }-*/;



    public void setContextSubTitle(String contextSubTitle) {
        _westPanelWrapper.setHeadingText(contextSubTitle);        
    }
    
}
