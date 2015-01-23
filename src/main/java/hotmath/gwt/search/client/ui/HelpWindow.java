package hotmath.gwt.search.client.ui;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;
import hotmath.gwt.shared.client.util.FeedbackWindow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Frame;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class HelpWindow extends GWindow {
    public  HelpWindow() {
        super(false);
        
        //setAutoHeight(true);
        setPixelSize(450,350);
        
        setModal(true);
        setResizable(false);
        //setStyleName("help-window");
        setHeadingText("Catchup Math Search Help Window");
        
        Frame frame = new Frame("/gwt-resources/search-help.html");
        frame.setSize("100%", "450px");
        
        addButton(new TextButton("Enter Feedback", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new FeedbackWindow("isAdmin=true");
            }
        }));

        addCloseButton();

        add(frame);
        
        setVisible(true);
        
        if(CmCore.isDebug() == true) {
            GWT.runAsync(new CmRunAsyncCallback() {
                @Override
                public void onSuccess() {
                    getHeader().addTool(new TextButton("Version", new SelectHandler() {
                        @Override
                        public void onSelect(SelectEvent event) {
                            CatchupMathTools.showVersionInfo();
                        }
                    }));
                }
            });
        }        
    }

}
