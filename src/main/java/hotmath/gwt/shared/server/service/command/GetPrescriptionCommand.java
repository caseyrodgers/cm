package hotmath.gwt.shared.server.service.command;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.GetPrescriptionAction;
import hotmath.gwt.shared.client.rpc.action.GetViewedInmhItemsAction;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.inmh.INeedMoreHelpResourceType;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.util.Jsonizer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;



/**
 * Transfer data from local data structures into JSON
 * 
 * 
 * Show all INMH types, and provide an empty one where none exist
 * 
 * Lesson, Video, Activities, Required Problems, Extra Problems
 * 
 * 
 * marks the user object with the current active session
 * 
 * @param runId
 *            The run id
 * @param sessionNumber
 *            The session number to load
 * @param updateActiveInfo
 *            Should the user's active info be updated. If false, then no
 *            user state is changed.
 * 
 */
public class GetPrescriptionCommand implements ActionHandler<GetPrescriptionAction, PrescriptionSessionResponse> {

    static Logger __logger = Logger.getLogger(GetPrescriptionCommand.class);

    @Override
    public PrescriptionSessionResponse execute(final Connection conn, GetPrescriptionAction action) throws Exception {

        __logger.info("getting prescription: " + action);
        int runId = action.getRunId();
        int sessionNumber = action.getSessionNumber();
        try {
            AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, runId);
            
            new CmStudentDao().verifyActiveProgram(conn, pres.getTest().getTestId());


            int totalSessions = pres.getSessions().size();
            if (totalSessions == 0) {
                // no prescription created (no missed answers?)
                RpcData rdata = new RpcData();
                rdata.putData("correct_percent", 100);
                
                PrescriptionSessionResponse resp = new PrescriptionSessionResponse();
                resp.setCorrectPercent(100);
                return resp;
            }
            // which session
            if (sessionNumber > (totalSessions - 1)) {
                __logger.warn(String.format("WARNING: session request for %d is outside bounds of prescription: %d, %d",
                		runId, sessionNumber, totalSessions));
                sessionNumber = 0;
            }
            AssessmentPrescriptionSession sess = pres.getSessions().get(sessionNumber);

            PrescriptionData presData = new PrescriptionData();

            List<AssessmentPrescription.SessionData> practiceProblems = sess.getSessionDataFor(sess.getTopic());
            PrescriptionSessionDataResource problemsResource = new PrescriptionSessionDataResource();
            problemsResource.setType("practice");
            problemsResource.setLabel("Required Practice Problems");
            int cnt = 1;
            for (AssessmentPrescription.SessionData sdata : practiceProblems) {
                InmhItemData id = new InmhItemData();
                id.setTitle("Problem " + cnt++);
                id.setFile(sdata.getPid());
                id.setType("practice");

                problemsResource.getItems().add(id);
            }

            PrescriptionSessionDataResource lessonResource = new PrescriptionSessionDataResource();
            lessonResource.setType("review");
            lessonResource.setLabel("Review Lesson");
            InmhItemData lessonId = new InmhItemData();

            // Get the lesson this session is based on
            // each session is a single topic
            INeedMoreHelpItem item = sess.getSessionCategories().get(0);
            lessonId.setTitle(item.getTitle());
            lessonId.setFile(item.getFile());
            lessonId.setType(item.getType());
            lessonResource.getItems().add(lessonId);

            // always send complete list of all topics
            // @TODO: should we have an initialize phase and return this info
            // This would impose two request/response
            PrescriptionSessionData sessionData = new PrescriptionSessionData();
            for (AssessmentPrescriptionSession s : pres.getSessions()) {
                presData.getSessionTopics().add(s.getTopic());
            }
            presData.setCurrSession(sessionData);

            sessionData.setTopic(sess.getTopic());
            sessionData.setSessionNumber(sessionNumber);
            sessionData.setName(sess.getName());
            for (INeedMoreHelpResourceType t : sess.getPrescriptionInmhTypesDistinct(conn)) {

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

            // add a results resource type to allow user to view current results
            PrescriptionSessionDataResource resultsResource = new PrescriptionSessionDataResource();
            resultsResource.setType("results");
            resultsResource.setLabel("Quiz Results");
            InmhItemData id = new InmhItemData();
            id.setTitle("Your quiz results");
            id.setFile("");
            id.setType("results");
            resultsResource.getItems().add(id);
            
            
            

            sessionData.getInmhResources().add(lessonResource);
            sessionData.getInmhResources().add(problemsResource);
            sessionData.getInmhResources().add(resultsResource);

            /** Call action and request list of INMH items */
            GetViewedInmhItemsAction getViewedAction = new GetViewedInmhItemsAction(runId);
            List<RpcData> rdata = ActionDispatcher.getInstance().execute(getViewedAction).getRpcData();
            
            
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

            // update this user's active run session
            if (action.getUpdateActionInfo()) {
                pres.getTest().getUser().setActiveTestRunSession(sessionNumber);
                pres.getTest().getUser().update(conn);
            }
            
            
            
            /** Mark this lesson as being viewed
             * 
             */
            new HaTestRunDao().setLessonViewed(conn,runId,sessionNumber);
            
            PrescriptionSessionResponse response = new PrescriptionSessionResponse();
            response.setPrescriptionData(presData);
            response.setCorrectPercent(getTestPassPercent(pres.getTest().getTestQuestionCount(), pres.getTestRun().getAnsweredCorrect()));
            response.setProgramTitle(pres.getTest().getTestDef().getTitle());

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
    private List<PrescriptionSessionDataResource> fixupInmhResources(List<PrescriptionSessionDataResource> inmhTypes) {

        List<PrescriptionSessionDataResource> newTypes = new ArrayList<PrescriptionSessionDataResource>();
        String types[][] = {
                { "Lesson", "review", "Review lesson on the current topic" },
                { "Video", "video", "Math videos related to the current topic" },
                { "Activities", "activity", "Math activities and games related to the current topic" },                
                { "Required Practice Problems", "practice", "Practice problems you must complete before advancing" },
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