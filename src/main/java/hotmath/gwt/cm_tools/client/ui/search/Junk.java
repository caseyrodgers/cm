package hotmath.gwt.cm_tools.client.ui.search;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

public class Junk extends SimpleContainer {

    public Junk() {
        
        TabPanel tp = new TabPanel();
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.setScrollMode(ScrollMode.AUTO);
        flow.add(new HTML(createLongText()));
        tp.add(flow, new TabItemConfig("TEst"));
        setWidget(tp);
    }

    
    private String createLongText() {
        String text="";
        for(int j=0;j<200;j++) {
            for(int i=0;i< 20;i++) {
                text += i;
            }
            text += "\n";
        }
        return text;
    }


    static public void doTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                GWindow gWindow = new GWindow(true);
                gWindow.setWidget(new Junk());
                gWindow.setVisible(true);
            }
        });
    }
}