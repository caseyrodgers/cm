package hotmath.gwt.cm_mobile_shared.server.rpc;

import hotmath.HotMathLogger;
import hotmath.HotMathUtilities;
import hotmath.ProblemID;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc.server.service.ActionHandlerManualConnectionManagement;
import hotmath.gwt.shared.server.service.command.SolutionHTMLCreatorImplFileSystem;
import hotmath.solution.SolutionParts;
import hotmath.solution.writer.SolutionHTMLCreator;
import hotmath.solution.writer.TutorProperties;

import java.sql.Connection;

import org.apache.log4j.Logger;

public class GetSolutionCommand implements ActionHandler<GetSolutionAction, SolutionResponse>, ActionHandlerManualConnectionManagement{

        private static Logger logger = Logger.getLogger(GetSolutionCommand.class);
    public static SolutionHTMLCreator __creator;
    static TutorProperties __tutorProps = new TutorProperties();
    static {
        try {
            __creator = new SolutionHTMLCreatorImplFileSystem(__tutorProps.getTemplate(), __tutorProps.getTutor());
        } catch (Exception hme) {
            HotMathLogger.logMessage(hme, "Error creating solution creator: " + hme);
        }
    }


    @Override
    public SolutionResponse execute(final Connection conn, GetSolutionAction action) throws Exception {
        try {
            String pid = action.getPid();

            /** LOOK HERE! */
            //pid = "cmextras_1_1_1_101_1";


            SolutionParts parts = __creator.getSolutionHTML(null,__tutorProps, pid);
            String solutionHtml = parts.getMainHtml();

            ProblemID ppid = new ProblemID(action.getPid());
            String path = ppid.getSolutionPath_DirOnly("solutions");

            solutionHtml = HotMathUtilities.makeAbsolutePaths(path, solutionHtml);

            /** Replace local server to one with static images
             *
             */
            //String match = "/help/solutions";
            //String imageServer = "http://m.hotmath.com/help/solutions";
            //solutionHtml = solutionHtml.replaceAll(match, imageServer);


//            Map<String, String> map = new HashMap<String, String>();
//            map.put("solution_html", solutionHtml);
//            map.put("pid", pid);

            //String runTimeDir = HotMathProperties.getInstance().getProperty("cm.runtime",".");
            //String tutorWrapper = new SbFile(runTimeDir + "/" + "mobile_tutor_wrapper.vm").getFileContents().toString("\n");
            //solutionHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(tutorWrapper, map);

            ProblemNumber problem = new ProblemNumber(ppid.getProblemNumber(), ppid.getProblemSet(),ppid.getGUID(),ppid.getPage()       );
            SolutionResponse rs = new SolutionResponse(problem, solutionHtml, parts.getData(), false);
            return rs;
        } catch (Exception e) {
                logger.error(String.format("*** Error executing Action: %s", action.toString()), e);
            throw new CmRpcException(e);
        }
    }

        @Override
    public Class<? extends Action<? extends Response>> getActionType() {
            return GetSolutionAction.class;
    }
}
