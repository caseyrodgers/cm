package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm_tools.client.ui.viewer.ShowWorkPanel;
import hotmath.gwt.shared.client.util.UserInfo;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

public class StudentShowWorkDisplayWindow extends Window {
    
    public StudentShowWorkDisplayWindow(StudentModel student, String pid) {
        setModal(true);
        setLayout(new BorderLayout());        
        setSize(700,500);
        
        BorderLayoutData ld = new BorderLayoutData(LayoutRegion.NORTH, 300);
        ld.setSplit(true);
        
        UserInfo user = new UserInfo(student.getUid(), 0);
        UserInfo.setInstance(user);
        ShowWorkPanel workPanel = new ShowWorkPanel(null);
        add(workPanel,ld);
        workPanel.setupForPid(pid);
        
        
        final InmhItemData solItem = new InmhItemData();
        solItem.setType("practice");
        solItem.setFile(pid);
        try {
        	
        	ResourceViewerFactory.ResourceViewerFactory_Client client = new ResourceViewerFactory.ResourceViewerFactory_Client() {
				
				@Override
				public void onUnavailable() {
					CatchupMathTools.showAlert("Error creating resource: " + solItem);
				}
				
				@Override
				public void onSuccess(ResourceViewerFactory instance) {
				    try {
		                 CmResourcePanel resourcePanel = instance.create(solItem);
		                 add(resourcePanel.getResourcePanel(), new BorderLayoutData(LayoutRegion.CENTER));
		                 layout();
				    }
				    catch(Exception e) {
				    	CatchupMathTools.showAlert("Error creating resource: " + e.getLocalizedMessage());
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
