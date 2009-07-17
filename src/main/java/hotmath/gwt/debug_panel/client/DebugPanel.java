package hotmath.gwt.debug_panel.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.debugpanel.client.DefaultCookieDebugPanelComponent;
import com.google.gwt.debugpanel.client.DefaultDebugStatisticsDebugPanelComponent;
import com.google.gwt.debugpanel.client.DefaultExceptionDebugPanelComponent;
import com.google.gwt.debugpanel.client.DefaultRawLogDebugPanelComponent;
import com.google.gwt.debugpanel.client.DefaultStatisticsModelRpcEventHandler;
import com.google.gwt.debugpanel.client.DefaultStatisticsModelStartupEventHandler;
import com.google.gwt.debugpanel.client.DelayedDebugPanelComponent;
import com.google.gwt.debugpanel.common.GwtStatisticsEventSystem;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsModel;
import com.google.gwt.debugpanel.models.GwtExceptionModel;
import com.google.gwt.debugpanel.widgets.DebugPanelWidget;
import com.google.gwt.user.client.ui.RootPanel;

public class DebugPanel implements EntryPoint, DebugPanelWidget.Listener {
    private GwtStatisticsEventSystem sys;
    private DefaultDebugStatisticsDebugPanelComponent panelComponent;
    private DelayedDebugPanelComponent xmlComponent;
    private DefaultRawLogDebugPanelComponent logComponent;

    private GwtDebugStatisticsModel sm;
    private GwtExceptionModel em;

    public void onModuleLoad() {
        sys = new GwtStatisticsEventSystem();
        panelComponent = new DefaultDebugStatisticsDebugPanelComponent(null);
        xmlComponent = panelComponent.xmlComponent();
        logComponent = new DefaultRawLogDebugPanelComponent(sys);
        em = new GwtExceptionModel();

        // Find the debug-panel div, or add it to (current) bottom of page.
        RootPanel root = RootPanel.get("debug-panel");
        if (root == null) {
            root = RootPanel.get();
        }

        root.add(
                new DebugPanelWidget(this, true, new DebugPanelWidget.Component[] {
                panelComponent,
                new DefaultExceptionDebugPanelComponent(em),
                new DefaultCookieDebugPanelComponent(),
                logComponent,
                xmlComponent
                }));
    }

    // @Override
    public void onShow() {
        panelComponent.reset(
                sm = new GwtDebugStatisticsModel(
                new DefaultStatisticsModelStartupEventHandler(),
                new DefaultStatisticsModelRpcEventHandler()));
        xmlComponent.reset();
        logComponent.reset();

        sys.addListener(sm, false);
        sys.addListener(em, false);
        sys.enable(true);
    }

    // @Override
    public void onReset() {
        sys.removeListener(sm);
        sys.removeListener(em);

        panelComponent.reset(null);
        sys.clearEventHistory();
    }

}
