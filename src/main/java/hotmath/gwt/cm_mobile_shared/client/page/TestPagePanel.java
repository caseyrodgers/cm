package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;

import com.google.gwt.user.client.ui.TextBox;

public class TestPagePanel extends AbstractPagePanel {
    
    public TestPagePanel(TestPage page) {
        TextBox tb = new TextBox();
        initWidget(tb);
    }
}
