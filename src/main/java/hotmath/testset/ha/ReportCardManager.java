package hotmath.testset.ha;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.testset.ha.info.UserProgramInfo;
import hotmath.testset.ha.info.UserProgramInfoDao;

import java.sql.Connection;
import java.util.List;

/** Provide management/creation tools for creating
 *  a report card object.
 *  
 *  
 * @author casey
 *
 */
public class ReportCardManager {

    
    static public StudentReportCardModel2 createModel(final Connection conn, Integer userId) throws Exception {
        StudentReportCardModel2 rmodel = new StudentReportCardModel2();

        StudentModelI student = CmStudentDao.getInstance().getStudentModel(conn, userId, false);
        List<UserProgramInfo> userInfos = new UserProgramInfoDao().getUserProgramInfoForUser(conn, userId);
        
        UserProgramInfoDao upid = new UserProgramInfoDao();
        
        rmodel.setAdminUid(student.getAdminUid());
        rmodel.setStudentName(student.getName());
        rmodel.setFirstActivityDate(upid.getUserFirstActivityDate(conn, userId));
        return rmodel;
    }


}
