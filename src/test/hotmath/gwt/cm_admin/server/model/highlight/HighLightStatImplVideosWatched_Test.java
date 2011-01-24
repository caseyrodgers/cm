package hotmath.gwt.cm_admin.server.model.highlight;

import hotmath.gwt.cm.server.CmDbTestCase;

import java.util.Date;
import java.util.GregorianCalendar;

public class HighLightStatImplVideosWatched_Test extends CmDbTestCase {
    
    public HighLightStatImplVideosWatched_Test(String name) {
        super(name);
    }
    
    public void testExecute() throws Exception {
        Date fromDate = new GregorianCalendar(2011,0,0).getTime();
        new HighLightStatImplVideosWatched().getStatsFromDate(conn, fromDate);
    }

}
