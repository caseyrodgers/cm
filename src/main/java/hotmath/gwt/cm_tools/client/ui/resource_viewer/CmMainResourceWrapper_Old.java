package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceViewerState;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplActivity;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplVideo;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * Main resource panel that contains the actual resource viewers for each
 * resource type. It provides the ability to maximize/optimize the content area.
 * 
 * The container is retrieved via the getResourceWrapper method.
 * 
 * The wrapper is either maximzied or optmized. Each requires a different
 * concrete object and is handled via composition.
 * 
 * 
 * @author casey
 * 
 */
public class CmMainResourceWrapper_Old {

    public static enum WrapperType {
        OPTIMIZED, MAXIMIZED
    };

    CmResourcePanel resourcePanel;
    String currentTitle;

    SimpleContainer _panelMax = new SimpleContainer();
    MyCenterLayoutContainer _panelOptimze = new MyCenterLayoutContainer();

    ResizeContainer _wrapper;

    WrapperType wrapperType;

    public CmMainResourceWrapper_Old() {
        this(WrapperType.OPTIMIZED); // default
    }

    private CmMainResourceWrapper_Old(WrapperType wrapperType) {

        this.wrapperType = wrapperType;

        switch (wrapperType) {
        case OPTIMIZED:
            _wrapper = _panelOptimze;
            break;

        case MAXIMIZED:
            _wrapper = _panelMax;
            break;
        }

        _wrapper.addStyleName("resource-container");
        _wrapper.addStyleName("main-resource-panel");

        if (UserInfo.getInstance() != null && UserInfo.getInstance().getBackgroundStyle() != null)
            _wrapper.addStyleName(UserInfo.getInstance().getBackgroundStyle());

        // setScrollMode(ScrollMode.AUTO);
    }

    /**
     * Add a new resource panel to the main resource container.
     * 
     * There is only one active CmResourcePanel active at any one time.
     * 
     * Each CmResourcePanel can be in one of two states:
     * 
     * 1. default state: static sizing define by the resource that is optimized
     * for the given content.
     * 
     * 2. (optional) Maximized, which will maximize the content within the
     * entire usable space of the resource content area.
     * 
     * 
     * 
     * @param panel
     */

    /**
     * Display a single resource, remove any previous
     * 
     * Do not track its view
     * 
     * @param resourceItem
     */
    public void showResource(final InmhItemData resourceItem) {
        showResource(resourceItem, isMaximized);
    }

    public void showResource(final InmhItemData resourceItem, final boolean viewMaximized) {
        try {
            _wrapper.clear();

            ResourceViewerFactory.ResourceViewerFactory_Client client = new ResourceViewerFactory.ResourceViewerFactory_Client() {
                @Override
                public void onUnavailable() {
                    CatchupMathTools.showAlert("Resource not available");
                }

                @Override
                public void onSuccess(ResourceViewerFactory instance) {
                    try {
                        CmResourcePanel viewer = instance.create(resourceItem);
                        showResource(viewer, resourceItem.getTitle(), viewMaximized);
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

    /**
     * show the last viewed resource, if any
     * 
     */
    public void showResource() {
        if (resourcePanel != null) {
            showResource(resourcePanel, currentTitle, isMaximized);
        }
    }

    public CmResourceContentPanel _lastContainer;

    public void showResource(CmResourcePanel viewer, String title, boolean viewMaximized) {

        /**
         * Create new ContentPanel each time, otherwise layout does not reset
         * margins.
         */
        CmResourceContentPanel currentContainer = new CmResourceContentPanel(viewer, title);
        currentContainer.setHeadingText(title);

        _lastContainer = currentContainer;

        resourcePanel = viewer;
        currentTitle = title;

        if (wrapperType == WrapperType.MAXIMIZED) {
            displayAsMaximized(currentContainer);
        } else {
            displayAsOptimized(currentContainer);
        }

        _wrapper.forceLayout();

        if (viewer instanceof ResourceViewerImplActivity || viewer instanceof CmResourcePanelImplWithWhiteboard
                || viewer instanceof ResourceViewerImplVideo) {
            /**
             * do not slide in activity to avoid bugs: - double load of flash
             * objecs - unsetting of external HTML radio buttons
             */
        } else {
            // currentContainer.el().slideIn(Direction.DOWN, FxConfig.NONE);
        }

        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_RESOURCE_VIEWER_OPEN, viewer));
    }

    public ResizeContainer getResourceWrapper() {
        return _wrapper;
    }

    static boolean isMaximized;

    private void displayAsOptimized(CmResourceContentPanel currentContainer) {

        if (currentContainer == null) {
            currentContainer = _lastContainer;
        }

        _panelOptimze.clear();
        currentContainer.setupDisplayForOptimize();

        fireWindowResized(currentContainer);

        _panelOptimze.setWidget(currentContainer);
        currentContainer.forceLayout();
        _panelOptimze.forceLayout();

        isMaximized = false;
    }

    private void displayAsMaximized(CmResourceContentPanel currentContainer) {
        if (currentContainer == null) {
            currentContainer = _lastContainer;
        }

        // _wrapper.clear();
        currentContainer.setupDisplayForMaximized();

        _panelMax.clear();
        _panelMax.setWidget(currentContainer);

        isMaximized = true;
    }

    public CmResourcePanel getCurrentPanel() {
        return resourcePanel;
    }

    public void setCurrentPanel(CmResourcePanel currentPanel) {
        this.resourcePanel = currentPanel;
    }

    /**
     * Remove this resource from display
     * 
     */
    public void removeResource() {
        resourcePanel = null;
        _wrapper.clear();
        // forceLayout();
    }

    /**
     * Determines resource dimension when in Optimized mode
     * 
     * Fired when the window container has been resized manually.
     */
    boolean isFiring;

    public void fireWindowResized() {
        fireWindowResized(_lastContainer);
    }

    public void fireWindowResized(final CmResourceContentPanel currentContainer) {

        if (wrapperType == WrapperType.MAXIMIZED) {
            return;
        }

        if (isFiring)
            return;

        if (resourcePanel != null && currentContainer.getViewerState() == ResourceViewerState.OPTIMIZED
                && resourcePanel.getOptimalHeight() == -1) {
            isFiring = true;
            /**
             * run in timer to allow x/y to be set first
             * 
             * @TODO: why isn't x/y already correct?
             */
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    try {
                        if (currentContainer != null) {
                            if(resourcePanel.getOptimalHeight() > -1) {
                                currentContainer.setHeight(resourcePanel.getOptimalHeight());
                            }
                            else {
                                int h = CmMainResourceWrapper_Old.getCalculatedHeight(CmMainPanel.__lastInstance, resourcePanel);
                                if (h > 0) {
                                    Info.display("fireWindowResized", " h==" + h);
                                    currentContainer.setHeight(h);
    
                                    getResourceWrapper().forceLayout();
                                }
                            }
                        }
                    } finally {
                        isFiring = false;
                    }
                }
            });
            
        } else if (resourcePanel != null && currentContainer.getViewerState() == ResourceViewerState.MAXIMIZED) {
            /**
             * Window changed while resource is MAXIMIZED
             * 
             */
            // CmMainResourceContainer.this.currentContainer.removeAll();
            // CmMainResourceContainer.this.currentContainer.setLayout(new
            // FitLayout());
            // CmMainResourceContainer.this.currentContainer.add(new
            // Button("Test"));
            // CmMainResourceContainer.this.currentContainer.layout();
        }
    }

    /**
     * Calculate the proper height for this widget.
     * 
     * If OptimialHeight is set to -1, then expand to available space.
     * Otherwise, return the static height.
     * 
     * Only shrink down to getMinHeight on panel.
     * 
     * @param container
     * @param panel
     * @return
     */
    static protected Integer getCalculatedHeight(Widget container, CmResourcePanel panel) {

        if (container == null) {
            return 0;
        }

        int HEADER_FOOTER_GUTTER = 10;
        if (panel.getOptimalHeight() == -1) {
            int h = container.getOffsetHeight() - HEADER_FOOTER_GUTTER;
            if (h < panel.getMinHeight()) {
                h = panel.getMinHeight();
            }
            return h;
        } else {
            return panel.getOptimalHeight();
        }
    }

    public String getCurrentTitle() {
        return currentTitle;
    }
}

/**
 * Modify the CenterLayout to not allow components to to be positioned out of
 * view. This causes the head portion to be unreachable.
 * 
 * @author casey
 * 
 */
// class MyCenterLayout extends CenterLayout {
//
// @Override
// protected void onLayout(Container<?> container, El target) {
// super.onLayout(container, target);
//
// Component item = container.getItem(0);
// if(container.getItemCount() > 0) {
// int top = item.getAbsoluteTop();
//
// if(top < 0)
// setPosition(item,item.el().getLeft() , 0);
// }
// }
// }

class MyCenterLayoutContainer extends CenterLayoutContainer {

    public void doLayoutNow() {
        layoutCommand.execute();
    }
}