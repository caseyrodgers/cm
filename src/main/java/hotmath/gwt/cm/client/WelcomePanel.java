package hotmath.gwt.cm.client;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.CmBusyManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class WelcomePanel extends CenterLayoutContainer {

    TextButton _goBtn;

    static public WelcomePanel __instance;
    public WelcomePanel() {
        __instance = this;
        
        addStyleName("cm-welcome-panel");
        addStyleName("resource-container");
        addStyleName(UserInfo.getInstance().getBackgroundStyle());

        ContentPanel main = new ContentPanel();
        main.addStyleName("welcome-wrapper");

        main.setHeadingText("Welcome to Catchup Math");

        if (CmCore.getQueryParameterValue("type").equals("1")) {
            main.setPixelSize(370,190);
            main.add(new SampleSessionInfo());
        } else if (CmCore.getQueryParameterValue("type").equals("2") || UserInfo.getInstance().getViewCount() == 0) {
            main.setPixelSize(330, 200);
            main.add(new StandardInfo());
        } else {
            main.setPixelSize(350, 200);
            main.add(new StandardInfo());
        }

        _goBtn = new TextButton("Begin Catchup Math");
        _goBtn.setId("welcome-panel-go");
        _goBtn.addSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                startup();                
            }
        })
        ;
        main.addButton(_goBtn);
        
        
//        main.addButton(new TextButton("Calculator", new SelectHandler() {
//            @Override
//            public void onSelect(SelectEvent event) {
//                CalculatorWindow.getInstance().setVisible(true);
//            }
//        }));

        main.setButtonAlign(BoxLayoutPack.CENTER);
        
        //main.setStyleAttribute("padding", "0 5px 0 5px");

        add(main);
    }

    private void startup() {
        
        _goBtn.setEnabled(false);
        try {
            CmBusyManager.setBusy(true,false);

            GWT.runAsync(new RunAsyncCallback() {

                @Override
                public void onSuccess() {
                    CatchupMath.getThisInstance().startNormalOperation();
                }

                @Override
                public void onFailure(Throwable reason) {
                    _goBtn.setEnabled(true);
                    Window.alert("Error starting CM normal operations: " + reason.getLocalizedMessage());
                }
            });
        } finally {
            CmBusyManager.setBusy(false);
        }
    }

    /**
     * Info for users taking the sample session
     * 
     * @author casey
     * 
     */
    class SampleSessionInfo extends HTML {

        public SampleSessionInfo() {

            String html = "<h1>Try a Pre-algebra Session Right Now</h1>"
                    + "<p>Please <b>mark at least one quiz problem wrong</b> so you experience the review and practice.</p>";

            setHTML(html);
        }
    }

    /**
     * Info for user's first visit to CM
     * 
     * @author casey
     * 
     */
    class StandardInfo extends HTML {
        public StandardInfo() {
            String html = "";
            if(UserInfo.getInstance().getRunId() > 0){
                html = "<p>You will start this Catchup Math session with a lesson.</p>";
            }
            else {
                html = "<p>You will start this Catchup Math session with a quiz.</p>";
            }
            
            html += "<p>Please work out your answers carefully using our whiteboard or pencil and paper.</p>" +
                    "<a style='display: block;margin-left: 10px;text-decoration: underline;color: #00A8FF;' " +
                    " href='#' onclick='showMotivationalVideo_Gwt();return false;'>Video: How to use Catchup Math</a>";
            
            html = "<div class='welcome-wrapper-internal'>" + html + "</div>";
            setHTML(html);
        }
    }
}
