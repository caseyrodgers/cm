package hotmath.cm.dao;

import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.AvailableOn;

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
    
    public void testAdd() throws Exception {
        String name = "TEST: " + System.currentTimeMillis();
        WebLinkModel webLink = new WebLinkModel(0, 2, name,"http://test.com","Comment", AvailableOn.DESKTOP_AND_MOBILE,false,null);
        int linkId = WebLinkDao.getInstance().addWebLink(webLink);
        
        WebLinkModel newWebLink = WebLinkDao.getInstance().getWebLink(linkId);
        assert(newWebLink.getName().equals(name));
    }
    
    public void testImport() throws Exception {
        WebLinkModel newWebLink = WebLinkDao.getInstance().getWebLink(1);
        newWebLink.setName(newWebLink.getName() + ":" + System.currentTimeMillis());
        int importLinkId = WebLinkDao.getInstance().importWebLink(2, newWebLink);
        WebLinkModel newLink = WebLinkDao.getInstance().getWebLink(importLinkId);
        assert(newLink.getName().equals(newWebLink.getName()));
    }


}
