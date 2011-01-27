package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmHighlightsDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class HighlightsGetReportCommand implements ActionHandler< HighlightsGetReportAction, CmList<HighlightReportData>>{

    @Override
    public CmList<HighlightReportData> execute(Connection conn, HighlightsGetReportAction action) throws Exception {
        try {

            
            CmList<HighlightReportData> list=null;
            
            Date fromDate = action.getFrom();
            if(fromDate == null) {
                fromDate = new GregorianCalendar(2010,0,0).getTime();
            }
            Date toDate = action.getTo();
            if(toDate == null) {
                toDate = new GregorianCalendar(2050,0,0).getTime();
            }
            
            
            List<StudentModelExt> studentPool = new GetStudentGridPageCommand().getStudentPool(conn, action.getStudentGridPageAction());
            if(studentPool.size() == 0)
                return list;
            
            
            List<String> uids = new ArrayList<String>();
            for(StudentModelExt student: studentPool) {
                uids.add(Integer.toString(student.getUid()));
            }
            CmHighlightsDao dao = new CmHighlightsDao();
            switch(action.getType()) {
                case GREATEST_EFFORT:
                    list = dao.getReportGreatestEffort(conn, uids, fromDate, toDate);
                    break;
                    
                case LEAST_EFFORT:
                    list = dao.getReportLeastEffort(conn, uids, fromDate, toDate);
                    break;
                    
                case MOST_GAMES:
                    list = dao.getReportMostGames(conn, uids, fromDate, toDate);
                    break;             
                    
                case MOST_QUIZZES_PASSED:
                    list = dao.getReportQuizzesPassed(conn, uids, fromDate, toDate);
                    break;             
                    
                case AVERAGE_QUIZ_SCORES:
                    list = dao.getReportAvgQuizScores(conn, uids, fromDate, toDate);
                    break;             

                case FAILED_QUIZZES:
                    list = dao.getReportFailedQuizzes(conn, uids, fromDate, toDate);
                    break;   

                case FAILED_CURRENT_QUIZZES:
                    list = dao.getReportFailedCurrentQuizzes(conn, uids, fromDate, toDate);
                    break; 
                    
                case LOGINS_PER_WEEK:
                    list = dao.getReportLoginsPerWeek(conn, uids, fromDate, toDate);
                    break;
                    
                case COMPARE_PERFORMANCE:
                    list = dao.getReportComparePerformance(conn, action.getAdminId(), uids, fromDate, toDate);
                    break;
                    
                case GROUP_PERFORMANCE:
                    list = dao.getReportGroupProgress(conn, action.getAdminId(), uids, fromDate, toDate);
                    break;
                    
                    default:
                        throw new Exception("Uknown report type: " + action);
            }
            return list;
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private List<String> getUids(final Connection conn) throws Exception {
        ResultSet rs = conn.createStatement().executeQuery("Select uid from HA_USER where admin_id = 2");
        List<String> list = new ArrayList<String>();
        while(rs.next()) {
            list.add(rs.getString("uid"));
        }
        return list;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return  HighlightsGetReportAction.class;
    }
}
