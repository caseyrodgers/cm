package hotmath.gwt.cm.client;

import hotmath.gwt.cm.client.history.CatchupMathHistoryListener;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.util.ReportCardInfoPanel;
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
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;

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
            main.setSize(440,250);
            main.add(new FirstTimeInfo(), new BorderLayoutData(LayoutRegion.CENTER));
        }
        else {
            main.setSize(350,200);
            main.add(new ReportCardInfoPanel(UserInfo.getInstance()), new BorderLayoutData(LayoutRegion.CENTER,200));
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

    class SampleSessionInfo extends Html {
        
        public SampleSessionInfo() {
        
            String html = "<h1>Try a Pre-algebra Session Right Now</h1>" +
                          "<p>The button below begins a sample session with Catchup Math. " + 
                          "You will start with a 10-question quiz. Then, you will experience " +
                          " the review and practice of Catchup Math (be sure to get at least " + 
                          " one quiz problem wrong! " +
                          "</p>";
            
            setHtml(html);
        }
    }
    
    class FirstTimeInfo extends LayoutContainer {
        String _html1, _html2;
        public FirstTimeInfo() {
            
            setLayout(new FitLayout());
            _html1 =      "<h1>First time user information</h1> " +
                          "<ul> " +
                          "<li>Take quizzes to find out what you are ready to learn next.</li> " +
                          "<li>Then, go through the menu items in any order you like.</li> " +
                          "<li>You'll learn fastest if you try the problems on your own.</li> " +
                          "<li>Use paper and pencil to work out your answers.</li> " +
                          "</ul> ";
            _html2 =      "<h1>First time user information, page 2</h1> " +
                          "<ul>" +
                          "<li>You can change your wallpaper using [Help].</li> " +
                          "<li>You can see your historical progress on [Help].</li> " +
                          "<li>Use the Flash Cards if you find them helpful.</li> " +
                          "<li>We hope you like Catchup Math!</li> " +
                          "</ul>";
            
            showPageOne();
        }
        
        private void showPageOne() {
            removeAll();
            add(new Html(_html1));
            Anchor btn = new Anchor("More Info >>");
            btn.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    showPageTwo();
                }
            });
            add(btn);
            layout();
        }
        
        private void showPageTwo() {
            removeAll();
            add(new Html(_html2));
            Anchor btn = new Anchor("<< Back");
            btn.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    showPageOne();
                }
            });
            add(btn);
            layout();
        }
        
    }
}

