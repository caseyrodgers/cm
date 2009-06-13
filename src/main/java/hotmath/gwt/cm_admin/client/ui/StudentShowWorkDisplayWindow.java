package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewer;
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
        ShowWorkPanel workPanel = new ShowWorkPanel();
        add(workPanel,ld);
        workPanel.setupForPid(pid);
        
        
        InmhItemData solItem = new InmhItemData();
        solItem.setType("practice");
        solItem.setFile(pid);
        try {
            ResourceViewer viewer = ResourceViewerFactory.create(solItem.getType());
            add(viewer.getResourcePanel(solItem), new BorderLayoutData(LayoutRegion.CENTER));
        }
        catch(Exception e) {
            e.printStackTrace();
        }        
        
        setVisible(true);
    }

}
