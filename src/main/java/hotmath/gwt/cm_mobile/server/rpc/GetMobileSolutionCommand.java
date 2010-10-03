package hotmath.gwt.cm_mobile.server.rpc;

import hotmath.HotMathLogger;
import hotmath.HotMathUtilities;
import hotmath.ProblemID;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.GetMobileSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.server.service.command.SolutionHTMLCreatorImplFileSystem;
import hotmath.solution.SolutionParts;
import hotmath.solution.writer.SolutionHTMLCreator;
import hotmath.solution.writer.TutorProperties;
import hotmath.util.VelocityTemplateFromStringManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Return the raw HTML that makes up the solution
 * 
 * Use the uid to lookup if this solution has any ShowWork applied
 * 
 * Return RpcData with the following members: solutionHtml, hasShowWork
 * 
 */
public class GetMobileSolutionCommand implements ActionHandler<GetMobileSolutionAction, SolutionResponse> {

	private static Logger logger = Logger.getLogger(GetMobileSolutionCommand.class);
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
    public SolutionResponse execute(final Connection conn, GetMobileSolutionAction action) throws Exception {
        try {
            String pid = action.getPid();
            SolutionParts parts = __creator.getSolutionHTML(__tutorProps, pid);
            String solutionHtml = parts.getMainHtml();

            ProblemID ppid = new ProblemID(pid);
            String path = ppid.getSolutionPath_DirOnly("solutions");
            solutionHtml = HotMathUtilities.makeAbsolutePaths(path, solutionHtml);

            Map<String, String> map = new HashMap<String, String>();
            map.put("solution_html", solutionHtml);
            map.put("pid", pid);

            InputStream is = getClass().getResourceAsStream("mobile_tutor_wrapper.vm");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String tutorWrapper = null;
            StringBuilder sb = new StringBuilder();
            while ((tutorWrapper = br.readLine()) != null) {
                sb.append(tutorWrapper);
            }
            tutorWrapper = sb.toString();
            solutionHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(tutorWrapper, map);
            
            SolutionResponse rs = new SolutionResponse(solutionHtml, parts.getData(), false);
            return rs;
        } catch (Exception e) {
        	logger.error(String.format("*** Error executing Action: %s", action.toString()), e);
            throw new CmRpcException(e);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetMobileSolutionAction.class;
    }
}
    