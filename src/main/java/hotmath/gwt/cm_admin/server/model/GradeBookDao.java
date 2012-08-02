package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.QueryHelper;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.AssignmentModel;
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
                	GradeBookModel gbMdl = new GradeBookModel(rs.getInt("uid"),rs.getString("user_name"));
                	CmList<AssignmentModel> asgList = new CmArrayList<AssignmentModel>();

                	int numAnswered = rs.getInt("num_answered");
                	int numCorrect = rs.getInt("num_correct");
                	int countEntries = rs.getInt("count_entries");
                	
                	String percentCorrect = String.format("%d%s", rs.getInt("percent_correct"), "%");
                	AssignmentModel asgMdl = new AssignmentModel( rs.getString("lesson_name"), countEntries,
                			numCorrect, percentCorrect, numAnswered, rs.getInt("cp_id"),
                			rs.getString("cp_name"));
                	asgList.add(asgMdl);
                	gbMdl.setAssignmentList(asgList);
                    return gbMdl;
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
        Map<Integer,List<AssignmentModel>> assignmentMap = new HashMap<Integer,List<AssignmentModel>>();
        for(GradeBookModel m : list) {
        	AssignmentModel asgMdl = m.getAssignmentList().get(0);
        	String lessonName = asgMdl.getLessonName();
        	if (lessonName == null) continue;
        	Integer cpId = asgMdl.getCpId();
        	List<AssignmentModel> assignmentList = assignmentMap.get(cpId);
        	if (assignmentList == null) {
        		assignmentList = new ArrayList<AssignmentModel>();
                assignmentMap.put(cpId, assignmentList);
        	}
        	boolean isInList = false;
        	for (AssignmentModel mdl : assignmentList) {
        		if (mdl.getCpId().equals(asgMdl.getCpId()) == false ||
        		    mdl.getLessonName().equals(asgMdl.getLessonName()) == false) continue;
        		isInList = true;
        		break;
        	}
            //if (assignmentList.contains(asgMdl) == false) {
            if (isInList == false) {
            	assignmentList.add(asgMdl);
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
        List<AssignmentModel> pivotList = null;
        int colCount = 0;
        for(GradeBookModel gbMdl : list) {
            
            if(lastUid != gbMdl.getUid()) {
                // new student, start a new pivot row
                pivotModel = new GradeBookModel();
                pivotModel.set("userName", gbMdl.getUserName());
                pivotModel.set("uid", gbMdl.getUid());
                pivotModel.setAssignmentList(new CmArrayList<AssignmentModel>());
                pivotList = pivotModel.getAssignmentList();
                colCount = 0; // reset
                lastCpId = -1; // reset
                pivot.add(pivotModel);
                lastUid = gbMdl.getUid();
            }

            AssignmentModel asgMdl = gbMdl.getAssignmentList().get(0);
            String lessonName = asgMdl.getLessonName();
            Integer cpId = asgMdl.getCpId();

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
            		assignmentMap.put(cpId, new ArrayList<AssignmentModel>());
            	}
            	int cCount = colCount;
            	for (AssignmentModel mdl : assignmentMap.get(cpId)) {
            		AssignmentModel naMdl = new AssignmentModel(mdl.getLessonName(), mdl.getCountEntries(), 0, "N/A", 0,
            				mdl.getCpId(), mdl.getCpName());
            		String name = String.format("Asg-%d", ++cCount);
            		naMdl.setName(name);
            		pivotList.add(naMdl);
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
                	if (assignmentMap.get(cpId) == null) {
                		assignmentMap.put(cpId, new ArrayList<AssignmentModel>());
                	}
                	int cCount = colCount;
                	for (AssignmentModel mdl : assignmentMap.get(cpId)) {
                		AssignmentModel naMdl = new AssignmentModel(mdl.getLessonName(), mdl.getCountEntries(), 0, "N/A", 0,
                			    mdl.getCpId(), mdl.getCpName());
                		String name = String.format("Asg-%d", ++cCount);
                		naMdl.setName(name);
                		pivotList.add(naMdl);
                	}
                	lastCpId = cpId;
            	}
            	List<AssignmentModel> asgList = assignmentMap.get(cpId);
            	int cCount = colCount;
            	for (AssignmentModel asg : asgList) {
            		++cCount;
            		if (asgMdl.getCountEntries() > 0 && lessonName.equals(asg.getLessonName())) {
                		AssignmentModel mdl = new AssignmentModel(asg.getLessonName(), asg.getCountEntries(),
                				asgMdl.getNumCorrect(), asgMdl.getPercentCorrect(), asgMdl.getNumAnswered(),
                				asg.getCpId(), asgMdl.getCpName());
                		String name = String.format("Asg-%d", cCount);
                		mdl.setName(name);
                		int index = pivotList.indexOf(mdl);
                		//__logger.debug("cCount: " + cCount + ", index: " + index);
                		pivotList.remove(index);
                		pivotList.add(index, mdl);
            		}
            	}
            	
            }
        }
        /**
         * transfer last one
         */
        //xferMapToModel(pivotModel, pivotList);

        return pivot;
    }
}
