package hotmath.gwt.cm_admin.server.model.highlight;

import hotmath.gwt.cm.server.CmDbTestCase;

import java.util.Date;
import java.util.GregorianCalendar;

public class HighLightStatImplAvgLoginsWeek_Test extends CmDbTestCase {
    
    public HighLightStatImplAvgLoginsWeek_Test(String name) {
        super(name);
    }
    
    public void testExecute() throws Exception {
        Date fromDate = new GregorianCalendar(2011,0,0).getTime();
        HighLightStatImplAvgLoginsWeek hq = new HighLightStatImplAvgLoginsWeek();
        hq.setStatName("logins_week");
        hq.getStatsFromDate(conn, fromDate, null);
    }

}
