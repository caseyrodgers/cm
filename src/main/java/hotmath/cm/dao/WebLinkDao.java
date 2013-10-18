package hotmath.cm.dao;

import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.spring.SpringManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class WebLinkDao  extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(WebLinkDao.class);

    static private WebLinkDao __instance;

    static public WebLinkDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (WebLinkDao) SpringManager.getInstance().getBeanFactory().getBean(WebLinkDao.class.getName());
        }
        return __instance;
    }

    public List<WebLinkModel> getWebLinksFor(int adminId, int groupId, String topic) {
 
        Collection<? extends WebLinkModel> links = getAllWebLinksDefinedForAdmin(adminId, true);
        
        /** Check which weblink is active in this context
         * 
         */
        List<WebLinkModel> activeLinks = new ArrayList<WebLinkModel>();
        for(WebLinkModel l: links) {
            if(l.getAlwaysAvailable() && l.isAllGroups() || isWebLinkActive(l, topic, groupId) ) {
                activeLinks.add(l);
            }
        }
        return activeLinks;
    }

    private boolean isWebLinkActive(WebLinkModel l, String topic, int groupId) {
        boolean isActive=false;
        if(l.isAllGroups()) {
            isActive = true;
        }
        else if( isWebLinkActiveInGroup(l, groupId) ) {
            isActive = true;
        }
        
        if(isActive) {
            // made it through group check.
            if(!l.getAlwaysAvailable()) {
                
                if(!isWebLinkActiveInLesson(l, topic)) {
                    isActive = false;
                }
            }
        }
        
        return isActive;
    }

    private boolean isWebLinkActiveInLesson(WebLinkModel l, String topic) {
        for(LessonModel lm: l.getLinkTargets()) {
            if(lm.getLessonName().equals(topic)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWebLinkActiveInGroup(WebLinkModel link, int groupId) {
        for(GroupInfoModel g: link.getLinkGroups()) {
            if(g.equals(groupId)) {
                return true;
            }
        }

        return false;
    }
    
    public Collection<? extends WebLinkModel> getAllWebLinksDefinedForAdmin(int adminId, boolean readGroupsAndLessons) {
        String sql = "select * from CM_WEBLINK where admin_id = ? order by name";
        List<WebLinkModel> links = getJdbcTemplate().query(sql, new Object[] { adminId }, new RowMapper<WebLinkModel>() {
            @Override
            public WebLinkModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                WebLinkModel wlm = new WebLinkModel(rs.getInt("id"), rs.getInt("admin_id"), rs.getString("name"), rs.getString("url"));
                return wlm;
            }
        });    

        if(readGroupsAndLessons) {
            for(final WebLinkModel wl: links) {
                sql = "select wg.*, g.name as group_name " +
                      "from    CM_WEBLINK_GROUPS wg " +
                      "JOIN CM_GROUP g on g.id = wg.group_id " +
                      "where link_id = ? " +
                      " order by group_name ";
                
                List<GroupInfoModel> groups = getJdbcTemplate().query(sql, new Object[] { wl.getLinkId() }, new RowMapper<GroupInfoModel>() {
                    @Override
                    public GroupInfoModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new GroupInfoModel(wl.getAdminId(), rs.getInt("group_id"),rs.getString("group_name"),0,true,false);
                    }
                });
                wl.getLinkGroups().clear();
                wl.getLinkGroups().addAll(groups);
    
                sql = "select * from CM_WEBLINK_LESSONS where link_id = ? order by id";
                List<LessonModel> lessons = getJdbcTemplate().query(sql, new Object[] { wl.getLinkId() }, new RowMapper<LessonModel>() {
                    @Override
                    public LessonModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new LessonModel(rs.getString("lesson_name"), rs.getString("lesson_file"), rs.getString("lesson_subject"));
                    }
                });
                wl.getLinkTargets().clear();
                wl.getLinkTargets().addAll(lessons);
            }
        }
        return links;
    }
}
