package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction.TestApplication;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;
import hotmath.gwt.shared.client.util.NetTestWindow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Frame;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class HelpWindow extends GWindow {

    public  HelpWindow() {
    	super(false);
    	
        //setAutoHeight(true);
        setPixelSize(640,520);
        
        setModal(true);
        setResizable(false);
        //setStyleName("help-window");
        setHeadingText("Catchup Math Administration Help Window");
        
        Frame frame = new Frame("/gwt-resources/cm-admin-help.html");
        frame.setSize("100%", "450px");
        
        TextButton webinar = new TextButton("Teacher Training Videos", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
            	showTrainingVideosPage();
            }
        });
        webinar.setToolTip("Watch our training videos.");
        addButton(webinar);
        addCloseButton();

        add(frame);
        
        setVisible(true);
        
        if(CmShared.getQueryParameter("debug") != null) {
            GWT.runAsync(new CmRunAsyncCallback() {
                @Override
                public void onSuccess() {
                    getHeader().addTool(new TextButton("Version", new SelectHandler() {
                        @Override
                        public void onSelect(SelectEvent event) {
                            CatchupMathTools.showVersionInfo();
                        }
                    }));
                    getHeader().addTool(new TextButton("Net Test", new SelectHandler() {
                        @Override
                        public void onSelect(SelectEvent event) {
                            GWT.runAsync(new CmRunAsyncCallback() {
                                @Override
                                public void onSuccess() {
                                    new NetTestWindow(TestApplication.CM_ADMIN,StudentGridPanel.instance._cmAdminMdl.getUid()).runTests();
                                }
                            });
                        }
                    }));
                }
            });
        }        
    }

    private native void showTrainingVideosPage() /*-{
        var tv = window.open('/training-videos');
        tv.focus();
    }-*/;

}