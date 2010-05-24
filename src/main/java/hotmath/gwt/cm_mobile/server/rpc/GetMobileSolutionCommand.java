package hotmath.gwt.cm_mobile.server.rpc;

import hotmath.ProblemID;
import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.GetMobileSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.util.VelocityTemplateFromStringManager;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import sb.util.SbFile;

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

    @Override
    public SolutionResponse execute(final Connection conn, GetMobileSolutionAction action) throws Exception {
        try {
            
            /** read the sprited tutor html */
            File pathWebBase = new File(CmWebResourceManager.getInstance().getFileBase()).getParentFile();
            ProblemID pid = new ProblemID(action.getPid());
            String path = pid.getSolutionPath().replace("\\", "/");
            String pidPath = "help/solutions/" + path + "/" + pid.getGUID();
            String spriteHtmlPath =  pidPath + "/tutor_steps-sprited.html";
            String solutionDataPath = pidPath + "/tutor_data.js";
            
            String solutionHtml = new SbFile(new File(pathWebBase, spriteHtmlPath)).getFileContents().toString("\n");
            String solutionData = new SbFile(new File(pathWebBase, solutionDataPath)).getFileContents().toString("\n");
            
            Map<String, String> map = new HashMap<String, String>();
            map.put("solution_html", solutionHtml);
            map.put("pid", action.getPid());

            InputStream is = getClass().getResourceAsStream("mobile_tutor_wrapper.vm");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String tutorWrapper = null;
            StringBuilder sb = new StringBuilder();
            while ((tutorWrapper = br.readLine()) != null) {
                sb.append(tutorWrapper);
            }
            tutorWrapper = sb.toString();

            int i = 1;
            solutionHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(tutorWrapper, map);
            
            SolutionResponse rs = new SolutionResponse(solutionHtml, solutionData, false);
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
    