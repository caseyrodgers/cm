package hotmath.gwt.shared.server.service.command;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.cm.program.CmProgramFlow;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GetViewedInmhItemsAction;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.inmh.INeedMoreHelpResourceType;
import hotmath.testset.ha.HaTestRun;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;



/** Read an existing prescription based on a test run
 * 
 *  and return data that represents a single program
 *  prescription lesson.
 *  
 * @author casey
 *
 */
public class GetPrescriptionCommand implements ActionHandler<GetPrescriptionAction, PrescriptionSessionResponse> {

    static Logger __logger = Logger.getLogger(GetPrescriptionCommand.class);

    @Override
    public PrescriptionSessionResponse execute(final Connection conn, GetPrescriptionAction action) throws Exception {

        __logger.debug("getting prescription: " + action);
        int runId = action.getRunId();
        int sessionNumber = action.getSessionNumber();
        try {
            
            AssessmentPrescription prescription = AssessmentPrescriptionManager.getInstance().getPrescription(conn, runId);
            
            __logger.debug("verifing prescription: " + action);
            CmStudentDao.getInstance().verifyActiveProgram(conn, prescription.getTest().getTestId());

            int totalSessions = prescription.getSessions().size();
            if (totalSessions == 0) {
                /** no prescription created (no missed answers?)
                 * 
                 * This might be a bug in that an empty prescription
                 * is associated with a runId. 
                 * 
                 * return a default response
                 */
                PrescriptionSessionResponse resp = new PrescriptionSessionResponse();
                resp.setCorrectPercent(100);  /** why does this need to 100? */
                return resp;
            }
            // which session
            if (sessionNumber > (totalSessions - 1)) {
                __logger.warn(String.format("WARNING: session request for %d is outside bounds of prescription: %d, %d",
                		runId, sessionNumber, totalSessions));
                sessionNumber = 0;
            }
            
            CmProgramFlow cmProgram = new CmProgramFlow(conn, prescription.getTest().getUser().getUid());
            

            AssessmentPrescriptionSession session = prescription.getSessions().get(sessionNumber);
            PrescriptionData presData = new PrescriptionData();
            
            
            /** We now take the prescription and extract and massage
             *  it's data into a PrescriptionSessionResponse object.
             */
            List<AssessmentPrescription.SessionData> practiceProblems = session.getSessionDataFor(session.getTopic());
            PrescriptionSessionDataResource problemsResource = new PrescriptionSessionDataResource();
            problemsResource.setType("practice");
            
            __logger.debug("assigning problems to prescription: " + action);
            
            /** label either as Problems (RPP) or Activities (RPA)
             * 
             *  All RPs will be with RPP or RPA, never both. 
             */
            boolean isActivity=practiceProblems.get(0).getWidgetArgs()!=null;
            String title = "Required Practice " + (isActivity?"Activities":"Problems");
            problemsResource.setLabel(title);
            int cnt = 1;
            for (AssessmentPrescription.SessionData sdata : practiceProblems) {
                InmhItemData id = new InmhItemData();
                String type = isActivity?"Activity ":"Problem ";
                id.setTitle(type + cnt++);
                id.setFile(sdata.getPid());
                id.setType("practice");
                id.setWidgetJsonArgs(sdata.getWidgetArgs());
                
                problemsResource.getItems().add(id);
            }

            PrescriptionSessionDataResource lessonResource = new PrescriptionSessionDataResource();
            lessonResource.setType("review");
            lessonResource.setLabel("Review Lesson");
            InmhItemData lessonId = new InmhItemData();

            /** Get the lesson for this session
             * (NOTE: this is using the INMH code in HM)
             */
            INeedMoreHelpItem item = session.getSessionCategories().get(0);
            lessonId.setTitle(item.getTitle());
            lessonId.setFile(item.getFile());
            lessonId.setType(item.getType());
            lessonResource.getItems().add(lessonId);

            /** Always send complete list of all lesson names.
             * TODO: many should be done in initialize phase?
             * TODO: why do need the full list?
             */
            __logger.debug("creating list of session names: " + action);
            PrescriptionSessionData sessionData = new PrescriptionSessionData();
            sessionData.setSessionRpa(isActivity);
            for (AssessmentPrescriptionSession s : prescription.getSessions()) {
                presData.getSessionTopics().add(s.getTopic());
            }
            presData.setCurrSession(sessionData);

            
            __logger.debug("Getting prescription resource items: " + action);
            sessionData.setTopic(session.getTopic(),item.getFile());
            sessionData.setSessionNumber(sessionNumber);
            for (INeedMoreHelpResourceType t : session.getPrescriptionInmhTypesDistinct(conn)) {

                // skip the workbooks for now.
                if (t.getTypeDef().getType().equals("workbook"))
                    continue;

                PrescriptionSessionDataResource resource = new PrescriptionSessionDataResource();
                resource.setType(t.getTypeDef().getType());
                resource.setLabel(t.getTypeDef().getLabel());
                for (INeedMoreHelpItem i : t.getResources()) {
                    InmhItemData id = new InmhItemData();
                    id.setFile(i.getFile());
                    id.setTitle(i.getTitle());
                    id.setType(i.getType());

                    resource.getItems().add(id);
                }
                sessionData.getInmhResources().add(resource);
            }

            /** 
             * Add a results resource type to allow user to view current results.
             */
            PrescriptionSessionDataResource resultsResource = new PrescriptionSessionDataResource();
            resultsResource.setType("results");
            resultsResource.setLabel("Quiz Results");
            InmhItemData id = new InmhItemData();
            id.setTitle("Your quiz results");
            id.setFile("");
            id.setType("results");
            resultsResource.getItems().add(id);
            
            
            
            __logger.debug("adding prescription sessions: " + action);
            sessionData.getInmhResources().add(lessonResource);
            sessionData.getInmhResources().add(problemsResource);
            sessionData.getInmhResources().add(resultsResource);


            /** 
             * Get list of lesson resources that have been viewed. 
             */
            __logger.debug("getting list of viewed lessons: " + action);
            GetViewedInmhItemsAction getViewedAction = new GetViewedInmhItemsAction(runId);
            List<RpcData> rdata = new GetViewedInmhItemsCommand().execute(conn, getViewedAction).getRpcData();
            
            List<PrescriptionSessionDataResource> resources = fixupInmhResources(sessionData.getInmhResources());
            for (PrescriptionSessionDataResource r : resources) {
                for (InmhItemData itemData : r.getItems()) {
                    // is this item viewed?
                    for (RpcData rd : rdata) {
                        if (rd.getDataAsString("file").equals(itemData.getFile())) {
                            itemData.setViewed(true);
                            break;
                        }
                    }
                }
            }
            sessionData.setInmhResources(resources);
            
            cmProgram.markSessionAsActive(conn, sessionNumber);
            
            HaTestRun testRun = prescription.getTestRun();
            
            PrescriptionSessionResponse response = new PrescriptionSessionResponse();
            response.setRunId(testRun.getRunId());
            response.setPrescriptionData(presData);
            response.setCorrectPercent(getTestPassPercent(testRun.getAnsweredCorrect() + testRun.getAnsweredIncorrect() + testRun.getNotAnswered(), prescription.getTestRun().getAnsweredCorrect()));
            response.setProgramTitle(prescription.getTest().getTestDef().getTitle());
            

            return response;

        } catch (Exception e) {
            throw new CmRpcException(e);
        }
    }

    
    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetPrescriptionAction.class;
    }

    /**
     * Make sure that ALL resources are in list, and provide a dummy if none do.
     * 
     * @param inmhTypes
     */
    static public List<PrescriptionSessionDataResource> fixupInmhResources(List<PrescriptionSessionDataResource> inmhTypes) {

        List<PrescriptionSessionDataResource> newTypes = new ArrayList<PrescriptionSessionDataResource>();
        String types[][] = {
                { "Lesson", "review", "Review lesson on the current topic" },
                { "Video", "video", "Math videos related to the current topic" },
                { "Activities", "activity", "Math activities and games related to the current topic" },                
                { null, "practice", "Practice problems you must complete before advancing" },
                { "Extra Practice Problems", "cmextra", "Additional workbook problems" },
                { "Quiz Results", "results", "The current quiz's results" },
        };

        for (int i = 0; i < types.length; i++) {
            String type[] = types[i];

            // find this type, if not exist .. create it
            boolean found = false;
            for (PrescriptionSessionDataResource r : inmhTypes) {
                if (r.getType().equals(type[1])) {
                    // exists, so add it
                    if(type[0]!=null)
                        r.setLabel(type[0]);
                    r.setDescription(type[2]);
                    newTypes.add(r);
                    found = true;
                    break;
                }
            }
            if (!found) {
                PrescriptionSessionDataResource nr = new PrescriptionSessionDataResource();
                nr.setLabel(type[0]);
                nr.setType(type[1]);
                nr.setDescription(type[2]);
                InmhItemData iid = new InmhItemData();
                iid.setTitle("No " + type[0] + " Available");
                iid.setType(type[1]);
                iid.setFile("");
                // nr.getItems().add(iid);

                newTypes.add(nr);
            }
        }
        return newTypes;
    }
    
    
    
    

    /**
     * Return the percent of correct from total
     * 
     * @TODO:  find a home ...
     * 
     * @param total
     * @param correct
     * @return
     */
    static public int getTestPassPercent(int total, int correct) {

        double percent = ((double) correct / (double) total) * 100.0d;

        int ipercent = (int) Math.round(percent);
        return ipercent;
    }    

}


class FC {
    public FC(String t, String f) {
        this.title = t;
        this.file = f;
    }
    String title;
    String file;
}