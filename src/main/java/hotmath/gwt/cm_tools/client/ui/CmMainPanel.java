package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceContainer;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.List;

import com.extjs.gxt.ui.client.Style.Direction;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.Timer;
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
		setStyleAttribute("position", "relative");
		setScrollMode(Scroll.NONE);
		this.cmGuiDef = cmGuiDef;
		BorderLayout bl = new BorderLayout() {
			public void layout() {
				super.layout();
				if (_westPanel.getHeader().getToolCount() > 0) {
					_westPanel.getHeader().getTool(0).setToolTip("Hide");
				}
			}
		};
		setLayout(bl);
		_mainContent = new CmMainResourceContainer();

		_westPanel = new ContentPanel();

		_westPanel.addStyleName("main-panel-west");
		_westPanel.setLayout(new BorderLayout());
		// _westPanel.setAnimCollapse(true);
		_westPanel.getHeader().addStyleName("cm-main-panel-header");
		_westPanel.setBorders(false);

		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 226);
		westData.setSplit(false);
		westData.setCollapsible(true);

		_westPanel.add(cmGuiDef.getWestWidget(), new BorderLayoutData(
				LayoutRegion.CENTER));

		add(_westPanel, westData);

		addTools();

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		// centerData.setMargins(new Margins(1, 0,1, 1));
		centerData.setSplit(false);
		add(_mainContent, centerData);

		Widget w = cmGuiDef.getCenterWidget();
		if (w != null) {
			_mainContent.add(w);
		}

	}

	public void expandResourceButtons() {
		((BorderLayout) getLayout()).expand(LayoutRegion.WEST);
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
		List<Component> comps = this.cmGuiDef.getContext().getTools();

		// Add the special prev/next buttons to horizontal panel
		LayoutContainer lc = new LayoutContainer();
		lc.setStyleName("cm-main-panel-button-panel");

		for (Component c : comps) {
			lc.add(c);
		}
		_westPanel.add(lc, new BorderLayoutData(LayoutRegion.NORTH, 50));
	}

	/**
	 * Remove any resource
	 * 
	 */
	public void removeResource() {
		__lastInstance._mainContent.removeResource();
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
							__lastInstance._lastResourceViewer = (CmResourcePanel) event
									.getEventData();
							break;

						case EVENT_TYPE_WINDOW_RESIZED:
							__lastInstance._mainContent.fireWindowResized();
							break;

						case EVENT_TYPE_CONTEXT_TOOLTIP_SHOW:
							ContextTooltipPanel.getInstance()
									.setContextTooltip(
											(String) event.getEventData());
							break;

						case EVENT_TYPE_WHITEBOARD_READY:
							setWhiteboardIsVisible(true);
							setQuizQuestionDisplayAsActive(getLastQuestionPid());
							break;

						case EVENT_TYPE_WHITEBOARD_CLOSED:
							setWhiteboardIsVisible(false);
							setQuizQuestionDisplayAsActive(null);
							break;

						case EVENT_TYPE_MODAL_WINDOW_OPEN:
							/**
							 * only hide windows if they might contain a
							 * whiteboard
							 */
							if (__lastInstance != null
								&& __lastInstance._mainContent != null
								&& __lastInstance._lastResourceViewer != null
								&& __lastInstance._lastResourceViewer instanceof CmResourcePanelImplWithWhiteboard
								&& ((CmResourcePanelImplWithWhiteboard)__lastInstance._lastResourceViewer).isWhiteboardActive()) {
									/**
									 * If the whiteboard is active
									 * hide the current resource to avoid Flash
									 * z-order issues
									 * 
									 */
									__lastInstance._mainContent.removeAll();
							}
							break;

						case EVENT_TYPE_MODAL_WINDOW_CLOSED:
							if (__lastInstance != null
									&& __lastInstance._mainContent != null
									&& __lastInstance._lastResourceViewer != null
									&& __lastInstance._lastResourceViewer instanceof CmResourcePanelImplWithWhiteboard
									&& ((CmResourcePanelImplWithWhiteboard)__lastInstance._lastResourceViewer).isWhiteboardActive()) {
								       /** If whiteboard is active, restore any resources
								        * 
								        */
										__lastInstance._mainContent.showResource();
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
}

/**
 * Display a dropdown tooltip below the main context buttons
 * 
 * @author casey
 * 
 */
class ContextTooltipPanel extends LayoutContainer {

	static private ContextTooltipPanel __instance;

	static public ContextTooltipPanel getInstance() {
		if (__instance == null)
			__instance = new ContextTooltipPanel();
		return __instance;
	}

	static final int TIP_SHOW_MILLS = 2000;
	Timer tipTimer;

	private ContextTooltipPanel() {
		addStyleName("context-tooltip-panel");
		setStyleAttribute("position", "absolute");
		setStyleAttribute("top", "50px");
		setStyleAttribute("left", "0");
		setStyleAttribute("width", "100%");
	}

	/**
	 * Display tooltip in drop down area
	 * 
	 * If tip is already being displayed and is different then we want to cancel
	 * curent display and show new one.
	 * 
	 * @param tooltip
	 */
	public void setContextTooltip(String tooltip) {

		if (tipTimer != null) {
			tipTimer.cancel();
			tipTimer = null;
		}

		String html = "<p style='padding: 10px;background: yellow;'>" + tooltip
				+ "</p>";

		removeAll();
		setStyleAttribute("top", "50px");
		setStyleAttribute("left", "0");
		add(new Html(html));

		CmMainPanel.__lastInstance._westPanel.add(this);
		CmMainPanel.__lastInstance._westPanel.layout();
		el().slideIn(Direction.DOWN, new FxConfig(500));

		tipTimer = new Timer() {
			@Override
			public void run() {
				/**
				 * remove this widget from parent
				 * 
				 * @TODO: remove variable access, use event?
				 */
				// CmMainPanel.__lastInstance._westPanel.remove(TooltipContentPanel.this);
				// CmMainPanel.__lastInstance.layout();
				try {
					el().fadeOut(FxConfig.NONE);
					CmMainPanel.__lastInstance._westPanel
							.remove(ContextTooltipPanel.this);
					CmMainPanel.__lastInstance.layout();
				} finally {
					tipTimer = null;
				}
			}
		};
		tipTimer.schedule(TIP_SHOW_MILLS);
	}

}