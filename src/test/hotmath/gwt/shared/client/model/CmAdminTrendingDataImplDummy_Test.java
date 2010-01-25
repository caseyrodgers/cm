package hotmath.gwt.shared.client.model;

import junit.framework.TestCase;

public class CmAdminTrendingDataImplDummy_Test extends TestCase{
    
    public CmAdminTrendingDataImplDummy_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        CmAdminTrendingDataI tdi = new CmAdminTrendingDataImplDummy();
        for(TrendingData td: tdi.getTrendingData()) {
            assertTrue(td.lessonName != null);
            assertTrue(td.getCountAssigned() > 0);
        }
        
        assertTrue(tdi.getAdminModel() != null);
        assertTrue(tdi.getAdminModel().getSchoolName() != null);
    }
}
