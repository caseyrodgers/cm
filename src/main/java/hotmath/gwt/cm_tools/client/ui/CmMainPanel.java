package hotmath.gwt.cm_tools.client.ui;



import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.assignment.event.StudentAssignmentViewerActivatedAction;
import hotmath.gwt.cm_tools.client.ui.assignment.event.StudentAssignmentViewerActivatedHandler;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper.ResourceWrapperCallback;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper.WrapperType;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceContentCallback;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceViewerState;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.search.SearchButton;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard.WhiteboardResourceCallback;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class CmMainPanel extends CmMainPanelShared {

	private static CmMainPanel __activeInstance;

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
	
    
    ResourceWrapperCallback _resourceCallback = new ResourceWrapperCallback() {
        @Override
        public ResizeContainer getResizeContainer() {
            return __activeInstance;
        }
    };
    
	public CmMainPanel(final CmGuiDefinition cmGuiDef) {

		__activeInstance = this;
		
		addStyleName("cm-main-panel");
		
		getElement().setAttribute("style","position:relative;");
		
		_mainContentWrapper = new CmMainResourceWrapper(WrapperType.OPTIMIZED,_resourceCallback);
		
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
	    __activeInstance._mainContentWrapper.getResourceWrapper().clear();
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
		publishNative();
		
		/** Configure normal CM system when Assignments are activated
		 * 
		 */
		CmRpcCore.EVENT_BUS.addHandler(StudentAssignmentViewerActivatedAction.TYPE, new StudentAssignmentViewerActivatedHandler() {
            @Override
            public void assignmentGuiActivated() {
                Log.debug("Student Assignments are activated");
                
                /** Remove any resource due to tutor using
                 *  hardcoded ids. 
                 */
//                if(__lastInstance != null) {
//                    if(ContextController.getInstance().getTheContext() instanceof QuizContext) {
//                        // skip it, leave it up
//                    }
//                    else {
//                        __lastInstance.removeResource();
//                    }
//                }
            }
        });

		/**
		 * Implement a static listener for performance reasons
		 * 
		 */
		EventBus.getInstance().addEventListener(
				new CmEventListenerImplDefault() {
					@Override
					public void handleEvent(CmEvent event) {
						switch (event.getEventType()) {
                        
                        case EVENT_TYPE_FORCE_GUI_REFRESH:
                            if(__activeInstance != null) {
                                __activeInstance.setWidget(__activeInstance.getWidget());  // really force UI update
                                __activeInstance._mainContentWrapper.fireWindowResized();
                            }
                            break;

						case EVENT_TYPE_RESOURCE_VIEWER_OPEN:
						    if(__activeInstance != null) {
						        __activeInstance._lastResourceViewer = (CmResourcePanel) event.getEventData();
						    }
							break;

						case EVENT_TYPE_RESOURCE_VIEWER_CLOSE:
	                          if(__activeInstance != null) {
    							__activeInstance.expandResourceButtons();
    							__activeInstance._lastResourceViewer = (CmResourcePanel) event.getEventData();
	                          }
							break;

						case EVENT_TYPE_WINDOW_RESIZED:
	                          if(__activeInstance != null) {
							    __activeInstance._mainContentWrapper.fireWindowResized();
	                          }
							break;
							
							
						case EVENT_TYPE_MAXIMIZE_RESOURCE:
						    if(__activeInstance != null) {
						        __activeInstance.maximizeResource();
						    }
						    break;
						    
						    
                        case EVENT_TYPE_OPTIMIZE_RESOURCE:
                            if(__activeInstance != null) {
                                __activeInstance.optimizeResource();
                            }
                            break;
						    

						case EVENT_TYPE_WHITEBOARD_READY:
	                        if(__activeInstance != null) {
    							setWhiteboardIsVisible(true);
    							setQuizQuestionDisplayAsActive(getLastQuestionPid());
	                        }
							break;

						case EVENT_TYPE_WHITEBOARD_CLOSED:
                            if(__activeInstance != null) {
							    setWhiteboardIsVisible(false);
							    setQuizQuestionDisplayAsActive(null);
							
							    __activeInstance.maximizeResource();
                            }							    
							break;
							
							
						case EVENT_TYPE_RESOURCE_CONTAINER_REFRESH:
						    //__lastInstance.maximizeResource();
						    break;
							    

						case EVENT_TYPE_MODAL_WINDOW_OPEN:
							/**
							 * only hide windows if they might contain a
							 * whiteboard
							 */
							if (__activeInstance != null
								&& __activeInstance._mainContentWrapper != null
								&& __activeInstance._lastResourceViewer != null
								&& (__activeInstance._lastResourceViewer instanceof CmResourcePanelImplWithWhiteboard
							         && ((CmResourcePanelImplWithWhiteboard)__activeInstance._lastResourceViewer).isWhiteboardActive()
							         || __activeInstance._lastResourceViewer instanceof ResourceViewerImplTutor2)){
									/**
									 * If the whiteboard is active
									 * hide the current resource to avoid Flash
									 * z-order issues
									 * 
									 */
									__activeInstance._mainContentWrapper.getResourceWrapper().clear();
							}
							break;

						case EVENT_TYPE_MODAL_WINDOW_CLOSED:
							if (__activeInstance != null
									&& __activeInstance._mainContentWrapper != null
									&& __activeInstance._lastResourceViewer != null
									&& __activeInstance._lastResourceViewer instanceof CmResourcePanelImplWithWhiteboard
	                                && (__activeInstance._lastResourceViewer instanceof CmResourcePanelImplWithWhiteboard
	                                        && ((CmResourcePanelImplWithWhiteboard)__activeInstance._lastResourceViewer).isWhiteboardActive()
	                                        || __activeInstance._lastResourceViewer instanceof ResourceViewerImplTutor2)){
								       /** If whiteboard is active, restore any resources
								        * 
								        */
										__activeInstance.showLastResource();
										
										/** Trigger a refresh to allow any viewer to reset any
										 *  data that might be reset if panel is removed/added.
										 *  This is mainly for IE.
										 */
										EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_RESOURCE_CONTAINER_REFRESH));
									}
							break;
							
                        case EVENT_TYPE_MODAL_WINDOW_CLEAR:
                            if (__activeInstance != null) {
                                __activeInstance._mainContentWrapper = null;
                                __activeInstance = null;
                            }
                            break;
                            
                            
                            
                        case EVENT_TYPE_TOPIC_CHANGED:
                            if(__activeInstance != null) {
                                __activeInstance.removeResource();
                            }
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

	
	ResourceContentCallback callback = new ResourceContentCallback() {
        @Override
        public void closeResource() {
            EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_RESOURCE_VIEWER_CLOSE, _lastResourceViewer));            
        }
        
        @Override
        public boolean isMaximized() {
            boolean isCurrentlyMaximized = (__activeInstance._mainContentWrapper.getWrapperMode() == WrapperType.MAXIMIZED);
            return isCurrentlyMaximized;
        }
    };
	
	
	/** Have to create a new Wrapper each time .. other objects
	 *  are reused, include the content panel and the resource viewer.
	 */
	public void maximizeResource() {
	    _mainContentWrapper = new CmMainResourceWrapper(WrapperType.MAXIMIZED, _resourceCallback);
	    _mainContentWrapper.setContentPanel(new CmResourceContentPanel(_lastResourceViewer,_lastResourceContentPanel.getHeader().getText(), callback));
	    setCenterWidget(_mainContentWrapper.getResourceWrapper());
	    
	    forceLayout();
	    
	    
	    CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
    }
	
	protected void optimizeResource() {
	    _mainContentWrapper = new CmMainResourceWrapper(WrapperType.OPTIMIZED, _resourceCallback);
	    _mainContentWrapper.setContentPanel(new CmResourceContentPanel(_lastResourceViewer,_lastResourceContentPanel.getHeader().getText(), callback));
        setCenterWidget(_mainContentWrapper.getResourceWrapper());	    
        
        forceLayout();
        
        CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
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


    WhiteboardResourceCallback whiteboardCallback = new WhiteboardResourceCallback() {

        @Override
        public void ensureOptimizedResource() {
            CmMainPanel.this.ensureOptimizedResource();
        }

        @Override
        public void ensureMaximizeResource() {
            CmMainPanel.this.ensureMaximizeResource();
        }

        @Override
        public ResizeContainer getResizeContainer() {
            return CmMainPanel.__activeInstance;
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
                            ((ResourceViewerImplTutor2)viewer).setWhiteboardCallback(whiteboardCallback);
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
    
    public void showResource(final CmResourcePanel panel, String title, boolean trackView) {
        if(_lastResourceViewer != null) {
            _lastResourceViewer.removeResourcePanel();
        }
        
        _lastResourceViewer = panel;
        
        switch(panel.getInitialMode()) {
            case MAXIMIZED:
                _mainContentWrapper = new CmMainResourceWrapper(WrapperType.MAXIMIZED, _resourceCallback);
                break;
                
            case OPTIMIZED:
                _mainContentWrapper = new CmMainResourceWrapper(WrapperType.OPTIMIZED, _resourceCallback);
                break;
        }
        
        _lastResourceContentPanel = new CmResourceContentPanel(panel, title, callback);
        _mainContentWrapper.setContentPanel(_lastResourceContentPanel);
        
        _lastResourceContentPanel.setHeadingText(title);
        
        
        //_mainContentWrapper.getResourceWrapper().clear();
        //_mainContentWrapper.getResourceWrapper().add(panel.getResourcePanel());

        BorderLayoutData centerData = new BorderLayoutData();
        centerData.setCollapsible(true);
        centerData.setMargins(new Margins(1, 0,1, 1));
        centerData.setSplit(true);
        
        //setCenterWidget(new TextButton("Test"));
        ResizeContainer v = _mainContentWrapper.getResourceWrapper();
        setCenterWidget(v, centerData);
        
        if(trackView) {
            EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_RESOURCE_VIEWER_OPEN, panel));
        }
        super.makeSureUiIsRefreshed(panel);
    }

    public void showCenterMessage(HTML ohtml, Hyperlink howToLink) {
        _mainContentWrapper = new CmMainResourceWrapper(WrapperType.OPTIMIZED, _resourceCallback);
        VerticalPanel vp = new VerticalPanel();
        vp.addStyleName("prescription-help-panel");
        
        
        FlowLayoutContainer fp = new FlowLayoutContainer();
        fp.addStyleName("info");
        
        
        fp.add(ohtml);
        fp.add(howToLink);
        
        SearchButton searchButton = new SearchButton();
        fp.add(new HTML("<div class='search-btn-label'>Search Catchup Math Lessons:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>"));
        fp.add(searchButton);
        
        vp.add(fp);
        
        _mainContentWrapper.getResourceWrapper().add(vp);
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



    public static CmMainPanel getActiveInstance() {
        return __activeInstance;
    }
    
}
