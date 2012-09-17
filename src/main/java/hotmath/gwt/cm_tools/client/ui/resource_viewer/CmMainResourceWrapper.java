package hotmath.gwt.cm_tools.client.ui.resource_viewer;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper.WrapperType;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.Container;
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
public class CmMainResourceWrapper {

    public static enum WrapperType {
        OPTIMIZED, MAXIMIZED
    };

    String currentTitle;

    SimpleContainer _panelMax;
    CenterLayoutContainer _panelOptimze;

    ResizeContainer _wrapper;

    WrapperType _wrapperType;

    public CmMainResourceWrapper(WrapperType wrapperType) {

        this._wrapperType = wrapperType;
        setWrapperMode(wrapperType);
    }

    public ResizeContainer getResourceWrapper() {
        return _wrapper;
    }

    public void showResource() {
        CmLogger.debug("Show Resource");
    }

    boolean isFiring;

    public void fireWindowResized() {
        Widget w = _wrapper.getWidgetCount()>0?_wrapper.getWidget(0):null;
        if(w instanceof CmResourceContentPanel) {
            CmResourceContentPanel c = (CmResourceContentPanel) ((Container) _wrapper).getWidget(0);
            fireWindowResized(c);
        }
    }

    public void fireWindowResized(final CmResourceContentPanel container) {
        if (_wrapperType == WrapperType.MAXIMIZED) {
            return;
        }

        if (isFiring)
            return;

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
                    CmResourcePanel resourcePanel = container.getResourcePanel();
                    if(resourcePanel.getOptimalHeight() > -1) {
                        container.setHeight(resourcePanel.getOptimalHeight());
                        container.forceLayout();
                    }
                    else {
                        int h = CmMainResourceWrapper_Old.getCalculatedHeight(CmMainPanel.__lastInstance, resourcePanel);
                        if (h > 0) {
                            CmLogger.debug("fireWindowResized: h==" + h);
                            container.setHeight(h);
                        }
                                                
                    }
                } finally {
                    isFiring = false;
                }
                
                CmMainPanel.__lastInstance.forceLayout();
            }
        });

    }

    static public Integer getCalculatedHeight(Widget container) {
        if (container == null) {
            return 0;
        }
        int HEADER_FOOTER_GUTTER = 10;
        int h = container.getOffsetHeight() - HEADER_FOOTER_GUTTER;
        return h;
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    CmResourceContentPanel _contentPanel;
    public void setContentPanel(CmResourceContentPanel cmResourceContentPanel) {
        _contentPanel = cmResourceContentPanel;
        fireWindowResized(cmResourceContentPanel);
        getResourceWrapper().add(cmResourceContentPanel);
    }

    public void setWrapperMode(WrapperType mode) {
        _wrapperType = mode;
        
        switch (mode) {
        case OPTIMIZED:
            _panelOptimze = new CenterLayoutContainer();
            _wrapper = _panelOptimze;
            break;

        case MAXIMIZED:
            _panelMax = new SimpleContainer();
            _wrapper = _panelMax;
            break;
        }
        _wrapper.addStyleName("resource-container");
        _wrapper.addStyleName("main-resource-panel");

        if (UserInfo.getInstance() != null && UserInfo.getInstance().getBackgroundStyle() != null)
            _wrapper.addStyleName(UserInfo.getInstance().getBackgroundStyle());

        // setScrollMode(ScrollMode.AUTO);
    }

    public void resetWrapperMode(WrapperType type) {
        setWrapperMode(type);
        _wrapper.add(_contentPanel);
        
        _wrapper.forceLayout();
        CmMainPanel.__lastInstance.forceLayout();
    }

    public WrapperType getWrapperMode() {
        return _wrapperType;
    }
}
