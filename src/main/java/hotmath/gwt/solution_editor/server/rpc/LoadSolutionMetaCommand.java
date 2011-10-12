package hotmath.gwt.solution_editor.server.rpc;

import hotmath.HotMathException;
import hotmath.HotMathTokenReplacements;
import hotmath.ProblemID;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.rpc.LoadSolutionMetaAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionMeta;
import hotmath.gwt.solution_editor.client.rpc.SolutionMetaStep;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;
import hotmath.gwt.solution_editor.server.solution.TutorSolution;
import hotmath.gwt.solution_editor.server.solution.TutorStepUnit;
import hotmath.gwt.solution_editor.server.solution.TutorStepUnit.Role;
import hotmath.solution.SolutionPostProcess;
import hotmath.solution.SolutionResources;
import hotmath.util.HtmlCleanser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.tags.Div;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;

public class LoadSolutionMetaCommand implements ActionHandler<LoadSolutionMetaAction, SolutionMeta>{

    Logger _logger = Logger.getLogger(LoadSolutionMetaCommand.class.getName());
    String _pid;

    @Override
    public SolutionMeta execute(Connection conn, LoadSolutionMetaAction action) throws Exception {
        
        _pid = action.getPid();
        String solutionBase = CatchupMathProperties.getInstance().getSolutionBase();
        ProblemID pid = new ProblemID(action.getPid());
        TutorSolution tutorSolution = new CmSolutionManagerDao().getTutorSolution(conn, _pid);
        SolutionMeta meta = new SolutionMeta(action.getPid());
        
        /** Save MD5, and if different on save the user must confirm overwrite
         *  
         */
        meta.setMd5OnRead(new CmSolutionManagerDao().getSolutionMd5(conn, action.getPid()));
        
        
        
        
        String problemStatement = tutorSolution.getProblem().getStatement();
        
        // if figure attribute then add as normal HTML block
        String figure = tutorSolution.getProblem().getStatementFigure();
        meta.setProblemStatement(postProcessHtml(solutionBase,problemStatement ));
        meta.setFigure(figure);
        
        List<TutorStepUnit> units = tutorSolution.getProblem().getStepUnits();
        for(int u=0,t=units.size();u<t;u++) {

            /** each step is a two units: the hint and step
             * 
             * do sanity check!
             */
            if(!(units.get(u).getRole() == Role.HINT) ||
               !(units.get(u+1).getRole() == Role.STEP)) {
                throw new Exception("Invalid series of steps in solution: " + action.getPid());
            }
            String hint = postProcessHtml(solutionBase, units.get(u++).getContentAsString());
            String text = postProcessHtml(solutionBase, units.get(u).getContentAsString());
            
            String stepFigure = units.get(u).getFigures().size() > 0?units.get(u).getFigures().get(0):null;
            SolutionMetaStep step = new SolutionMetaStep(meta,hint,text,stepFigure);
            meta.getSteps().add(step);
        }
        return meta;
    }
    
    
    private String postProcessHtml(String solutionBase, String html) throws Exception {
        
        SolutionDef solution = new SolutionDef(_pid);
        
        /** Do any token replacement before loading into editor
         * 
         */
        SolutionResources sr = new SolutionResources();
        html = HotMathTokenReplacements.doReplacements(sr, html);
        
        String processed = _solutionPostProcess.processHTML_SolutionImagesAbsolute( html, solution.getSolutionPathHttp() + "/", solutionBase);
        return processed;
    }
    

    String _html;
    Parser _parser;
    DOMFragmentParser _parserBalancer;
    StringWriter _writerBalancer;
    Node root;
    SolutionPostProcess _solutionPostProcess = new SolutionPostProcess();
    String _widgetJson;
    public String  removeHmWidgetOldStyle(String html) throws HotMathException {
        
        _parser = new Parser();
        _parser = new Parser();
        _parser.setFeedback(Parser.STDOUT);

        String widgetJson=null;
        
        //SbLogger.postMessage("in -> " + html, "postprocess");
        try {
            _parser.setInputHTML(html);
            NodeVisitor visitor = new  NodeVisitor() {
                public void visitTag(Tag tag) {
                    if(root == null)
                        root = tag;
                    
                    String name = tag.getTagName();
                    
                    if (name.equalsIgnoreCase("div")) {
                        String id = tag.getAttribute("id");
                        if(id != null && id.equals("hm_flash_object")) {
                            
                            /** Extract the json for JS widget only */
                            NodeList nl = tag.getParent().getChildren();
                            for(int i=0;i<nl.size();i++) {
                                
                               nl.extractAllNodesThatMatch(new NodeFilter() {
                                
                                    @Override
                                    public boolean accept(Node arg0) {
                                        Div div=null;
                                        if(arg0 instanceof Div) {
                                            div = ((Div)arg0);
                                            String id = div.getAttribute("id");
                                            if(id != null) {
                                                if(id.equals("hm_flash_widget")) {
                                                    _widgetJson = arg0.getText();
                                                }
                                            }
                                        }
                                        return false;
                                    }
                               });
                            }
                            tag.setEmptyXmlTag(true);
                        }
                    }
                }
            };
            root = null;
            _parser.visitAllNodesWith(visitor);
            html = getDocumentNode(root).toHtml();
            
            html = HtmlCleanser.getInstance().cleanseHtml(html);
            return html;
            
        }
        catch(Exception e) {
            throw new HotMathException(e, "Error creating new HTML Parser");
        }
    }    
    
    private void removeExtraneousSubTags(NodeList nl) {
        if (nl != null) {
            Node[] a = nl.toNodeArray();
            int beginSubIndex = -1;
            int imgIndex = -1;
            for (int i=0; i < a.length; i++) {
                if (a[i] instanceof Tag) {
                    String n = ((Tag) a[i]).getTagName();
                    if (n.equalsIgnoreCase("sub")) {
                        if (beginSubIndex < 0) {
                            beginSubIndex = i;
                            continue;
                        }
                        if (i == (imgIndex + 1)) {
                            nl.remove(a[i]);
                        }
                        beginSubIndex = -1;
                        continue;
                    }
                    if (n.equalsIgnoreCase("img")) {
                        imgIndex = i;
                        if (beginSubIndex > -1 && i == (beginSubIndex + 1)) {
                            nl.remove(a[beginSubIndex]);
                        }
                    }
                    //SbLogger.postMessage("+++ [" + i + "]: " + ((Tag) a[i]).getTagName(), "upload");
                }
            }
            //String sHtml = nl.toHtml();
            //SbLogger.postMessage("--- <p> children: " + sHtml, "upload");
        }
    }
    
    /** Return the height from the image
     * 
     * @param imageFile The relative file to search
     * @param imageDir the base directory to use
     * 
     * @return The file size in bytes, zero if file does not exist
     */
    private int obtainHeightFromImage(String imageFile, String imageDir) {
        int h = 0;
           if (imageDir != null) {
               String imagePath = imageDir + "/" + imageFile;
               try {
                   File file = new File(imagePath);
                   
                   if(file.exists()) {
                       FileInputStream is = new FileInputStream(imagePath);
                       BufferedImage img = ImageIO.read(is);
                       h = img.getHeight();
                       //int w = img.getWidth();
                   }
               }
               catch (Exception e) {
                  _logger.error("Error obtaining height for image: " + imagePath,e);
               }
        }
           return h;
    }

    private Node getDocumentNode(Node nl) {
        Node parent = nl;
        while(parent.getParent() != null)
            parent = parent.getParent();
        
        return parent;
    }
    
    
    private String getCssClass(int imageHeight) {
        if (imageHeight < 25) return "text-bottom";
        if (imageHeight < 36) return "text-top";
        if (imageHeight < 45) return "middle";
        return "text-top";    
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return LoadSolutionMetaAction.class;
    }    

}
