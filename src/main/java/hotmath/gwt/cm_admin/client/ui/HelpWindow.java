package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CatchupMathVersionInfo;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction.TestApplication;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;
import hotmath.gwt.shared.client.util.NetTestWindow;
import hotmath.gwt.shared.client.util.UserInfo;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Frame;


public class HelpWindow extends CmWindow {

    public  HelpWindow() {
        setAutoHeight(true);
        setSize(640,480);
        
        setModal(true);
        setResizable(false);
        setStyleName("help-window");
        setHeading("Catchup Math Administration Help Window, version: " + CatchupMathVersionInfo.getBuildVersion());
        
        Frame frame = new Frame("/gwt-resources/cm-admin-help.html");
        frame.setSize("100%", "450px");
        
        Button closeBtn = new Button("Close");
        closeBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                HelpWindow.this.close();
            }
        });
        addButton(closeBtn);
        
        add(frame);
        
        setVisible(true);
        
        
        if(CmShared.getQueryParameter("debug") != null) {
            GWT.runAsync(new CmRunAsyncCallback() {
                @Override
                public void onSuccess() {
                    getHeader().addTool(new Button("Version", new SelectionListener<ButtonEvent>() {
                        @Override
                        public void componentSelected(ButtonEvent ce) {
                            CatchupMathTools.showVersionInfo();
                        }
                    }));
                    getHeader().addTool(new Button("Net Test", new SelectionListener<ButtonEvent>() {
                        @Override
                        public void componentSelected(ButtonEvent ce) {
                            GWT.runAsync(new CmRunAsyncCallback() {
                                @Override
                                public void onSuccess() {
                                    new NetTestWindow(TestApplication.CM_ADMIN,StudentGridPanel.instance._cmAdminMdl.getId()).runTests();
                                }
                            });
                        }
                    }));
                    getHeader().addTool(new Button("CmLogger", new SelectionListener<ButtonEvent>() {
                        @Override
                        public void componentSelected(ButtonEvent ce) {
                            GWT.runAsync(new CmRunAsyncCallback() {
                                @Override
                                public void onSuccess() {
                                    CmLogger.getInstance().enable(true);
                                }
                            });
                        }
                    }));
                    
                }
            });
        }        
    }
}

