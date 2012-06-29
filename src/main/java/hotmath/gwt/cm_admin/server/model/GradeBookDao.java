package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_tools.client.model.GradeBookModel;
import hotmath.spring.SpringManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * Provide DAO access for GradeBook data.
 * 
 * @author casey
 * 
 */
public class GradeBookDao extends SimpleJdbcDaoSupport {

    private static final Logger __logger = Logger.getLogger(GradeBookDao.class);

    static private GradeBookDao __instance;

    static public GradeBookDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (GradeBookDao) SpringManager.getInstance().getBeanFactory()
                    .getBean(GradeBookDao.class.getName());
        }
        return __instance;
    }

    private GradeBookDao() {
    }

    public List<GradeBookModel> getGradeBookData(int aid) throws Exception {

        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_GRADE_BOOK_DATA");
        List<GradeBookModel> list = getJdbcTemplate().query(sql, new Object[] { aid }, new RowMapper<GradeBookModel>() {
            @Override
            public GradeBookModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new GradeBookModel(rs.getInt("uid"),rs.getString("user_name"), rs.getString("lesson_name"), 
                        rs.getInt("count_entries"));
            }
        });


        
        
        /** create pivot table based on lesson names
         * 
         */
        List<GradeBookModel> pivot = new ArrayList<GradeBookModel>(list.size());
        int lastUid=-1;
        GradeBookModel pivotModel = null;
        int colCount=1;
        for(GradeBookModel m: list) {
            
            if(lastUid != m.getUid()) {
                // new student, start a new pivot row
                pivotModel = new GradeBookModel();
                pivotModel.set("userName", m.getUserName());
                pivotModel.set("uid", m.getUid());
                colCount=1; // reset
                pivot.add(pivotModel);
                lastUid = m.getUid();
            }
            
            for(String n: m.getPropertyNames()) {
                if(n.equals("userName") || n.equals("uid") || n.equals("countEntries")) {
                    continue;
                }
                
                pivotModel.set("Asg-" + colCount++, m.get("countEntries"));
            }
        }
        return pivot;
    }
}
