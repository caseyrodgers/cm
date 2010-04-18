package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAction;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class GroupManagerCommand implements ActionHandler<GroupManagerAction, RpcData>{

    static Logger __logger = Logger.getLogger(GroupManagerCommand.class);
    
    @Override
    public RpcData execute(Connection conn, GroupManagerAction action) throws Exception {
        
        RpcData rpcData = new RpcData();
        if(action.getActionType() == GroupManagerAction.ActionType.DELETE)
            doDelete(conn,action.getAdminId(), action.getGroupId());
        else if(action.getActionType() == GroupManagerAction.ActionType.UPDATE)
            doUpdate(conn,action.getGroupId(), action.getGroupName());
        else if(action.getActionType() == GroupManagerAction.ActionType.UNREGISTER_STUDENTS)
            doUnregister(conn, action.getAdminId(), action.getGroupId());
        else if(action.getActionType() == GroupManagerAction.ActionType.GROUP_PROGRAM_ASSIGNMENT)
            doGroupProgramAssignment(conn,action.getAdminId(),action.getGroupId(), action.getStudentModel(), action.getIsSelfReg());
        else if(action.getActionType() == GroupManagerAction.ActionType.GROUP_PROPERTY_SET)
            doGroupPropertySet(conn,action.getAdminId(),action.getGroupId(),action.getShowWorkRequired(),action.getDisallowTutoring(),
            		action.getLimitGames(), action.getStopAtProgramEnd(), action.getPassPercent());
        
        rpcData.putData("status","OK");
        return rpcData;
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GroupManagerAction.class;
    }
    
    
    /** Delete existing group name
     *  Does not delete the actual students, just the group name.
     *  Sets to null the group for existing group members.
     *  
     *  If group is a self-reg-group, then it also remove the group template
     *  
     * @param conn
     * @param groupId
     * @throws Exception
     */
    private void doDelete(final Connection conn, Integer adminId, Integer groupId) throws Exception {
        new CmAdminDao().deleteGroup(conn, adminId, groupId);
    }
    
    
    
    /** Update group name identified by groupId
     * 
     * @param conn
     * @param groupId
     * @param groupName
     * @throws Exception
     */
    private void doUpdate(final Connection conn, Integer groupId, String groupName) throws Exception {
        new CmAdminDao().updateGroup(conn, groupId, groupName);
    }
    
    
    
    private void doUnregister(final Connection conn, Integer adminId,Integer groupId) throws Exception {
        CmStudentDao dao = new CmStudentDao();
        List<StudentModelI> cmList = new ArrayList<StudentModelI>();

        PreparedStatement ps=null;
        try {
            if(groupId == -1) {
                String sql = "select uid from HA_USER where is_active = 1 and admin_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1,adminId);
            }
            else {
                String sql = "select uid from HA_USER where is_active = 1 and admin_id = ? and group_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1,adminId);
                ps.setInt(2,groupId);
            }
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                StudentModel sm = new StudentModel();
                sm.setUid(rs.getInt("uid"));
                cmList.add(sm);
            }
            
            dao.unregisterStudents(conn, cmList);
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }
    
 
    /** Assign group information assigned to studentTemplate to all students in named group
     * 
     * @param conn
     * @param adminId
     * @param groupId
     * @param studentTemplate
     * @throws Exception
     */
    private void doGroupProgramAssignment(final Connection conn, Integer adminId, Integer groupId, StudentModel studentTemplate, Integer isSelfReg) throws Exception {
        PreparedStatement ps=null;
        try {
            if(groupId == -1) {
                String sql = "select uid from HA_USER where is_active = 1 and is_auto_create_template = 0 and admin_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1,adminId);
            }
            else if (isSelfReg < 1){
                String sql = "select uid from HA_USER where is_active = 1 and is_auto_create_template = 0 and admin_id = ? and group_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1,adminId);
                ps.setInt(2,groupId);
            }
            else {
                String sql = "select uid from HA_USER where is_active = 1 and admin_id = ? and group_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1,adminId);
                ps.setInt(2,groupId);            	
            }
            
            //String passPercent = studentTemplate.getPassPercent();
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                new CmStudentDao().assignProgramToStudent(conn, rs.getInt("uid"), 
                        studentTemplate.getProgram(), studentTemplate.getChapter(),
                        studentTemplate.getPassPercent());
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }        
    }
    
    
    /** 
     * Apply properties to each student in group
     * 
     * @param conn
     * @param adminId
     * @param showWorkRequired
     * @param disallowTutoring
     * @param passPercent
     * @throws Exception
     */
    private void doGroupPropertySet(final Connection conn,Integer adminId,Integer groupId,Boolean showWorkRequired,Boolean disallowTutoring,
    	Boolean limitGames, Boolean stopAtProgramEnd, Integer passPercent) throws Exception {
        PreparedStatement ps=null;
        try {
            if(groupId == -1) {
                String sql = "select uid from HA_USER where is_active = 1 and admin_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1,adminId);
            }
            else {
                String sql = "select uid from HA_USER where is_active = 1 and group_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1,groupId);
            }
            
            CmStudentDao dao = new CmStudentDao();
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                
                // update the basic information
                //StudentModelI sm = dao.getStudentModelBasic(conn,rs.getInt("uid"));
                //sm.setTutoringAvail(!disallowTutoring);
                //sm.setShowWorkRequired(showWorkRequired);
                dao.updateStudentSettings(conn, rs.getInt("uid"), showWorkRequired, !disallowTutoring, limitGames, stopAtProgramEnd, passPercent);
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }                
    }
}
