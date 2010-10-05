package hotmath.gwt.cm_mobile_shared.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


public class ControlPanel extends FlowPanel {
    Widget collapsed;
    FlowPanel expanded;
    public ControlPanel() {
        getElement().setId("control-floater");
        Anchor anchor = new Anchor("<<<");
        anchor.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {
                showControlPanel();
            }
        });
        collapsed = anchor;
        add(anchor);
        
        FlowPanel fp = new FlowPanel();
        expanded = fp;
        expanded.getElement().setId("control-panel-expanded");
        RootPanel.get().add(expanded);   
        
        
        setControlActions(null);
    }
    
    Widget busyWidget=null;
    public void showBusy(boolean show) {
        if(show) {
            if(busyWidget == null) {
                /** not already busy */
                busyWidget = new HTML("<img src='/gwt-resources/images/mobile/spinner.gif' />");
                add(busyWidget);
            }
        }
        else {
            if(busyWidget != null) {
                remove(busyWidget);
                busyWidget = null;
            }
        }
    }
    
    public void hideControlPanelFloater() {
        getElement().setClassName("hide");
    }
    
    public void showControlPanelFloater() {
        getElement().removeClassName("hide");
    }
    
    private void showControlPanel() {
        setExpandedDimensions();
        expanded.getElement().setClassName("show");
    }

        
    private native void setExpandedDimensions() /*-{
    
        var visibleSize = $wnd.getViewableSize();
    
        var scrollXy = $wnd.getScrollXY();
        // alert(visibleSize + "/" + scrollXy);
        
        var visTop = scrollXy[1];
        var top = visTop + 30; // at least 30 px
        
        var cp = $doc.getElementById("control-floater");
        var left = $wnd.DL_GetElementLeft(cp);
        
        var controlPanel = $doc.getElementById("control-panel-expanded");
        var width = $wnd.grabComputedWidth(controlPanel);
        var right = left - width;
        if(right < 0)
           right = 0;
           
        controlPanel.style.left = right + "px"; 
        controlPanel.style.top = top + "px";
        
        controlPanel.style.display = "block";
        
        // hide it in 5 seconds
        setTimeout(function(){controlPanel.style.display = 'none';},5000);
        
}-*/;

   
    
    private native void hideControlPanel() /*-{
        var controlPanel = $doc.getElementById("control-panel-expanded");
        controlPanel.style.display = 'none';
    }-*/;

    
    public void setControlActions(List<ControlAction> actions) {
        expanded.clear();
        
        /** Add default actions */
        List<ControlAction> defaultActions = new ArrayList<ControlAction>();
        defaultActions.add(new ControlAction("Search for a lesson") {
            @Override
            public void doAction() {
                Controller.navigateToTopicList();
            }
        });
        for(final ControlAction action: defaultActions) {
            Button btn = new Button(action.getLabel(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent arg0) {
                    action.doAction();
                    hideControlPanel();                    
                }
            });
            expanded.add(btn);
        }

        if(actions != null) {
            /** Add any custom actions */
            for(final ControlAction action: actions) {
                Button btn = new Button(action.getLabel(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent arg0) {
                        action.doAction();
                        hideControlPanel();                    
                    }
                });
                expanded.add(btn);
            }
        }
    }
}