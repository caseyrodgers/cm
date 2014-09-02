package hotmath.cm.assignment;

import hotmath.gwt.cm_rpc_assignments.client.model.AssignmentRealTimeStats;
import hotmath.gwt.cm_rpc_assignments.client.model.AssignmentRealTimeStatsUsers;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentModel;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetAssignmentRealTimeStatsUsersAction;
import hotmath.gwt.shared.server.service.command.GetAssignmentRealTimeStatsUsersCommand;
import hotmath.testset.ha.SolutionDao;

import java.util.List;

import junit.framework.TestCase;

public class AssignmentDao_Test extends TestCase {
    
    int ASSIGN_KEY=4415;
    
    public AssignmentDao_Test(String name) {
        super(name);
    }
    
    
    public void testGetPidAsses() throws Exception {
    	List<AssignmentModel> result = AssignmentDao.getInstance().getAssignmentsWithPid("cmextrasalg2_1_5_1_28_1");
    	assertTrue(result != null);
    }
    public void testGetStats1() throws Exception {
        AssignmentRealTimeStats stats = AssignmentDao.getInstance().getAssignmentStats(ASSIGN_KEY);
        assert(stats.getPidStats().size() > 0);
    }
    
    public void testGet1() throws Exception {
        GetAssignmentRealTimeStatsUsersAction action = new GetAssignmentRealTimeStatsUsersAction(4415, "cahseehm2_CourseTest_1_PracticeTest_10_1");
        AssignmentRealTimeStatsUsers userStat = new GetAssignmentRealTimeStatsUsersCommand().execute(null,action);
        assertTrue(userStat != null);
    }



    public void testGetStats() throws Exception {
        AssignmentRealTimeStats stats = AssignmentDao.getInstance().getAssignmentStats(ASSIGN_KEY);
        assert(stats.getPidStats().size() > 0);
    }

    public void testGetStudents() throws Exception {
        List<StudentDto> students = AssignmentDao.getInstance().getStudentsInAssignment(ASSIGN_KEY);
        assert(students.size() > 0);
    }
    
    public void testOne() throws Exception {
        String html1 = "<div id='problem_statement'><div id='hm_flash_widget' class='not_used'></div></div>";
        String html2 = "<div id='problem_statement'><div id='hm_flash_widget' class='used'></div></div>";
        assertTrue(SolutionDao.determineProblemType(html1) == ProblemType.WHITEBOARD);
        assertTrue(SolutionDao.determineProblemType(html2) == ProblemType.WHITEBOARD);
    }

}
