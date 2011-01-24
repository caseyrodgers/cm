package hotmath.gwt.cm_admin.server.model.highlight;

import hotmath.gwt.cm.server.CmDbTestCase;

import java.util.Date;
import java.util.GregorianCalendar;

public class HighLightStatImplAvgQuiz_Test extends CmDbTestCase {
    
    public HighLightStatImplAvgQuiz_Test(String name) {
        super(name);
    }
    
    public void testExecute() throws Exception {
        Date fromDate = new GregorianCalendar(2011,0,0).getTime();
        new HighLightStatImplAvgQuiz().getStatsFromDate(conn, fromDate);
    }

}
