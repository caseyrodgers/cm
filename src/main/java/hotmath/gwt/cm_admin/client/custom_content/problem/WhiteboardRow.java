package hotmath.gwt.cm_admin.client.custom_content.problem;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;



public class WhiteboardRow extends SimpleContainer {
    public WhiteboardRow() {
        setHeight(300);
        setWidget(new HTML("<div class='cm_whiteboard' wb_id='wb_ps'></div>"));
    }
    
    
    native public void initializeWhiteboard() /*-{
        $wnd.setupStaticWhiteboards();
    }-*/;
    
}
