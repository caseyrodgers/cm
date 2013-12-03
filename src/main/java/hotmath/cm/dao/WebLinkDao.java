package hotmath.cm.dao;

import hotmath.cm.login.ClientEnvironment;
import hotmath.cm.util.UserAgentDetect;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.model.SubjectType;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.AvailableOn;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.LinkViewer;
import hotmath.gwt.cm_rpc.client.model.WebLinkType;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.spring.SpringManager;
import hotmath.testset.ha.HaUserDao;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

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
         * Only add weblink that is compatible with current user's environment
         * (mobile, desktop)
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
                
                if(!l.isOffline()) {
                    activeLinks.add(l);
                }
            }
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
        String sql = "select * from CM_WEBLINK where admin_id = ? and is_public = 0 order by name";
        if (adminId == 0) {
            sql = "select * from CM_WEBLINK where is_public = 1 or (admin_id = ?) order by name"; // admin_id
                                                                                                  // is
                                                                                                  // placeholder
        }
        List<WebLinkModel> links = getJdbcTemplate().query(sql, new Object[] { adminId }, new RowMapper<WebLinkModel>() {
            @Override
            public WebLinkModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                return extractWebLinkModel(rs);
            }
        });

        if (readGroupsAndLessons) {
            for (final WebLinkModel wl : links) {
                sql = "select wg.*, g.admin_id, g.name as group_name " + "from    CM_WEBLINK_GROUPS wg " + "JOIN CM_GROUP g on g.id = wg.group_id "
                        + "where link_id = ? " + " order by group_name ";

                List<GroupInfoModel> groups = getJdbcTemplate().query(sql, new Object[] { wl.getLinkId() }, new RowMapper<GroupInfoModel>() {
                    @Override
                    public GroupInfoModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return extractWebLinkGroup(rs);
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

                wl.setSubjectType(lookupLinkSubject(wl));

            }
        }
        return links;
    }

    protected GroupInfoModel extractWebLinkGroup(ResultSet rs) throws SQLException {
        return new GroupInfoModel(rs.getInt("admin_id"), rs.getInt("group_id"), rs.getString("group_name"), 0, true, false);
    }

    protected WebLinkModel extractWebLinkModel(ResultSet rs) throws SQLException {
        int available = rs.getInt("platform");
        AvailableOn availableOn = AvailableOn.values()[available];
        boolean publicLink = rs.getInt("is_public") != 0 ? true : false;
        WebLinkType linkType = WebLinkType.values()[rs.getInt("link_type")];
        LinkViewer linkViewer = LinkViewer.values()[rs.getInt("link_viewer")];

        boolean isOffline = rs.getInt("offline") != 0 ? true : false;
        WebLinkModel wlm = new WebLinkModel(rs.getInt("id"), rs.getInt("admin_id"), rs.getString("name"), rs.getString("url"), rs.getString("comments"),
                availableOn, publicLink, linkType, null, linkViewer, isOffline);
        return wlm;
    }

    /**
     * Find the subject associated with this link.
     * 
     * If multiple links then return the one with the lowest grade level.
     * 
     * @param wlm
     * @return
     */
    private SubjectType lookupLinkSubject(WebLinkModel wlm) {
        SubjectType type = null;
        for (LessonModel lesson : wlm.getLinkTargets()) {
            SubjectType t = SubjectType.lookup(lesson.getSubject());
            if (type == null || t.getLevel() < type.getLevel()) {
                type = t;
            }
        }
        return type;
    }

    public int importWebLink(int adminId, WebLinkModel webLink) throws Exception {

        /**
         * if (adminId == webLink.getAdminId()) { throw new
         * CmException("This weblink is already in your private links"); }
         */
        webLink.setLinkId(0); // make sure it is new
        webLink.setAdminId(adminId);
        webLink.getLinkGroups().clear();

        return addWebLink(webLink);
    }

    public void deleteWebLink(final WebLinkModel link) throws Exception {
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                String sql = "delete from CM_WEBLINK_LESSONS where link_id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, link.getAdminId());
                return ps;
            }
        });

        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                String sql = "delete from CM_WEBLINK_GROUPS where link_id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, link.getLinkId());
                return ps;
            }
        });

        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                String sql = "delete from CM_WEBLINK where id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, link.getLinkId());
                return ps;
            }
        });
    }

    /**
     * Add new weblink and return new link_id
     * 
     * @param link
     * @return
     * @throws Exception
     */
    public int addWebLink(final WebLinkModel link) throws Exception {

        validateWebLink(link.getUrl());
        
        link.setUrl(performLinkConversion(link.getUrl()));

        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                String sql = "insert into CM_WEBLINK(admin_id, name, url, comments, platform, is_public, link_type, link_viewer, offline, date_created)values(?,?,?,?,?,?,?,?,?,now())";
                PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, link.getAdminId());
                ps.setString(2, link.getName());
                ps.setString(3, link.getUrl());
                ps.setString(4, link.getComments());
                ps.setInt(5, link.getAvailableWhen().ordinal());
                ps.setInt(6, link.isPublicLink() ? 1 : 0);

                int typeOrdinal = link.getLinkType() != null ? link.getLinkType().ordinal() : 0;
                ps.setInt(7, typeOrdinal);

                ps.setInt(8, link.getLinkViewer().ordinal());

                ps.setInt(9, link.isOffline() ? 1 : 0);

                return ps;
            }
        }, keyHolder);
        final int webLinkId = keyHolder.getKey().intValue();

        if (!link.isAllLessons()) {

            String sql = "insert into CM_WEBLINK_LESSONS(link_id, lesson_name, lesson_file, lesson_subject)values(?,?,?,?)";
            getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    LessonModel lesson = link.getLinkTargets().get(i);
                    ps.setInt(1, webLinkId);
                    ps.setString(2, lesson.getLessonName());
                    ps.setString(3, lesson.getLessonFile());
                    ps.setString(4, lesson.getSubject());
                }

                @Override
                public int getBatchSize() {
                    return link.getLinkTargets().size();
                }
            });
        }

        if (!link.isAllGroups()) {

            String sql = "insert into CM_WEBLINK_GROUPS(link_id, group_id)values(?,?)";
            getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    GroupInfoModel group = link.getLinkGroups().get(0);
                    ps.setInt(1, webLinkId);
                    ps.setInt(2, group.getId());
                }

                @Override
                public int getBatchSize() {
                    return link.getLinkGroups().size();
                }
            });
        }

        return webLinkId;
    }


    LinkConvert linkConversions[] = {new LinkConvertImplKahn(), new LinkConvertImplNoHTTP()};
    
    /** perform any URL conversion by checking for known
     *  patterns .. then convert to best link. 
     *  
     *  For example, convert high level kahn videos to 
     *  internal embedded video urls
     *  
     */
    public String performLinkConversion(String url) throws Exception {
        //String conversions = new SbFile(CatchupMathProperties.getInstance().getCatchupRuntime() + "/weblink_conversion.txt").getFileContents().toString("\n");
        for(LinkConvert lc: linkConversions) {
            String convertedUrl = lc.doConversion(url);
            if(convertedUrl != null) {
                validateWebLink(convertedUrl);
                return convertedUrl;
            }
        }
        return url;
    }

    private void validateWebLink(String urlString) throws Exception {
        try {
            URL u = new URL(urlString);
            HttpURLConnection huc = (HttpURLConnection) u.openConnection();
            huc.setRequestMethod("GET");
            huc.connect();
            int rc = huc.getResponseCode();
        } catch (Exception e) {
            throw new CmException("Link URL does not exist: " + urlString);
        }
    }

    /**
     * Looks up the weblink and returns it or returns null if not found
     * 
     * @param linkId
     * @return
     */
    public WebLinkModel getWebLink(int linkId) {
        String sql = "select * from CM_WEBLINK where id = ?";
        List<WebLinkModel> links = getJdbcTemplate().query(sql, new Object[] { linkId }, new RowMapper<WebLinkModel>() {
            @Override
            public WebLinkModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                return extractWebLinkModel(rs);
            }
        });
        if (links.size() > 0) {
            assert (links.size() < 2);
            return links.get(0);
        } else {
            return null;
        }
    }
}

interface LinkConvert {
    /** return converted string if converted, otherwise return null
     * 
     * @param url
     * @return
     */
    String doConversion(String url);
}
