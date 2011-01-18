package hotmath.gwt.solution_editor.server.rpc;

import hotmath.HotMathException;
import hotmath.HotMathProperties;
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
import hotmath.solution.SolutionPostProcess;
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
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;

public class LoadSolutionMetaCommand implements ActionHandler<LoadSolutionMetaAction, SolutionMeta>{

    Logger _logger = Logger.getLogger(LoadSolutionMetaCommand.class.getName());
    String _pid;

    @Override
    public SolutionMeta execute(Connection conn, LoadSolutionMetaAction action) throws Exception {
        
        _pid = action.getPid();
        String solutionBase = CatchupMathProperties.getInstance().getSolutionBase();
        
        TutorSolution tutorSolution = new CmSolutionManagerDao().getTutorSolution(conn, action.getPid());
        SolutionMeta meta = new SolutionMeta(action.getPid());
        meta.setProblemStatement(postProcessHtml(solutionBase, tutorSolution.getProblem().getStatement()));
        List<TutorStepUnit> units = tutorSolution.getProblem().getStepUnits();
        for(int u=0,t=units.size();u<t;u++) {

            /** each step is a two units: the hint and step
             * 
             */
            String hint = postProcessHtml(solutionBase, units.get(u++).getHint());
            String text = postProcessHtml(solutionBase, units.get(u).getStep());
        
            SolutionMetaStep step = new SolutionMetaStep(hint,text);
            meta.getSteps().add(step);
        }
        return meta;
    }
    
    
    private String postProcessHtml(String solutionBase, String html) throws Exception {
        
        SolutionDef solution = new SolutionDef(_pid);
        
        String processed = _solutionPostProcess.processHTML_SolutionImagesAbsolute( html, solution.getSolutionPathHttp() + "/", solutionBase);
        return processed;
    }
    
    String _html;
    Parser _parser;
    DOMFragmentParser _parserBalancer;
    StringWriter _writerBalancer;
    Node root;
    SolutionPostProcess _solutionPostProcess = new SolutionPostProcess();
    
    public String  processHTML(String html, final String solutionPath) throws HotMathException {
        String head = "<span class='pp'>";
        String tail = "</span>";
        
        // put in valid head and foot before 
        // processing so we know at least that
        // is valid.  Otherwise, some text 
        // might be lost.
        html = head + html + tail;
            //SbLogger.postMessage("in -> " + html, "postprocess");
        try {
            _parser.setInputHTML(html);
            NodeVisitor visitor = new  NodeVisitor() {
                public void visitTag(Tag tag) {
                    if(root == null)
                        root = tag;
                    
                    String name = tag.getTagName();
                    //if (name.equalsIgnoreCase("sub")) {
                    //    SbLogger.postMessage("===  <sub> ===", "upload");
                    //}
                    
                    if (name.equalsIgnoreCase("p")) {
                        tag.removeAttribute("class");
                        NodeList nl = tag.getChildren();
                        removeExtraneousSubTags(nl);
                    }
                    else {
                        if (name.equalsIgnoreCase("img")) {
                            String src = tag.getAttribute("src");
                            if (src == null || src.trim().length() == 0) {
                                _logger.warn("image src cannot be empty");
                            }
                            else {
                                if(tag.getAttribute("v:shapes") != null) {
                                    tag.removeAttribute("v:shapes");
                                    tag.removeAttribute("width");
                                    tag.removeAttribute("height");
                                    tag.setAttribute("src", src);
                                }
                                else {
                                    tag.removeAttribute("align");
                                    String cssClass = tag.getAttribute("class");
                                    String height = null;
                                    if (cssClass == null) {
                                        height = tag.getAttribute("height");
                                        int h = 0;
                                        if (height != null) {
                                            h = Integer.parseInt(height);
                                            cssClass = getCssClass(h);
                                        }
                                        else {
                                            String path;
                                            // obtain height from image file
                                            path = (src.startsWith("/")) ?
                                                   HotMathProperties.getInstance().getHotMathHome() + "/web" :
                                                   solutionPath;
                                            h = obtainHeightFromImage(src, path);
                                            cssClass = getCssClass(h);
                                            _logger.debug("*** obtained CSS class {" + cssClass + "} from [" + src + "] h(" + h +")");
                                        }
                                    }
                                    tag.setAttribute("class", cssClass);
                                    tag.setAttribute("src", src);
                                }
                            }                        
                        }
                    }
                }
            };
            root = null;
            _parser.visitAllNodesWith(visitor);
            html = getDocumentNode(root).toHtml();
            
            html = HtmlCleanser.getInstance().cleanseHtml(html);
            
            // strip off the head and tail
            html = html.substring(head.length(),html.length()-tail.length()).trim();
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
