package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.GetCorrelatedTopicsPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.testset.ha.SolutionDao;

import java.sql.Connection;
import java.util.Arrays;
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
public class GetCorrelatedTopicsPrescriptionCommand implements ActionHandler<GetCorrelatedTopicsPrescriptionAction, CmList<PrescriptionSessionResponse>> {

    static Logger __logger = Logger.getLogger(GetPrescriptionCommand.class);
    
    @Override
    public CmList<PrescriptionSessionResponse> execute(final Connection conn, GetCorrelatedTopicsPrescriptionAction action) throws Exception {
    	
    	String pid = action.getPid();
    	
    	String basePid = pid.split("\\$")[0]; 
    	List<LessonModel> associatedLessons = SolutionDao.getInstance().getLessonsAssociatedForPid(conn, basePid);
    	
    	PrescriptionSessionResponse response = new PrescriptionSessionResponse();
    	response.setPrescriptionData(new PrescriptionData());
    	CmList<PrescriptionSessionResponse> prescriptions = new CmArrayList<PrescriptionSessionResponse>();
    	for(LessonModel lesson: associatedLessons) {
    		GetTopicPrescriptionAction getTopic = new GetTopicPrescriptionAction(lesson.getLessonFile());
    		
    		try {
    		    PrescriptionSessionResponse pres = new GetTopicPrescriptionCommand().execute(conn, getTopic);
    		    prescriptions.add(pres);
    		}
    		catch(Exception e) {
    		    __logger.warn("Error getting prescription for '" + getTopic + "'", e);
    		}
    	}
    	return prescriptions;
    }

	private CmList<PrescriptionSessionResponse> processAsSingleAssignedLesson(Connection conn, LessonModel lessonAssigned) throws Exception {
		PrescriptionSessionResponse value = new GetTopicPrescriptionCommand().execute(conn, new GetTopicPrescriptionAction(lessonAssigned.getLessonFile()));
		return new CmArrayList<PrescriptionSessionResponse>(Arrays.asList(value));
	}

	public Class<? extends Action<? extends Response>> getActionType() {
		return GetCorrelatedTopicsPrescriptionAction.class;
	}

   
}