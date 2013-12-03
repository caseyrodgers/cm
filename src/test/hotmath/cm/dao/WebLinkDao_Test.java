package hotmath.cm.dao;

import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.AvailableOn;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.LinkViewer;

import java.util.Collection;

import junit.framework.TestCase;

public class WebLinkDao_Test extends TestCase {
    
    public WebLinkDao_Test(String name) {
        super(name);
    }
    
    public void testGet() throws Exception {
        Collection<? extends WebLinkModel> webLinks = WebLinkDao.getInstance().getAllWebLinksDefinedForAdmin(2,  true);
        assertTrue(webLinks.size() > 0);
    }
    
    String kahnUrl="https://www.khanacademy.org/math/geometry/basic-geometry/perimeter_area_tutorial/v/area-of-diagonal-generated-triangles-of-rectangle-are-equal";
    String kahnVideoUrl="https://www.youtube.com/watch?feature=player_embedded&v=5ctsUsvIp8w";
    String kahnUrlAfterConversion="https://www.youtube.com/embed/YTRimTJ5nX4?feature=player_embedded";

    public void testConvertUrl() throws Exception {
        String convertedUrl = WebLinkDao.getInstance().performLinkConversion(kahnUrl);
        assertTrue(convertedUrl.equals(kahnUrlAfterConversion));
    }
    public void testAddKahn() throws Exception {
        WebLinkModel webLink = new WebLinkModel(0, 2, "Kahn Video Test #1",kahnUrl,"Comment", AvailableOn.DESKTOP_ONLY,false,null,null,LinkViewer.INTERNAL, false);
        int linkId = WebLinkDao.getInstance().addWebLink(webLink);
        WebLinkModel newWebLink = WebLinkDao.getInstance().getWebLink(linkId);
        
        assertTrue( newWebLink.getUrl().equals(kahnUrlAfterConversion));
        
    }
    
    public void testAdd() throws Exception {
        String name = "TEST: " + System.currentTimeMillis();
        WebLinkModel webLink = new WebLinkModel(0, 2, name,"http://test.com","Comment", AvailableOn.DESKTOP_AND_MOBILE,false,null,null,LinkViewer.INTERNAL, false);
        int linkId = WebLinkDao.getInstance().addWebLink(webLink);
        
        WebLinkModel newWebLink = WebLinkDao.getInstance().getWebLink(linkId);
        assert(newWebLink.getName().equals(name));
    }
    
    public void testImport() throws Exception {
//        WebLinkModel newWebLink = WebLinkDao.getInstance().getAllWebLinksDefinedForAdmin(2,false).get(0);
//        newWebLink.setName(newWebLink.getName() + ":" + System.currentTimeMillis());
//        int importLinkId = WebLinkDao.getInstance().importWebLink(2, newWebLink);
//        WebLinkModel newLink = WebLinkDao.getInstance().getWebLink(importLinkId);
//        assert(newLink.getName().equals(newWebLink.getName()));
    }


}
