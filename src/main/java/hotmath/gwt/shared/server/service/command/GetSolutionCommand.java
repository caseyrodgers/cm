package hotmath.gwt.shared.server.service.command;

import hotmath.HotMathLogger;
import hotmath.HotMathUtilities;
import hotmath.ProblemID;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_rpc_core.server.service.ActionHandlerManualConnectionManagement;
import hotmath.solution.SolutionParts;
import hotmath.solution.writer.SolutionHTMLCreator;
import hotmath.solution.writer.TutorProperties;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.SolutionDao;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;

import sb.util.SbFile;

/**
 * Return the raw HTML that makes up the solution
 * 
 * Use the uid to lookup if this solution has any ShowWork applied
 * 
 * Return RpcData with the following members: solutionHtml, hasShowWork
 * 
 */
public class GetSolutionCommand implements ActionHandler<GetSolutionAction, SolutionInfo>, ActionHandlerManualConnectionManagement {
	
	private static final Logger logger = Logger.getLogger(GetSolutionCommand.class);

    public static SolutionHTMLCreator __creator;
    static TutorProperties __tutorProps = new TutorProperties();
    static {
        try {
            __creator = new SolutionHTMLCreatorImplFileSystem(__tutorProps.getTemplate(), __tutorProps.getTutor());
        } catch (Exception hme) {
            HotMathLogger.logMessage(hme, "Error creating solution creator: " + hme);
        }
    }






    protected Node root;

    @Override
    public SolutionInfo execute(final Connection connNotNused, GetSolutionAction action) throws Exception {
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            String pid = action.getPid();
            int uid = action.getUid();
            
            ProblemID ppid = new ProblemID(pid);
            
            SolutionParts sp = null;
            try {
            	sp = __creator.getSolutionHTML(null,null, pid);
            }
            catch(Exception e) {
            	logger.debug("Error getting solution (using default)", e);
            	sp = createDefaultSolutionParts();
            }
            String solutionHtml= sp.getMainHtml();
            
            String path = ppid.getSolutionPath_DirOnly("solutions");
            solutionHtml = HotMathUtilities.makeAbsolutePaths(path, solutionHtml);
            
            solutionHtml = processMathMlTransformations(solutionHtml);
            
            boolean hasShowWork = getHasShowWork(conn, uid, pid);

            SolutionInfo solutionInfo = new SolutionInfo(pid,solutionHtml,sp.getData(),hasShowWork);

            /** read the context stored for this solution view instance or return null allowing new context to be 
             *  created on client.
             */
            solutionInfo.setContext(SolutionDao.getInstance().getSolutionContext(action.getRunId(), action.getPid()));
            
            
            /** If there is a runid, get the widget result for this runid/pid
             * 
             */
            if(action.getRunId() > 0) {
                solutionInfo.setWidgetResult(HaTestRunDao.getInstance().getRunTutorWidgetValue(action.getRunId(), action.getPid()));
            }
            
            return solutionInfo;
        } catch (Exception e) {
        	logger.error(String.format("*** Error executing Action: %s", action.toString()), e);
            throw new CmRpcException(e);
        }
        finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
    }

    
    enum MathMlTransform{NONE, MAKE_TEXT_SMALLER, MAKE_FRACTIONS_LARGER}
    
    /** process any mathml with named math-process directive.  
     * 
     *  These directives, if specified, will modifing the return 
     *  MathML with attributes+values.  Such as mathsize.
     *  
     *  
     * @param solutionHtml
     * @return
     * @throws Exception
     */
    private String processMathMlTransformations(String solutionHtml) throws Exception {
        
        int start=0;
        int end=0;

        solutionHtml = solutionHtml.replace("<MFRAC",  "<MSTYLE><MFRAC");
        solutionHtml = solutionHtml.replace("</MFRAC>",  "</MFRAC></MSTYLE>");
        Parser parser = new Parser();
        try {
            NodeVisitor visitor = new NodeVisitor() {
                MathMlTransform transform = MathMlTransform.NONE;
                public void visitTag(org.htmlparser.Tag tag) {
                    
                    if (root == null)
                        root = tag;

                    if(tag.getTagName().equalsIgnoreCase("math")) {
                        
                        /** check to see if there is a transform present
                         * 
                         */
                        transform = MathMlTransform.NONE;
                        String mathProcess = tag.getAttribute("math-process");
                        if(mathProcess != null) {
                            mathProcess = mathProcess.toLowerCase();
                            if(mathProcess.contains("textsmaller")) {
                                transform = MathMlTransform.MAKE_TEXT_SMALLER;
                            }
                            else if(mathProcess.contains("fractionslarger")) {
                                transform = MathMlTransform.MAKE_FRACTIONS_LARGER;
                            }
                        }
                    }
                    else if(tag.getTagName().equalsIgnoreCase("mtext")) {
                        if(transform == MathMlTransform.MAKE_TEXT_SMALLER) {
                            tag.setAttribute("mathsize",  ".7em");
                        }
                    }
                    else if(tag.getTagName().equalsIgnoreCase("mstyle")) {
                        if(transform == MathMlTransform.MAKE_FRACTIONS_LARGER) {
                            tag.setAttribute("mathsize",  "6em");
                        }
                    }

                }
            };
            parser.setInputHTML(solutionHtml);
            parser.visitAllNodesWith(visitor);
            solutionHtml = getDocumentNode(root).toHtml();
            
        }
        catch(Exception e) {
            throw e;
        }
        
        return solutionHtml;
    }


    private Node getDocumentNode(Node nl) {
        Node parent = nl;
        while (parent.getParent() != null)
            parent = parent.getParent();

        return parent;
    }
    private SolutionParts createDefaultSolutionParts() throws Exception {
    	SolutionParts sp = new SolutionParts();
    	sp.setMainHtml("Problem was not found");
    	
    	File file = new File(CatchupMathProperties.getInstance().getCatchupRuntime(), "empty_tutor.json");
    	String data = new SbFile(file).getFileContents().toString("\n");
    	sp.setData(data);
    	
    	return sp;
	}

	@Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetSolutionAction.class;
    }
    
    

    


    /**
     * Return true if this solution has any Show Work activity for this user.
     * 
     * 
     * @param uid
     * @param pid
     * @return
     * @throws Exception
     */
    private boolean getHasShowWork(final Connection conn, int uid, String pid) throws Exception {
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            String sql = "select count(*) as cnt from HA_TEST_RUN_WHITEBOARD where user_id = ? and pid = ?";
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, uid);
            pstat.setString(2, pid);

            rs = pstat.executeQuery();
            rs.first();
            int cnt = rs.getInt("cnt");
            return cnt > 0;
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
    }    

}
