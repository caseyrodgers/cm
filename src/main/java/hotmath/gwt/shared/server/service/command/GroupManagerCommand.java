package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_admin.server.model.ParallelProgramDao;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAction;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class GroupManagerCommand implements ActionHandler<GroupManagerAction, RpcData> {

    static Logger __logger = Logger.getLogger(GroupManagerCommand.class);

    @Override
    public RpcData execute(Connection conn, GroupManagerAction action) throws Exception {

        RpcData rpcData = new RpcData();

        switch (action.getActionType()) {
            case DELETE:
                doDelete(conn, action.getAdminId(), action.getGroupId());
                break;
    
            case UPDATE:
                doUpdate(conn, action.getAdminId(), action.getGroupId(), action.getGroupName());
                break;
    
            case UNREGISTER_STUDENTS:
                doUnregister(conn, action.getAdminId(), action.getGroupId());
                break;
    
            case GROUP_PROGRAM_ASSIGNMENT:
                doGroupProgramAssignment(conn, action.getAdminId(), action.getGroupId(), action.getStudentModel(),
                        action.getIsSelfReg() > 0);
                break;
    
            case GROUP_PROPERTY_SET:
                doGroupPropertySet(conn, action.getAdminId(), action.getGroupId(), action.getShowWorkRequired(),
                        action.getDisallowTutoring(), action.getLimitGames(), action.getStopAtProgramEnd(),
                        action.getPassPercent(), action.getDisableCalcAlways(), action.getDisableCalcQuizzes(),
                        action.isNoPublicWebLinks(), action.isDisableSearch());
                break;
    
            default:
                __logger.warn("Unknown ActionType: " + action.getActionType());
        }

        rpcData.putData("status", "OK");
        return rpcData;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GroupManagerAction.class;
    }

    /**
     * Delete existing group name Does not delete the actual students, just the
     * group name. Sets to null the group for existing group members.
     * 
     * If group is a self-reg-group, then it also remove the group template
     * 
     * @param conn
     * @param groupId
     * @throws Exception
     */
    private void doDelete(final Connection conn, Integer adminId, Integer groupId) throws Exception {
        CmAdminDao.getInstance().deleteGroup(conn, adminId, groupId);
    }

    /**
     * Update group name identified by groupId
     * 
     * @param conn
     * @param groupId
     * @param groupName
     * @throws Exception
     */
    private void doUpdate(final Connection conn, Integer adminUid, Integer groupId, String groupName) throws Exception {
        CmAdminDao.getInstance().updateGroup(conn, adminUid, groupId, groupName);
    }

    private void doUnregister(final Connection conn, Integer adminId, Integer groupId) throws Exception {
        CmStudentDao dao = CmStudentDao.getInstance();
        List<StudentModelI> cmList = new ArrayList<StudentModelI>();

        PreparedStatement ps = null;
        try {
            if (groupId == -1) {
                String sql = "select uid from HA_USER where is_active = 1 and admin_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, adminId);
            } else {
                String sql = "select uid from HA_USER where is_active = 1 and admin_id = ? and group_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, adminId);
                ps.setInt(2, groupId);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StudentModel sm = new StudentModel();
                sm.setUid(rs.getInt("uid"));
                cmList.add(sm);
            }

            dao.unregisterStudents(conn, cmList);
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    /**
     * Assign group information assigned to studentTemplate to all students in
     * named group
     * 
     * @param conn
     * @param adminId
     * @param groupId
     * @param studentTemplate
     * @throws Exception
     */
    private void doGroupProgramAssignment(final Connection conn, Integer adminId, Integer groupId,
            StudentModel studentTemplate, boolean isSelfReg) throws Exception {
        PreparedStatement ps = null;
        try {
            if (groupId == -1) {
                String sql = "select uid from HA_USER where is_active = 1 and is_auto_create_template = 0 and admin_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, adminId);
            } else if (!isSelfReg) {
                String sql = "select uid from HA_USER where is_active = 1 and is_auto_create_template = 0 and admin_id = ? and group_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, adminId);
                ps.setInt(2, groupId);
            } else {
                /**
                 * is a self registration group. We must make sure to update the
                 * self_reg group
                 */
                String sql = "select uid from HA_USER where is_active = 1 and admin_id = ? and group_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, adminId);
                ps.setInt(2, groupId);
            }

            // String passPercent = studentTemplate.getPassPercent();

            CmStudentDao dao = CmStudentDao.getInstance();
            ParallelProgramDao ppDao = ParallelProgramDao.getInstance();

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("uid");
                if (ppDao.isStudentInParallelProgram(userId) == true) {
                    // update Active Info in CM_PROGRAM_ASSIGN before assigning
                    // new Program
                    ppDao.updateProgramAssign(userId);
                }
                dao.assignProgramToStudent(conn, userId, studentTemplate.getProgram(), studentTemplate.getChapter(),
                        studentTemplate.getPassPercent(), studentTemplate.getSettings(), isSelfReg,
                        studentTemplate.getSectionNum());
            }
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    /**
     * Apply properties to each student in group
     * 
     * @throws Exception
     */
    private void doGroupPropertySet(final Connection conn, Integer adminId, Integer groupId, Boolean showWorkRequired,
            Boolean disallowTutoring, Boolean limitGames, Boolean stopAtProgramEnd, Integer passPercent,
            Boolean disableCalcAlways, Boolean disableCalcQuizzes, boolean isNoPublicWebLinks, boolean isSearchDisabled) throws Exception {
        PreparedStatement ps = null;
        try {
            if (groupId == -1) {
                String sql = "select uid from HA_USER where is_active = 1 and admin_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, adminId);
            } else {
                String sql = "select uid from HA_USER where is_active = 1 and group_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, groupId);
            }

            CmStudentDao dao = CmStudentDao.getInstance();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                dao.updateStudentSettings(conn, rs.getInt("uid"), showWorkRequired, !disallowTutoring, limitGames,
                        stopAtProgramEnd, passPercent, disableCalcAlways, disableCalcQuizzes, isNoPublicWebLinks, isSearchDisabled);
            }
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }
}
