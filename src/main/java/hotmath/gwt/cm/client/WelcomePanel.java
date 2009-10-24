package hotmath.gwt.cm.client;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.util.UserInfo;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;

public class WelcomePanel extends LayoutContainer {
    
    Button _goBtn;
    public WelcomePanel() {
        
        setStyleName("cm-welcome-panel");
        setLayout(new CenterLayout());

        setScrollMode(Scroll.AUTO);
        ContentPanel main = new ContentPanel();

        main.setHeading("Welcome to Catchup Math");
        
        if(CmShared.getQueryParameterValue("type").equals("1")) {
            main.setSize(370,200);
            main.add(new SampleSessionInfo(), new BorderLayoutData(LayoutRegion.CENTER));
        }
        else if(CmShared.getQueryParameterValue("type").equals("2") || UserInfo.getInstance().getViewCount() == 0) {
            main.setSize(330,150);
            main.add(new FirstTimeInfo(), new BorderLayoutData(LayoutRegion.CENTER));
        }
        else {
            main.setSize(350,150);
            main.add(new ReturnUserInfo(), new BorderLayoutData(LayoutRegion.CENTER,200));
        }
        
        _goBtn = new Button("Begin Catchup Math");
        _goBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                startup();
            }
        });
        main.addButton(_goBtn);
        
        add(main);
    }
    
    
    private void startup() {
        try {
            CatchupMathTools.setBusy(true);
            CatchupMath.getThisInstance().startNormalOperation();
        }
        finally {
            CatchupMathTools.setBusy(false);
        }
    }

    /** Info for users taking the sample session
     * 
     * @author casey
     *
     */
    class SampleSessionInfo extends Html {
        
        public SampleSessionInfo() {
        
            String html = "<h1>Try a Pre-algebra Session Right Now</h1>" +
                          "<p>The button below begins a sample session with Catchup Math. " + 
                          "You will start with a 10-question quiz. Then, you will experience " +
                          " the review and practice of Catchup Math (be sure to get at least " + 
                          " one quiz problem wrong!)." +
                          "</p>";
            
            setHtml(html);
        }
    }
    
    /** Info for returning users
     * 
     * @author casey
     *
     */
    class ReturnUserInfo extends Html {
        
        public ReturnUserInfo() {
        
            String html = "<p>Use pencil and paper to work out the quiz problems.<br/>" +
                          "Then, you will start your personalized review and practice." +
                          "</p>";
            setHtml(html);
        }
    }
    
    
    /** Info for user's first visit to CM 
     * 
     * @author casey
     *
     */
    class FirstTimeInfo extends Html {
        public FirstTimeInfo() {
            String html = "<p>Please use pencil and paper to work out your quiz answers.<br/>" +
                           "Based on your answers we will assign your review and practice!</p>";
            
            setHtml(html);
        }
    }
}

