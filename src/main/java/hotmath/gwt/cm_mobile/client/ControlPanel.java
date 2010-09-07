package hotmath.gwt.cm_mobile.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


public class ControlPanel extends FlowPanel {
    Widget collapsed;
    Widget expanded;
    public ControlPanel() {
        getElement().setId("control-panel");
        List<ControlAction> controls = new ArrayList<ControlAction>();
        controls.add(new ControlAction("Test 1") {
            
            @Override
            void doAction() {
            }
        });
        controls.add(new ControlAction("Test 2") {
            
            @Override
            void doAction() {
            }
        });
        setControlActions(controls);
        
        Anchor anchor = new Anchor("<<");
        anchor.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {
                showControlPanel();
            }
        });
        collapsed = anchor;
        add(anchor);
    }
    
    private void showControlPanel() {
        

        setExpandedDimensions();
        expanded.getElement().setClassName("show");
    }

        
    private native int setExpandedDimensions() /*-{
    
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
        
        setTimeout(function(){controlPanel.style.display = 'none';},5000);
        
}-*/;

   
    
    private native void hideControlPanel() /*-{
        var controlPanel = $doc.getElementById("control-panel-expanded");
        controlPanel.style.display = 'none';
    }-*/;

    
    public void setControlActions(List<ControlAction> actions) {
        FlowPanel fp = new FlowPanel();
        for(ControlAction action: actions) {
            Button btn = new Button(action.getLabel(), new ClickHandler() {
                
                @Override
                public void onClick(ClickEvent arg0) {
                    hideControlPanel();                    
                }
            });
            fp.add(btn);
        }
        expanded = fp;
        
        expanded.getElement().setId("control-panel-expanded");
        RootPanel.get().add(expanded);
    }
}