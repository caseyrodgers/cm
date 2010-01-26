package hotmath.gwt.shared.client.model;

import hotmath.cm.util.CmAdminTrendingDataFactory;
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
    }
    
    public void testCreateWithFactory() throws Exception {
        CmAdminTrendingDataI tdi = CmAdminTrendingDataFactory.create(CmAdminTrendingDataFactory.TYPE.DUMMY);
        for(TrendingData td: tdi.getTrendingData()) {
            assertTrue(td.lessonName != null);
            assertTrue(td.getCountAssigned() > 0);
        }
        
        assertTrue(tdi.getAdminModel() != null);
        assertTrue(tdi.getAdminModel().getSchoolName() != null);
    }
}
