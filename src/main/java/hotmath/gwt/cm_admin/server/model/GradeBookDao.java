package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.QueryHelper;
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

    public List<GradeBookModel> getGradeBookData(int adminId, List<Integer> uidList, String[] dates) throws Exception {

        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_GRADE_BOOK_DATA");
        sql = QueryHelper.createInListSQL(sql, uidList);
        List<GradeBookModel> list = new ArrayList<GradeBookModel>(1);
        try {
            list = getJdbcTemplate().query(sql, new Object[] { dates[0], dates[1] }, new RowMapper<GradeBookModel>() {
                @Override
                public GradeBookModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new GradeBookModel(rs.getInt("uid"),rs.getString("user_name"), rs.getString("lesson_name"), 
                            rs.getInt("count_entries"), rs.getInt("num_correct"), rs.getInt("percent_correct"),
                            rs.getInt("cp_id"));
                }
            });
        }
        catch(Exception e) {
            __logger.error(String.format("Exception: adminId: %d, uidList: %s", adminId, uidList), e);
            throw e;
        }

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
        
        /**
         * If assignmentMap is empty there is nothing else to do,
         * just return empty list
         */
        if (assignmentMap.size() < 1) return pivot;
        
        int lastUid = -1;
        int lastCpId = -1;
        GradeBookModel pivotModel = null;
        Map<String, String> pivotMap = null;;
        int colCount = 0;
        for(GradeBookModel m : list) {
            
            if(lastUid != m.getUid()) {
                // new student, start a new pivot row
            	xferMapToModel(pivotModel, pivotMap);
                pivotModel = new GradeBookModel();
                pivotModel.set("userName", m.getUserName());
                pivotModel.set("uid", m.getUid());
                pivotMap = new HashMap<String, String>();
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
            	 * nothing completed for current lesson, init to "N/A"
            	 * if assigned to any student in current UID list
            	 */
            	if (assignmentMap.get(cpId) == null) {
            		assignmentMap.put(cpId, new ArrayList<String>());
            	}
            	for (int i=0; i < assignmentMap.get(cpId).size(); i++) {
                    pivotMap.put("Asg-" + (++colCount), "N/A");            		
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
                	if (assignmentMap.get(cpId) == null) {
                		assignmentMap.put(cpId, new ArrayList<String>());
                	}
                	for (int i=0; i < assignmentMap.get(cpId).size(); i++) {
                        pivotMap.put("Asg-" + (++cCount), "N/A");
                	}
                	lastCpId = cpId;
            	}
            	List<String> lessonList = assignmentMap.get(cpId);
            	int idx = 1;
            	for (String lesson : lessonList) {
            		if (lessonName.equals(lesson)) {
            			String percentCorrect = String.format("%d%s", m.getPercentCorrect(), "%");
            			String key = String.format("Asg-%d", colCount + idx);
            			pivotMap.put(key, percentCorrect);
            		}
            		idx++;
            	}
            	
            }
        }
        /**
         * transfer last one
         */
        xferMapToModel(pivotModel, pivotMap);

        return pivot;
    }

	private void xferMapToModel(GradeBookModel pivotModel,
			Map<String, String> pivotMap) {
		if (pivotMap != null) {
		    for (int i=1; i <= pivotMap.size(); i++) {
		    	String key = "Asg-" + i;
			    pivotModel.set(key, pivotMap.get(key));
    			__logger.debug(String.format("Model Set: uid: %d, key: %s, percentCorrect: %s", pivotModel.getUid(), key, pivotMap.get(key)));
		    }
		}
	}
}
