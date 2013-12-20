package hotmath.cm.dao;

import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.AvailableOn;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.LinkViewer;

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

public class WebLinkDao_Test extends TestCase {
    
    public WebLinkDao_Test(String name) {
        super(name);
    }
    
    public void testGet() throws Exception {
        Collection<? extends WebLinkModel> webLinks = WebLinkDao.getInstance().getAllWebLinksDefinedForAdminPublic(2);
        assertTrue(webLinks.size() > 0);
    }

    String youtubeUrl="http://www.youtube.com/watch?v=3H7385duSpA";
    String youtubeUrlEmbedded="https://www.youtube.com/embed/3H7385duSpA?feature=player_embedded";
    public void testAddYouTube() throws Exception {
        String convertedUrl = WebLinkDao.getInstance().performLinkConversion(youtubeUrl);
        assertTrue(convertedUrl.equals(youtubeUrlEmbedded));
    }

    
    String kahnUrl="https://www.khanacademy.org/math/geometry/basic-geometry/perimeter_area_tutorial/v/area-of-diagonal-generated-triangles-of-rectangle-are-equal";
    String kahnVideoUrl="https://www.youtube.com/watch?feature=player_embedded&v=5ctsUsvIp8w";
    String kahnUrlAfterConversion="https://www.youtube.com/embed/YTRimTJ5nX4?feature=player_embedded";
    public void testAddKahn1() throws Exception {
        String convertedUrl = WebLinkDao.getInstance().performLinkConversion(kahnUrl);
        assertTrue(convertedUrl.equals(kahnUrlAfterConversion));
    }
    public void testAddKahn2() throws Exception {
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
        @SuppressWarnings("unchecked")
        List<WebLinkModel> links = (List<WebLinkModel>)WebLinkDao.getInstance().getAllWebLinksDefinedForAdminPublic(2);
        WebLinkModel link = links.get(0);
        link.setName(link.getName() + ":" + System.currentTimeMillis());
        int importLinkId = WebLinkDao.getInstance().importWebLink(2, link);
        WebLinkModel newLink = WebLinkDao.getInstance().getWebLink(importLinkId);
        assert(newLink.getName().equals(link.getName()));
    }


}
