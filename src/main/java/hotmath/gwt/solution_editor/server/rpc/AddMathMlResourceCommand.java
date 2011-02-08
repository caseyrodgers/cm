package hotmath.gwt.solution_editor.server.rpc;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.solution_editor.client.rpc.AddMathMlResourceAction;

import java.io.File;
import java.io.StringReader;
import java.sql.Connection;

import javax.xml.transform.stream.StreamSource;

import net.sourceforge.jeuclid.MutableLayoutContext;
import net.sourceforge.jeuclid.context.LayoutContextImpl;
import net.sourceforge.jeuclid.converter.Converter;
import net.sourceforge.jeuclid.parser.Parser;

import org.w3c.dom.Document;

import sb.util.SbFile;

/**

 */
public class AddMathMlResourceCommand implements ActionHandler<AddMathMlResourceAction, RpcData> {

    @Override
    public RpcData execute(Connection conn, AddMathMlResourceAction action) throws Exception {

        String resourceFile=null;
        switch(action.getType()) {
        
        case LOCAL:
             resourceFile = getResourceName(action.getPid(), action.getResourceName());
             break;
             
        case GLOBAL:
            resourceFile = CatchupMathProperties.getInstance().getSolutionBase() + "/help/solutions/resources/" + action.getResourceName();
            break;
        }
        
        
        
        /** strip off any extension 
         *  
         */
        if(resourceFile.indexOf(".") > -1) {
            resourceFile = resourceFile.substring(0, resourceFile.indexOf("."));
        }
        
        /** convert entities to portable equivalents
         * 
         */
        String mathMl = MathMlEntityConverter.charToHtml(action.getMathMl());
        
        
        writeImage(resourceFile, mathMl);        
        writeMathmlDefinition(resourceFile, mathMl);

        return new RpcData("status=OK");
    }
    
    /** Return full path to resource (no extention).
     * 
     * @param pid
     * @param resourceName
     * @return
     */
    private String getResourceName(String pid, String resourceName) throws Exception {
        SolutionDef def = new SolutionDef(pid);
        String resourceFile = def.getResourcesPath();
        resourceFile += "/" + resourceName;
        
        int dot = resourceFile.indexOf(".");
        if(dot > -1)
            resourceFile = resourceFile.substring(0, dot);
        
        return resourceFile;
    }


    private void writeMathmlDefinition(String resourceName, String mathMl) throws Exception {
        SbFile sFile = new SbFile(new File(resourceName + ".mathml"));
        sFile.setFileContents( mathMl );
        sFile.writeFileOut();
    }


//    <?xml version="1.0"?> 
//    <!DOCTYPE some_name [ 
//    <!ENTITY nbsp "&#160;"> 
//    ]> 
    

    public void writeImage(String resourceFile, String mathMl) throws Exception {
       try {
           // convert the mathML into a DOM object
           String validXml = makeValidXml(mathMl);
           final StringReader sr = new StringReader(validXml);
           final Document doc = Parser.getInstance().parseStreamSource(new StreamSource(sr));
           
           MutableLayoutContext lc = new LayoutContextImpl(LayoutContextImpl.getDefaultLayoutContext());
           
           /** make sure path to image exists
            * 
            */
           File resourceDir = new File(resourceFile).getParentFile();
           if(!resourceDir.exists()) {
               resourceDir.mkdirs();
           }

           if (Converter.getInstance().convert(doc, new File(resourceFile + ".png"),"image/png", lc) == null) {
               throw new Exception("Could not create math equation: " + resourceFile);
           }

       } catch (final Exception e) {
           e.printStackTrace();
           throw e;
       }
   }
    
    private String makeValidXml(String mathMl) {
        String  head = 
            "<?xml version=\"1.0\"?>\n" +
            // "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/Math/DTD/mathml2/mathml2.dtd\">";
            "<!DOCTYPE math  PUBLIC \"-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN\" \"<http://www.w3.org/Math/DTD/mathml2/xhtml-math11-f.dtd\">";

        
        String xml = head + mathMl;
        return xml;
    }



    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return AddMathMlResourceAction.class;
    }
}
