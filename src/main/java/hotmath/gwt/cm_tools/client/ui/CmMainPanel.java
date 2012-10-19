package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper.WrapperType;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.info.Info;

public class CmMainPanel extends BorderLayoutContainer {

	public static CmMainPanel __lastInstance;

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
	 */
	public CmMainPanel(final CmGuiDefinition cmGuiDef) {

		__lastInstance = this;
		
		getElement().setAttribute("style","position:relative;");
		
		
		_mainContentWrapper = new    CmMainResourceWrapper(WrapperType.OPTIMIZED);
		
		this.cmGuiDef = cmGuiDef;
		
		_westPanel = new BorderLayoutContainer();

		_westPanel.addStyleName("main-panel-west");
		
		
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

		addTools();

		setMainPanelContainer();

        //_mainContent.getElement().setAttribute("style", "background: red");
		if(cmGuiDef.getCenterWidget() != null) {
		    _mainContentWrapper.getResourceWrapper().add(cmGuiDef.getCenterWidget());
		}
	}
	
	@Override
	public void forceLayout() {
	    // TODO Auto-generated method stub
	    super.forceLayout();
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
	private void addTools() {
		List<Widget> comps = this.cmGuiDef.getContext().getTools();

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
	    __lastInstance._mainContentWrapper.getResourceWrapper().clear();
	    _lastResourceViewer = null;
	    _lastResourceContentPanel = null;
	}

	static private boolean _isWhiteboardVisible;

	static public boolean isWhiteboardVisible() {
		return _isWhiteboardVisible;
	}

	static {
		publishNative();

		/**
		 * Implement a static listener for performance reasons
		 * 
		 */
		EventBus.getInstance().addEventListener(
				new CmEventListenerImplDefault() {
					@Override
					public void handleEvent(CmEvent event) {
						switch (event.getEventType()) {

						case EVENT_TYPE_RESOURCE_VIEWER_OPEN:
							__lastInstance._lastResourceViewer = (CmResourcePanel) event
									.getEventData();
							break;

						case EVENT_TYPE_RESOURCE_VIEWER_CLOSE:
							__lastInstance.expandResourceButtons();
							__lastInstance._lastResourceViewer = (CmResourcePanel) event.getEventData();
							break;

						case EVENT_TYPE_WINDOW_RESIZED:
							__lastInstance._mainContentWrapper.fireWindowResized();
							break;
							
							
						case EVENT_TYPE_MAXIMIZE_RESOURCE:
						    __lastInstance.maximizeResource();
						    break;
						    
						    
                        case EVENT_TYPE_OPTIMIZE_RESOURCE:
                            __lastInstance.optimizeResource();
                            break;
						    

						case EVENT_TYPE_WHITEBOARD_READY:
							setWhiteboardIsVisible(true);
							setQuizQuestionDisplayAsActive(getLastQuestionPid());
							break;

						case EVENT_TYPE_WHITEBOARD_CLOSED:
							setWhiteboardIsVisible(false);
							setQuizQuestionDisplayAsActive(null);
							
							
							__lastInstance.maximizeResource();
							break;
							
							
						case EVENT_TYPE_RESOURCE_CONTAINER_REFRESH:
						    //__lastInstance.maximizeResource();
						    break;
							    

						case EVENT_TYPE_MODAL_WINDOW_OPEN:
							/**
							 * only hide windows if they might contain a
							 * whiteboard
							 */
							if (__lastInstance != null
								&& __lastInstance._mainContentWrapper != null
								&& __lastInstance._lastResourceViewer != null
								&& (__lastInstance._lastResourceViewer instanceof CmResourcePanelImplWithWhiteboard
							         && ((CmResourcePanelImplWithWhiteboard)__lastInstance._lastResourceViewer).isWhiteboardActive()
							         || __lastInstance._lastResourceViewer instanceof ResourceViewerImplTutor)){
									/**
									 * If the whiteboard is active
									 * hide the current resource to avoid Flash
									 * z-order issues
									 * 
									 */
									__lastInstance._mainContentWrapper.getResourceWrapper().clear();
							}
							break;

						case EVENT_TYPE_MODAL_WINDOW_CLOSED:
							if (__lastInstance != null
									&& __lastInstance._mainContentWrapper != null
									&& __lastInstance._lastResourceViewer != null
									&& __lastInstance._lastResourceViewer instanceof CmResourcePanelImplWithWhiteboard
	                                && (__lastInstance._lastResourceViewer instanceof CmResourcePanelImplWithWhiteboard
	                                        && ((CmResourcePanelImplWithWhiteboard)__lastInstance._lastResourceViewer).isWhiteboardActive()
	                                        || __lastInstance._lastResourceViewer instanceof ResourceViewerImplTutor)){
								       /** If whiteboard is active, restore any resources
								        * 
								        */
										__lastInstance.showLastResource();
										
										/** Trigger a refresh to allow any viewer to reset any
										 *  data that might be reset if panel is removed/added.
										 *  This is mainly for IE.
										 */
										EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_RESOURCE_CONTAINER_REFRESH));
									}
							break;
							
                        case EVENT_TYPE_MODAL_WINDOW_CLEAR:
                            if (__lastInstance != null) {
                                __lastInstance._mainContentWrapper = null;
                                __lastInstance = null;
                            }
                            
                            
                            
                        case EVENT_TYPE_TOPIC_CHANGED:
                            __lastInstance.removeResource();
                            break;
						}
					}
				});
	}

	/**
	 * This block of code is used for global testset communication between
	 * external testset HTML/JS and GWT.
	 * 
	 * @TODO: create a separate abstraction that exposes a global listener.
	 * 
	 */
	/**
	 * define global method to allow for setting the active quiz pid
	 * 
	 */
	static private native void publishNative() /*-{
												$wnd.setQuizActiveQuestion_Gwt = @hotmath.gwt.cm_tools.client.ui.CmMainPanel::setQuizQuestionActive_Gwt(Ljava/lang/String;);
												}-*/;

	/** Have to create a new Wrapper each time .. other objects
	 *  are reused, include the content panel and the resource viewer.
	 */
	public void maximizeResource() {
	    _mainContentWrapper = new CmMainResourceWrapper(WrapperType.MAXIMIZED);
	    _mainContentWrapper.setContentPanel(new CmResourceContentPanel(_lastResourceViewer,_lastResourceContentPanel.getHeader().getText()));
	    setCenterWidget(_mainContentWrapper.getResourceWrapper()); // _mainContentWrapper.getResourceWrapper());
	    
	    forceLayout();
    }
	
	protected void optimizeResource() {
	    _mainContentWrapper = new CmMainResourceWrapper(WrapperType.OPTIMIZED);
	    _mainContentWrapper.setContentPanel(new CmResourceContentPanel(_lastResourceViewer,_lastResourceContentPanel.getHeader().getText()));
        setCenterWidget(_mainContentWrapper.getResourceWrapper()); // _mainContentWrapper.getResourceWrapper());	    
        
        forceLayout();
	}

    static private String __lastQuestionPid;

	/**
	 * called by external JS each time a testset question has been made current.
	 * 
	 * @param pid
	 */
	@SuppressWarnings("unused")
	static private void setQuizQuestionActive_Gwt(String pid) {
		if (__lastQuestionPid == null || !__lastQuestionPid.equals(pid)) {
			__lastQuestionPid = pid;
			EventBus.getInstance().fireEvent(
					new CmEvent(
							EventType.EVENT_TYPE_QUIZ_QUESTION_FOCUS_CHANGED,
							pid));
		}
	}

	/**
	 * return the last active question's pid. Return null if no question has
	 * been displayed.
	 * 
	 * NOTE: quiz question/whiteboard is disabled .. so only the one whiteboard
	 * per question.
	 * 
	 * @return
	 */
	static public String getLastQuestionPid() {
		return "quiz"; // __lastQuestionPid;
	}

	/**
	 * Call external method to set a given question as active. The guid is
	 * matched with passed pid. If null, then the first question is marked as
	 * current.
	 * 
	 * @param pid
	 */
	static public native void setQuizQuestionDisplayAsActive(String pid) /*-{
																			$wnd.setQuizQuestionDisplayAsActive(pid);
																			}-*/;

	static public native void setWhiteboardIsVisible(boolean whiteboardVisible) /*-{
																				   $wnd.setWhiteboardIsVisible(whiteboardVisible);
																				   }-*/;

	
	
    /**
     * Display a single resource, remove any previous
     * 
     * Do not track its view
     * 
     * @param resourceItem
     */
    public void showResource(final InmhItemData resourceItem) {
        Info.display("Info", "Showing resource: " + resourceItem.getFile());
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
                        showResource(viewer, resourceItem.getTitle());
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
        
        
        if(_lastResourceViewer != null) {
            _lastResourceViewer.removeResourcePanel();
        }
        
        _lastResourceViewer = panel;
        Info.display("Info", "Showing resource: " + title);
        
        
        switch(panel.getInitialMode()) {
            case MAXIMIZED:
                _mainContentWrapper = new CmMainResourceWrapper(WrapperType.MAXIMIZED);
                break;
                
            case OPTIMIZED:
                _mainContentWrapper = new CmMainResourceWrapper(WrapperType.OPTIMIZED);
                break;
        }
        
        _lastResourceContentPanel = new CmResourceContentPanel(panel, title);
        _mainContentWrapper.setContentPanel(_lastResourceContentPanel);
        
        
        _lastResourceContentPanel.setHeadingText(title);

        BorderLayoutData centerData = new BorderLayoutData();
        centerData.setCollapsible(true);
        centerData.setMargins(new Margins(1, 0,1, 1));
        centerData.setSplit(true);
        
        //setCenterWidget(new TextButton("Test"));
        setCenterWidget(_mainContentWrapper.getResourceWrapper(), centerData);
        
        forceLayout();
    }

    public void showCenterMessage(HTML ohtml) {
        _mainContentWrapper = new CmMainResourceWrapper(WrapperType.OPTIMIZED);
        _mainContentWrapper.getResourceWrapper().add(ohtml);
        setCenterWidget(_mainContentWrapper.getResourceWrapper());
        forceLayout();
    }
}
