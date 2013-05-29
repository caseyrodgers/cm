package hotmath.cm.assignment;

import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

public class Assignment_Test extends TestCase {
    
    public Assignment_Test(String name) {
        super(name);
    }

    public void testCreate() throws Exception {
        CmList<ProblemDto> pids = new CmArrayList<ProblemDto>();
        pids.add(new ProblemDto(0,0, new LessonModel("Lesson", "File"), "Label", "Pid",  0));
        Assignment as = new Assignment(2, 0,0, "Ass-Name-" + System.currentTimeMillis(),"Comments",new Date(),pids,"New", false, false,new Date(), true);
        int assKey = AssignmentDao.getInstance().saveAssignment(as);
        assertTrue(assKey > 0);
        Assignment assGet = AssignmentDao.getInstance().getAssignment(assKey);
        assertTrue(assGet.getPids().get(0).getPid().equals(as.getPids().get(0).getPid()));
        assertTrue(assGet.isAutoRelease());
    }
 
    public void testGetAssignmentGradebook() throws Exception {
    	CmList<StudentAssignment> list = AssignmentDao.getInstance().getAssignmentGradeBook(6);
    	
    	assertTrue(list.size() > 0);
    }


    public void testGetAssignmentGradeBook() throws Exception {
    	CmList<StudentAssignment> list = AssignmentDao.getInstance().getAssignmentGradeBook(9);
    	
    	assertTrue(list.size() > 0);
    }

    public void testGetAssignmentWorkForStudent() throws Exception {
    	List<StudentAssignment> list = AssignmentDao.getInstance().getAssignmentWorkForStudent(27554);
    	
    	assertTrue(list.size() > 0);
    }

}
