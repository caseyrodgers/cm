package hotmath.gwt.cm_admin.server.model.highlight;

import hotmath.gwt.cm.server.CmDbTestCase;

import java.util.Date;
import java.util.GregorianCalendar;

public class HighLightStatImplGamesPlayed_Test extends CmDbTestCase {
    
    public HighLightStatImplGamesPlayed_Test(String name) {
        super(name);
    }
    
    public void testExecute() throws Exception {
        Date fromDate = new GregorianCalendar(2010,0,0).getTime();
        new HighLightStatImplGamesPlayed().getStatsFromDate(conn, fromDate);
    }

}
