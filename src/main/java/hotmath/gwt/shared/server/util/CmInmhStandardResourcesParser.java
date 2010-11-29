package hotmath.gwt.shared.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;


/** Provide parsing services for the 
 *  
 *  CM Student standard Flash Activiites/Resources
 *  
 *  Definition file consists of XML in following format
 *  
 *  <inmh_resources>
        <inmh_resource type='Math Games' description='Math games that might help you'>
           <menu_item label='Catch The Fly' file='/hotmath_help/games/ctf/ctf_hotmath.swf'/>
           <menu_item label='Number Cop', file='/hotmath_help/games/numbercop/numbercop_hotmath.swf'/>
        </inmh_resource>
       
        <inmh_resource type='typecard' description='Flash Cards and Skill Builders'>
            <sub_menu label='English'>
                <sub_menu label='Fractions'>        
                    <menu_item label='Adding Like Fractions' file='/learning_activities/interactivities/flashcard_addfrac_like.swf'/>
                    <menu_item label='Multiplying a Fraction by an Integer' file='/learning_activities/interactivities/flashcard_multi_frac_int.swf'/>
                </sub_menu>
                
                <menu_item label='Comparing Quantities' file='/learning_activities/interactivities/flashcard_comparing_quantities.swf'/>
            </sub_menu>
        </inmh_resource>
    </inmh_resources>
 *  
 *  
 *  
 * @author casey
 *
 */
public class CmInmhStandardResourcesParser {
    public CmInmhStandardResourcesParser() {
    }
    
    public ResourceMenus getResourceMenu() throws Exception {
        
        String menuXml = readInFile();
        
        ResourceMenus resourceMenu = parseXML(menuXml);   
        
        return resourceMenu;
    }
    
    private ResourceMenus parseXML(String xml) throws SAXException, IOException {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("inmh_resources", "hotmath.gwt.shared.server.util.ResourceMenus");
        
        digester.addObjectCreate("inmh_resources/inmh_resource", "hotmath.gwt.shared.server.util.ResourceMenu");
        digester.addSetProperties("inmh_resources/inmh_resource");
        
        digester.addSetNext("inmh_resources/inmh_resource", "addResourceMenu");
        
        digester.addObjectCreate("inmh_resources/inmh_resource/menu_item", "hotmath.gwt.shared.server.util.ResourceMenuItem");
        digester.addSetProperties("inmh_resources/inmh_resource/menu_item");
        
        digester.addSetNext("inmh_resources/inmh_resource/menu_item", "addMenuItem");
        
        digester.addObjectCreate("inmh_resources/inmh_resource/sub_menu", "hotmath.gwt.shared.server.util.ResourceSubMenu");
        digester.addSetProperties("inmh_resources/inmh_resource/sub_menu");
        digester.addSetNext("inmh_resources/inmh_resource/sub_menu", "addMenuItem");
        
        digester.addObjectCreate("inmh_resources/inmh_resource/sub_menu/sub_menu", "hotmath.gwt.shared.server.util.ResourceSubMenu");
        digester.addSetProperties("inmh_resources/inmh_resource/sub_menu/sub_menu");
        digester.addSetNext("inmh_resources/inmh_resource/sub_menu/sub_menu", "addSubMenuItem");        
        
        digester.addObjectCreate("inmh_resources/inmh_resource/sub_menu/sub_menu/menu_item", "hotmath.gwt.shared.server.util.ResourceMenuItem");
        digester.addSetProperties("inmh_resources/inmh_resource/sub_menu/sub_menu/menu_item");
        digester.addSetNext("inmh_resources/inmh_resource/sub_menu/sub_menu/menu_item", "addSubMenuItem");
        
        StringReader reader = new StringReader(xml);
        Object o = digester.parse(reader);
        return (ResourceMenus) o;
    }
    
    private String readInFile() throws Exception {
        InputStream is = getClass().getResourceAsStream("CmInmhStandardResources.xml");
        if(is == null)
            throw new Exception("Could not load CmInmhStandardResources.xml file");
        
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String resourceMenuXml = null;
        StringBuilder sb = new StringBuilder();
        while ((resourceMenuXml = br.readLine()) != null) {
            sb.append(resourceMenuXml);
        }
        resourceMenuXml = sb.toString();
        
        return resourceMenuXml;
    }
}
