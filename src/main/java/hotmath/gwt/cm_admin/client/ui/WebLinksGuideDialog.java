package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

public class WebLinksGuideDialog extends GWindow  {

    interface MyUiBinder extends UiBinder<Widget, WebLinksGuideDialog> {}
    MyUiBinder myUibinder = GWT.create(MyUiBinder.class);
    
    public WebLinksGuideDialog() {
        super(true);
        setHeadingText("Web Links Guide");
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.add(myUibinder.createAndBindUi(this));
        flow.setScrollMode(ScrollMode.AUTO);
        setWidget(flow);
        setVisible(true);
    }
    
}
