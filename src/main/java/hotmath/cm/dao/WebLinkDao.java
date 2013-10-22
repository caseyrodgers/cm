package hotmath.cm.dao;

import hotmath.cm.login.ClientEnvironment;
import hotmath.cm.util.UserAgentDetect;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.AvailableOn;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.spring.SpringManager;
import hotmath.testset.ha.HaUserDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class WebLinkDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(WebLinkDao.class);

    static private WebLinkDao __instance;

    static public WebLinkDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (WebLinkDao) SpringManager.getInstance().getBeanFactory().getBean(WebLinkDao.class.getName());
        }
        return __instance;
    }

    public List<WebLinkModel> getWebLinksFor(int userId, int adminId, int groupId, String topic) throws Exception {

        Collection<? extends WebLinkModel> links = getAllWebLinksDefinedForAdmin(adminId, true);

        ClientEnvironment clientEnvironment = HaUserDao.getInstance().getLatestClientEnvironment(userId);
        UserAgentDetect userAgent = new UserAgentDetect(clientEnvironment.getUserAgent(), "");

        boolean isMobile = userAgent.detectTierTablet() || userAgent.detectMobileQuick();

        /**
         * Check which weblink is active in this context
         * 
         * Only add weblink that is compatible with current 
         * user's environment (mobile, desktop)
         */
        List<WebLinkModel> activeLinks = new ArrayList<WebLinkModel>();
        for (WebLinkModel l : links) {
            if (l.isAllLessons() && l.isAllGroups() || isWebLinkActive(l, topic, groupId)) {
                AvailableOn device = l.getAvailableWhen();
                if (device != AvailableOn.DESKTOP_AND_MOBILE)
                    if (isMobile && device != AvailableOn.MOBILE_ONLY) {
                        continue; // skip it
                    } else if (!isMobile && device != AvailableOn.DESKTOP_ONLY) {
                        continue; // skip it
                    }
            }
            activeLinks.add(l);
        }
        return activeLinks;
    }

    private boolean isWebLinkActive(WebLinkModel l, String topic, int groupId) {
        boolean isStillActive = false;

        // check groups first
        if (l.isAllGroups()) {
            isStillActive = true;
        } else if (isWebLinkActiveInGroup(l, groupId)) {
            isStillActive = true;
        }

        // check lesson if still acive
        if (isStillActive) {
            // made it through group check.
            if (!l.isAllLessons()) {
                if (!isWebLinkActiveInLesson(l, topic)) {
                    isStillActive = false;
                }
            }
        }

        return isStillActive;
    }

    private boolean isWebLinkActiveInLesson(WebLinkModel l, String topic) {
        for (LessonModel lm : l.getLinkTargets()) {

            // is a subject match only
            if (lm.getLessonFile() == null) {
                if (isWebLinkActiveInLessonSubject(lm.getSubject(), topic)) {
                    return true;
                }
            } else if (lm.getLessonName().equals(topic)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is topic in subject 'subject'.
     * 
     * @param subject
     * @param topic
     * @return
     */
    private boolean isWebLinkActiveInLessonSubject(String subject, String topic) {
        String sql = "select distinct s.name as subject " + " from   HA_PROGRAM_LESSONS_static ls " + " JOIN SUBJECT s on s.subj_id = ls.subject "
                + " where lesson = ? " + " order by grade_level ";
        List<String> subjects = getJdbcTemplate().query(sql, new Object[] { topic }, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("subject");
            }
        });
        if (subjects.size() > 0) {
            for (String sub : subjects) {
                if (sub != null && sub.equalsIgnoreCase(subject)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isWebLinkActiveInGroup(WebLinkModel link, int groupId) {
        for (GroupInfoModel g : link.getLinkGroups()) {
            if (g.equals(groupId)) {
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

                int available = rs.getInt("works_on_device");
                AvailableOn availableOn = AvailableOn.values()[available];

                WebLinkModel wlm = new WebLinkModel(rs.getInt("id"), rs.getInt("admin_id"), rs.getString("name"), rs.getString("url"),
                        rs.getString("comments"), availableOn);
                return wlm;
            }
        });

        if (readGroupsAndLessons) {
            for (final WebLinkModel wl : links) {
                sql = "select wg.*, g.name as group_name " + "from    CM_WEBLINK_GROUPS wg " + "JOIN CM_GROUP g on g.id = wg.group_id " + "where link_id = ? "
                        + " order by group_name ";

                List<GroupInfoModel> groups = getJdbcTemplate().query(sql, new Object[] { wl.getLinkId() }, new RowMapper<GroupInfoModel>() {
                    @Override
                    public GroupInfoModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new GroupInfoModel(wl.getAdminId(), rs.getInt("group_id"), rs.getString("group_name"), 0, true, false);
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
