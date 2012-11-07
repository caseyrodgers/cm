package hotmath.cm.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

public class Assignment_Test extends TestCase {
    
    public Assignment_Test(String name) {
        super(name);
    }

    public void testCreate() throws Exception {
        CmList<ProblemDto> pids = new CmArrayList<ProblemDto>();
        pids.add(new ProblemDto(0,"Lesson", "Label", "Pid", null, 0));
        
        List<Integer> uids = new ArrayList<Integer>();
        uids.add(new Integer(1));
        
        Assignment as = new Assignment(0,0, "Ass-Name-" + System.currentTimeMillis(),"Comments",new Date(),pids,uids,"New");
        
        int assKey = AssignmentDao.getInstance().saveAssignement(2,as);
        assertTrue(assKey > 0);
        Assignment assGet = AssignmentDao.getInstance().getAssignment(assKey);
        assertTrue(assGet.getUids().get(0).equals(as.getUids().get(0)));
        assertTrue(assGet.getPids().get(0).getPid().equals(as.getPids().get(0).getPid()));
    }
 
    public void testGetAssignmentGradebook() throws Exception {
    	CmList<StudentAssignment> list = AssignmentDao.getInstance().getAssignmentGradeBook(6);
    	
    	assertTrue(list.size() > 0);
    }

    public void testGetAssignmentForStudents() throws Exception {
    	List<Integer> uidList = new ArrayList<Integer>();
    	
    	uidList.add(23255);
    	uidList.add(27554);
    	
    	CmList<StudentAssignment> list = AssignmentDao.getInstance().getAssignmentForStudents(6, uidList);
    	
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
