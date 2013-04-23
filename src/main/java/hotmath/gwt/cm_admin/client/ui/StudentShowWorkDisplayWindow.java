package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm_tools.client.util.CmMessageBoxGxt2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel.ShowWorkPanelCallback;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

public class StudentShowWorkDisplayWindow extends Window {
    
    ShowWorkPanel _showWorkPanel;
    
    public StudentShowWorkDisplayWindow(final StudentModel student, final String pid) {
        setModal(true);
        setLayout(new BorderLayout());        
        setSize(700,500);
        
        BorderLayoutData ld = new BorderLayoutData(LayoutRegion.NORTH, 300);
        ld.setSplit(true);
        
        UserInfo user = new UserInfo(student.getUid(), 0);
        UserInfo.setInstance(user);
        _showWorkPanel = new ShowWorkPanel(new ShowWorkPanelCallback() {
            @Override
            public void showWorkIsReady() {
                new RetryAction<CmList<WhiteboardCommand>>() {
                    @Override
                    public void attempt() {
                        CmBusyManager.setBusy(true);
                        GetWhiteboardDataAction action = new GetWhiteboardDataAction(student.getUid(), pid,  UserInfo.getInstance().getRunId());
                        setAction(action);
                        CmShared.getCmService().execute(action, this);
                    }

                    public void oncapture(CmList<WhiteboardCommand> whiteboardCommands) {
                        try {
                            _showWorkPanel.loadWhiteboard(whiteboardCommands);
                            _showWorkPanel.setAsTeacherMode(true);
                        } finally {
                            CmBusyManager.setBusy(false);
                        }
                    }
                }.register();
            }
            
            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                int runId=UserInfo.getInstance().getRunId();
                return new SaveWhiteboardDataAction(student.getUid(),runId, pid, commandType, data);
            }
            
            @Override
            public void windowResized() {
                doLayout();
            }            
            
            
        });
        add(_showWorkPanel,ld);
        _showWorkPanel.setupForPid(pid);
        
        
        final InmhItemData solItem = new InmhItemData();
        solItem.setType("practice");
        solItem.setFile(pid);
        try {
        	
        	ResourceViewerFactory.ResourceViewerFactory_Client client = new ResourceViewerFactory.ResourceViewerFactory_Client() {
				
				@Override
				public void onUnavailable() {
					CmMessageBoxGxt2.showAlert("Error creating resource: " + solItem);
				}
				
				@Override
				public void onSuccess(ResourceViewerFactory instance) {
				    try {
		                 CmResourcePanel resourcePanel = instance.create(solItem);
		                 add(resourcePanel.getResourcePanel(), new BorderLayoutData(LayoutRegion.CENTER));
		                 layout();
				    }
				    catch(Exception e) {
				    	CmMessageBoxGxt2.showAlert("Error creating resource: " + e.getLocalizedMessage());
				    }
				}
			};
        }
        catch(Exception e) {
            e.printStackTrace();
        }        
        
        setVisible(true);
    }

}
