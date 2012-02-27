package hotmath.gwt.shared.server.service.command;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionCustomMobile;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;



/**
 * Read an existing prescription based on a test run
 * 
 * and return data that represents a single program prescription lesson.
 * 
 * @author casey
 * 
 */
public class GetTopicPrescriptionCommand implements ActionHandler<GetTopicPrescriptionAction, PrescriptionSessionResponse> {

    static Logger __logger = Logger.getLogger(GetPrescriptionCommand.class);

    @Override
    public PrescriptionSessionResponse execute(final Connection conn, GetTopicPrescriptionAction action) throws Exception {
        
        int userSharedUser = 24412;

        HaTest custTest = HaTestDao.getInstance().createTest(userSharedUser,HaTestDefDao.getInstance().getTestDef(CmProgram.CUSTOM_PROGRAM.getDefId()), HaTestDao.EMPTY_TEST);
        StudentUserProgramModel userProgram = new StudentUserProgramModel();
        custTest.setProgramInfo(userProgram);
        HaTestRun testRun = HaTestDao.getInstance().createTestRun(conn, userSharedUser, custTest.getTestId(), 10, 0, 0);
        testRun.setHaTest(custTest);
        
        List<CustomLessonModel> lessonModels = new ArrayList<CustomLessonModel>();
        lessonModels.add(new CustomLessonModel(getTopicLessonTitle(conn, action.getTopicFile()), action.getTopicFile(), "General"));
        
        AssessmentPrescription prescription = new AssessmentPrescriptionCustomMobile(conn, testRun, lessonModels);
        return GetPrescriptionCommand.createPrescriptionResponse(conn, prescription, 0);
    }

    
    private String getTopicLessonTitle(final Connection conn, String file) throws Exception {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("select lesson from HA_PROGRAM_LESSONS where file = ? limit 1");
            ps.setString(1, file);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getString(1);
            }
            return null;
        }
        finally {
            SqlUtilities.releaseResources(null, ps,null);
        }
    }
    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GetTopicPrescriptionAction.class;
    }
    
    static public void main(String as[]) {
        
        try {
            System.out.println(ActionDispatcher.getInstance().execute(new GetTopicPrescriptionAction("topics/number-line.html")));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
}