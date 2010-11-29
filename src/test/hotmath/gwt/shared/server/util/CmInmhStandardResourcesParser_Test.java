package hotmath.gwt.shared.server.util;

import java.util.List;

import junit.framework.TestCase;

public class CmInmhStandardResourcesParser_Test extends TestCase {
    
    public CmInmhStandardResourcesParser_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        CmInmhStandardResourcesParser inmhParser = new CmInmhStandardResourcesParser();
        assertNotNull(inmhParser);
    }
    
    public void testRead() throws Exception {
        CmInmhStandardResourcesParser inmhParser = new CmInmhStandardResourcesParser();
        ResourceMenus resourceMenus = inmhParser.getResourceMenu();
        assertTrue(resourceMenus.getResourceMenus().size() > 0);
    }
    
    public void testRead2() throws Exception {
        CmInmhStandardResourcesParser inmhParser = new CmInmhStandardResourcesParser();
        ResourceMenus resourceMenus = inmhParser.getResourceMenu();
        assertTrue(resourceMenus.getMenuFor("flashcard") != null );
    }
    
    public void testRead3() throws Exception {
        CmInmhStandardResourcesParser inmhParser = new CmInmhStandardResourcesParser();
        ResourceMenus resourceMenus = inmhParser.getResourceMenu();
        assertTrue(resourceMenus.getMenuFor("flashcard").getMenuItems().size() > 0);
    }
    
    public void testReadSubMenus() throws Exception {
        CmInmhStandardResourcesParser inmhParser = new CmInmhStandardResourcesParser();
        ResourceMenus resourceMenus = inmhParser.getResourceMenu();
        ResourceMenu menu = resourceMenus.getMenuFor("flashcard");
        List<ResourceMenuItem> subMenus = menu.getMenuItems();
        ResourceMenuItem engSubMenu = subMenus.get(0);
        assertTrue(engSubMenu.getLabel().equals("English"));
        
        ResourceMenuItem fracSubMenu = engSubMenu.getSubMenuItems().get(0);
        assertTrue(fracSubMenu.getLabel().equals("Fractions"));
        
        ResourceMenuItem addingLikeFractions = fracSubMenu.getSubMenuItems().get(0);
        assertTrue(addingLikeFractions.getLabel().equals("Adding Like Fractions"));
    }
}
