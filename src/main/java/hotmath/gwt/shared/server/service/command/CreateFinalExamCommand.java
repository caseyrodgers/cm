package hotmath.gwt.shared.server.service.command;

import hotmath.cm.exam.ExamDao;
import hotmath.cm.exam.FinalExam;
import hotmath.cm.exam.FinalExam.QuizSize;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.CreateFinalExamAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.CmProgram;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

public class CreateFinalExamCommand implements ActionHandler<CreateFinalExamAction, Assignment> {


    @Override
    public Assignment execute(Connection conn, CreateFinalExamAction action) throws Exception {
        
        String type = action.getProgramType();
        CmProgram program = null;
        if(type.contains("Pre-Alg")) {
            program = CmProgram.PREALG_PROF;
        }
        else if(type.contains("Algebra 1")) {
            program = CmProgram.ALG1_PROF;
        }
        else if(type.contains("Essentials")) {
            program = CmProgram.ESSENTIALS;
        }
        else if(type.contains("Algebra 2")) {
            program = CmProgram.ALG2_PROF;
        }
        else if(type.contains("Geometry")) {
            program = CmProgram.GEOM_PROF;
        }
        
        else if(type.contains("Grad Prep")) {
            program = CmProgram.NATIONAL;
        }
        
        
        QuizSize quizSize = null;
        switch(action.getNumberProblems()) {
        
        case 15:
            quizSize = QuizSize.FIFTEEN;
            break;

        case 30:
            quizSize = QuizSize.THIRTY;
            break;

        case 45:
            quizSize = QuizSize.FOURTY_FIVE;
            break;

        case 60:
            quizSize = QuizSize.SIXTY;
            break;

        
        }
        
        FinalExam finalExam = new FinalExam(program, quizSize);
        
        Assignment assCreated = createNewAssignmentForFinalExam(action.getProgramType(), action.getAdminId(),action.getGroup().getGroupId(), finalExam);
        
        return assCreated;
    }

    private Assignment createNewAssignmentForFinalExam(String name, int adminId, int groupId, FinalExam finalExam) throws Exception {
        
        Assignment ass = new Assignment();
        ass.setComments(name + " Course Test (" + new Date() + ")");
        ass.setAssignmentName(name);
        ass.setAdminId(adminId);
        ass.setGroupId(groupId);
        ass.setStatus("Draft");
        
        CmList<ProblemDto> pids = new CmArrayList<ProblemDto>();
        
        int ordinal=0;
        for(String pid: finalExam.getPids()) {
            
            List<LessonModel> lessons = ExamDao.getInstance().getLessonsForProblem(pid);
            
            
            String label = "Problem " +  (++ordinal);
            
            LessonModel lesson = lessons.size() > 0?lessons.get(0):new LessonModel("","");
            ProblemDto pd = new ProblemDto(ordinal, ordinal, lesson, label, pid, 0);
            pd.setProblemType(ProblemType.MULTI_CHOICE);
            pids.add(pd);
        }
        ass.setPids(pids);
        
        ass.setDueDate(new Date());
        //AssignmentDao.getInstance().saveAssignment(ass);
        
        //ass.getPids().clear(); // send just define of assignment, pids will be read when needed
        
        return ass;
        
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CreateFinalExamAction.class;
    }

}
