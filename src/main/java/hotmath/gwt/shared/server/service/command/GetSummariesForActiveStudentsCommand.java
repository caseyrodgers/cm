package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetSummariesForActiveStudentsAction;
import hotmath.gwt.shared.server.service.ActionHandler;

import java.sql.Connection;


/** Return list of StudentModel records that provides summary information for a students under 
 *  a given admin.
 *  
 * @author casey
 *
 */
public class GetSummariesForActiveStudentsCommand implements ActionHandler<GetSummariesForActiveStudentsAction, CmList<StudentModelI>>{

    
    public CmList<StudentModelI> execute(Connection conn, GetSummariesForActiveStudentsAction action) throws Exception {
        CmStudentDao dao = new CmStudentDao();
        
        CmList<StudentModelI> cmList = new CmArrayList<StudentModelI>();
        cmList.addAll(dao.getSummariesForActiveStudents(conn, action.getAdminId()));
        
        return cmList;
    }

    public Class<? extends Action<? extends Response>> getActionType() {
        return GetSummariesForActiveStudentsAction.class;
    }

}
