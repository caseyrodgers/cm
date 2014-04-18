package hotmath.gwt.solution_editor.server.rpc;

import hotmath.HotMathProperties;
import hotmath.ProblemID;
import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.solution_editor.client.rpc.SaveSolutionStepsAdminAction;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;
import hotmath.gwt.solution_editor.server.solution.TutorSolution;

import java.io.File;
import java.sql.Connection;

import sb.util.SbFile;

public class SaveSolutionStepsAdminCommand implements ActionHandler<SaveSolutionStepsAdminAction, RpcData> {

    @Override
    public RpcData execute(Connection conn, SaveSolutionStepsAdminAction action) throws Exception {
        SolutionDef def = new SolutionDef(action.getPid());
        TutorSolution ts = new TutorSolution(action.getUserName(), def, action.getStatement(), action.getStatementFigure(), action.getSteps(),action.isActive());

        /**
         * make sure it has not been changed since read
         * 
         */
        if(action.getMd5OnRead() != null) {
            if (new CmSolutionManagerDao().solutionExists(conn, action.getPid())) {
                String md5Now = new CmSolutionManagerDao().getSolutionMd5(conn, action.getPid());
                if (!md5Now.equals(action.getMd5OnRead())) {
                    if (!action.isOverrideDirty()) {
                        throw new CmException("Solution has been modified since read: " + action.getPid());
                    }
                }
            }
        }

        new CmSolutionManagerDao().saveSolutionXml(conn, action.getPid(), ts.toXml(), action.getTutorDefine(), action.isActive());

        /**
         * if the destination does not match the original, the copy resources
         * from the original to the newly created solution
         */
        if (!action.getOriginalPid().equals(action.getPid())) {
            copySolutionResources(action.getOriginalPid(), action.getPid());
        }

        String md5 = new CmSolutionManagerDao().getSolutionMd5(conn, action.getPid());
        RpcData rdata = new RpcData("status=OK,md5=" + md5);
        return rdata;
    }

    private void copySolutionResources(String from, String to) throws Exception {
        
        ProblemID pidFrom = new ProblemID(from);
        ProblemID pidTo = new ProblemID(to);

        String destination = HotMathProperties.getInstance().getHotMathWebBase() + "/"
                + HotMathProperties.getInstance().getStaticSolutionsDir() + "/" + pidTo.getSolutionPath() + "/" + to;

        String source = HotMathProperties.getInstance().getHotMathWebBase() + "/"
                + HotMathProperties.getInstance().getStaticSolutionsDir() + "/" + pidFrom.getSolutionPath() + "/"
                + from;

        File file = new File(source);
        
        copyFiles(file, destination);
    }
    
    private void copyFiles(File file, String destination) throws Exception {
        File fileKids[] = file.listFiles();
        if(fileKids != null) {
            for (int iKid = 0; iKid < fileKids.length; iKid++) {
                File fileKid = fileKids[iKid];
                
                if(fileKid.isDirectory()) {
                    copyFiles(fileKid, destination);
                }
                else {
                    String sSource = fileKid.getCanonicalPath();
        
                    // Add PNG..
                    if ( sSource.endsWith(".html") || 
                            sSource.endsWith(".js") ||
                            sSource.endsWith(".json"))  {
                        continue;
                    }
                    
                    String out=destination;
                    if(fileKid.getParent().endsWith("resources")) {
                        out += "/resources";
                    }
                        
                    SbFile.copyFile(sSource, out + "/" + fileKid.getName());
                }
            }
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return SaveSolutionStepsAdminAction.class;
    }

}
