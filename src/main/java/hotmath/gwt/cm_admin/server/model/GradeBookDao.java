package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_tools.client.model.GradeBookModel;
import hotmath.spring.SpringManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                        rs.getInt("count_entries"), rs.getInt("num_correct"), rs.getInt("percent_correct"),
                        rs.getInt("cp_id"));
            }
        });

        /**
         * identify unique homework assignments
         */
        int id = 0;
        Map<Integer,List<String>> assignmentMap = new HashMap<Integer,List<String>>();
        for(GradeBookModel m : list) {
        	String lessonName = m.getLessonName();
        	if (lessonName == null) continue;
        	Integer cpId = m.getCpId();
        	List<String> lessonList = assignmentMap.get(cpId);
        	if (lessonList == null) {
        		lessonList = new ArrayList<String>();
                assignmentMap.put(cpId, lessonList);
        	}
            if (lessonList.contains(lessonName) == false) {
            	lessonList.add(lessonName);
            }
        }
        
        /** create pivot table based on unique assignments
         * 
         */
        List<GradeBookModel> pivot = new ArrayList<GradeBookModel>(list.size());
        int lastUid = -1;
        int lastCpId = -1;
        GradeBookModel pivotModel = null;
        int colCount = 0;
        for(GradeBookModel m : list) {
            
            if(lastUid != m.getUid()) {
                // new student, start a new pivot row
                pivotModel = new GradeBookModel();
                pivotModel.set("userName", m.getUserName());
                pivotModel.set("uid", m.getUid());
                colCount = 0; // reset
                lastCpId = -1; // reset
                pivot.add(pivotModel);
                lastUid = m.getUid();
            }
            
            String lessonName = m.getLessonName();
            Integer cpId = m.getCpId();

            if (lessonName == null) {
            	if (lastCpId != cpId) {
            		if (lastCpId > 0) {
            			colCount += assignmentMap.get(lastCpId).size();
            			lastCpId = cpId;
            		}
            	}
            	/*
            	 * nothing completed for current lesson, init all to "N/A"
            	 */
            	for (int i=0; i < assignmentMap.get(cpId).size(); i++) {
                    pivotModel.set("Asg-" + (++colCount), "N/A");            		
            	}
            }

            else {
            	/*
            	 * student completed at least one lesson for current cpId
            	 */
            	if (lastCpId != cpId) {
            		if (lastCpId > 0) {
            			colCount += assignmentMap.get(lastCpId).size();
            			lastCpId = cpId;
            		}
            		int cCount = colCount;
                	for (int i=0; i < assignmentMap.get(cpId).size(); i++) {
                        pivotModel.set("Asg-" + (++cCount), "N/A");            		
                	}
                	lastCpId = cpId;
            	}
            	List<String> lessonList = assignmentMap.get(cpId);
            	int idx = 1;
            	for (String lesson : lessonList) {
            		if (lessonName.equals(lesson)) {
            			String percentCorrect = String.format("%d%s", m.getPercentCorrect(), "%");
            			String key = String.format("Asg-%d", colCount + idx);
            			pivotModel.set(key, percentCorrect);
            			__logger.debug(String.format("key: %s, percentCorrect: %s", key, percentCorrect));
            		}
            		idx++;
            	}
            	
            }
        }
        return pivot;
    }
}
