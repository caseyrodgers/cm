package hotmath.cm.util.report;

import hotmath.gwt.cm.server.CmDbTestCase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

public class StudentCCSSReport_Test extends CmDbTestCase {

	static final long MSEC_IN_YEAR = 365L*24L*60L*60L*1000L;

	public StudentCCSSReport_Test(String name) {
		super(name);
	}

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testMakePDF() throws Exception {
    	Date toDate = new Date();
    	Calendar cal = Calendar.getInstance();
    	cal.set(2012, 8, 1);
    	Date fromDate = cal.getTime();
    	
    	StudentCCSSReport report = new StudentCCSSReport("CCSS");
    	ByteArrayOutputStream baos = report.makePdf(6, 9451, fromDate, toDate);
    	assertTrue(baos != null);

		File filePath = new File("/tmp", "student-CCSS-report.pdf");
		FileOutputStream fw = null;
		try {
			 fw = new FileOutputStream(filePath);
			 baos.writeTo(fw);
		 }
		 finally {
			 if (fw != null) fw.close();
			 if (baos != null) baos.close();
		 }
    	
    }
}
