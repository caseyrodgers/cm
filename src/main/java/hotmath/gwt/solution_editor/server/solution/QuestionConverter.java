package hotmath.gwt.solution_editor.server.solution;

import hotmath.HotMathException;
import hotmath.HotMathProperties;
import hotmath.cm.util.service.SolutionDef;
import hotmath.util.HtmlCleanser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.filters.Purifier;
import org.cyberneko.html.filters.Writer;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sb.util.SbFile;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;


/** takes old style question HTML and reformats
 *  as new style
 *  
 * @author casey
 *
 */
public class QuestionConverter {

    String _html;
    Parser _parser;
    DOMFragmentParser _parserBalancer;
    StringWriter _writerBalancer;
    Logger _logger = LoggerFactory.getLogger(QuestionConverter.class);
    
    public QuestionConverter() {
        
        // setup HTML parser to remove elements
        _parser = new Parser();
        _parser.setFeedback(Parser.STDOUT);

        try {
            _parserBalancer = new DOMFragmentParser();
            _writerBalancer = new StringWriter();
            Writer writer = new Writer(_writerBalancer, "UTF8");
            Purifier purifier = new Purifier(); 
            XMLDocumentFilter[] filters = {
                purifier,
                writer,
            };
            _parserBalancer.setProperty("http://cyberneko.org/html/properties/filters", filters);
        }
        catch(Exception e) {
            _logger.error("Error creating SolutionPostProcess", e);
        }
    }


    
    /** old:
     * <div class="question">QUESTION</div>
       <div class="question_guess">
           <img onmouseover="doQuestionResponse('rid_39','no');" onmouseout="doQuestionResponseEnd();" src="/images/tutor5/hint_question-16x16.gif" name="question_0" class="text-bottom">&nbsp;<p>29 and 31</p>
       </div>
       <div class="question_guess">
           <img onmouseover="doQuestionResponse('rid_40','yes');" onmouseout="doQuestionResponseEnd();" src="/images/tutor5/hint_question-16x16.gif" name="question_1" class="text-bottom">&nbsp;<p>35 and 37</p>
      </div>
      
      
      
      
      example tutor_data.js:
      {

      "tutorProperties": {
        "_isControlled":"false",
        "_textCode":"samples",
        "_category":"$category",
        "_bookTitle":"Sample Exercises"
        },
      "_stepUnits_moArray": {},
      "_strings_moArray": {"rid_99":"Combine similar terms.", 
                           "rid_102":"You\'ve found the first odd integer.",
                           "rid_100":"Next, gather the constants on the right side." 
                          }
     }
     */
    
    Node root;
    public String  convertQuestion(String pid, String html) throws Exception {
        try {
            
            SolutionDef solution = new SolutionDef(pid);
            
            String solutionJsonData = new SbFile(new File(solution.getSolutionPathOnDisk(),"tutor_data.js")).getFileContents().toString("\n");
            JSONParser parser = new JSONParser(new StringReader(solutionJsonData));
            final JSONValue value = parser.nextValue();
            JSONObject solutionData = (JSONObject)value;
            JSONObject solutionStrings = (JSONObject)solutionData.get("_strings_moArray");
            HashMap<String,JSONValue> stringStringsMap = solutionStrings.getValue();
                        
            
            _parser.setInputHTML(html);
            NodeVisitor visitor = new  NodeVisitor() {
                public void visitTag(Tag tag) {
                    if(root == null)
                        root = tag;
                    
                    String name = tag.getTagName();
                    if (name.equalsIgnoreCase("div")) {
                        tag.removeAttribute("class");
                        NodeList nl = tag.getChildren();
                        removeExtraneousSubTags(nl);
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
    
    
    public String processHTML_SolutionImagesAbsolute(String html, final String path, final String solutionPath) throws Exception {
        try {
            // put in valid head and foot before 
            // processing so we know at least that
            // is valid.  Otherwise, some text 
            // might be lost.
            String head = "<span class='pp'>";
            String tail = "</span>";
            
            html = head + html + tail;            
            _parser.setInputHTML(html);
            NodeVisitor visitor = new  NodeVisitor() {
                public void visitTag(Tag tag) {
                    if(root == null)
                        root = tag;
                    
                    String name = tag.getTagName();
                    //if (name.equalsIgnoreCase("sub")) {
                    //    SbLogger.postMessage("=== <sub> ===", "upload");
                    //}
                    
                    if(name.equalsIgnoreCase("img")) {
                        String src = tag.getAttribute("src");
                        
                        // modify image's src to absolute if not already
                        if(!src.startsWith("/")) {
                            // is not already absolute
                            src = path + src;
                            tag.setAttribute("src", src);
                        }
                        
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
                    }
                }
            };
            root = null;
            _parser.visitAllNodesWith(visitor);
            html = getDocumentNode(root).toHtml();

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
    
    public void print(org.w3c.dom.Node node,  String indent, StringBuffer sb) {
        sb.append(indent+node.toString());
        org.w3c.dom.Node child = node.getFirstChild();
        while (child != null) {
            print(child, indent+" ", sb);
            child = child.getNextSibling();
        }
    }

    
    private Node getDocumentNode(Node nl) {
        Node parent = nl;
        while(parent.getParent() != null)
            parent = parent.getParent();
        
        return parent;
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
    
    private String getCssClass(int imageHeight) {
        if (imageHeight < 25) return "text-bottom";
        if (imageHeight < 36) return "text-top";
        if (imageHeight < 45) return "middle";
        return "text-top";    
    }

}

