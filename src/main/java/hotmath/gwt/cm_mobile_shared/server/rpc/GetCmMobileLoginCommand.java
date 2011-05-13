package hotmath.gwt.cm_mobile_shared.server.rpc;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.data.HaBasicUser.UserType;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.testset.ha.HaUserFactory;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GetCmMobileLoginCommand implements ActionHandler<GetCmMobileLoginAction, CmMobileUser> {

    @Override
    public CmMobileUser execute(Connection conn, GetCmMobileLoginAction action) throws Exception {
        
        HaBasicUser basicUser = HaUserFactory.loginToCatchup(conn, action.getName(), action.getPassword());
        if(basicUser.getUserType() != UserType.STUDENT)
            throw new CmException("Invalid user type: " + basicUser.getUserType());
        
        StudentModelI sm = CmStudentDao.getInstance().getStudentModelBase(conn, basicUser.getUserKey());
        StudentActiveInfo active = CmStudentDao.getInstance().loadActiveInfo(sm.getUid());
        CmMobileUser mobileUser = new CmMobileUser(sm.getUid(),active.getActiveTestId(),active.getActiveSegment(),active.getActiveSegmentSlot(),active.getActiveRunId());
        
        /** get list of previous prescribed lessons? 
         * 
         */
        PreparedStatement ps = null;
        try {
            String sql = "select concat(lesson_name,concat(concat(' (times: ', count(*))),')') as lesson_name, lesson_file, count(*) " +
                         " from HA_TEST_RUN_LESSON l " +
                         " JOIN HA_TEST_RUN r on r.run_id = l.run_id " +
                         " JOIN HA_TEST t on t.test_id = r.test_id " +
                         " where t.user_id = ? " +
                         " group by lesson_name " +
                         " order by lesson_name;";
           ps = conn.prepareStatement(sql);
           ps.setInt(1, mobileUser.getUserId());
           CmList<Topic> ptopics = new CmArrayList<Topic>();
           ResultSet rs = ps.executeQuery();
           while(rs.next()) {
               ptopics.add(new Topic(rs.getString("lesson_name"), rs.getString("lesson_file")));
           }
           mobileUser.setPrescribedLessons(ptopics);
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return mobileUser;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCmMobileLoginAction.class;
    }
}
