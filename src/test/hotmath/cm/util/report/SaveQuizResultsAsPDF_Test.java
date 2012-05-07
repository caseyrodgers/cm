package hotmath.cm.util.report;

import hotmath.cm.server.model.QuizResultsAccessor;
import hotmath.cm.server.model.QuizResultsFileSystemAccessor;
import hotmath.cm.util.service.SaveQuizResultsAsPDF;

import java.util.List;
import java.util.ArrayList;

import junit.framework.TestCase;

/*
 * @author bob
 */

public class SaveQuizResultsAsPDF_Test extends TestCase {
    
    public SaveQuizResultsAsPDF_Test(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
    }
    
    public void testCreatePDF() throws Exception {
    	SaveQuizResultsAsPDF save = new SaveQuizResultsAsPDF();
		save.setAssetsPath("src/main/webapp/assets/images/");
		save.setWebPath("../../workspace/hotmath2/web/");
		
		QuizResultsAccessor accessor = new QuizResultsFileSystemAccessor();
		accessor.setPath("/tmp/");
		save.setQuizResultsAccessor(accessor);
    	save.setRunInSeparateThread(false);
    	save.doIt(272938);
    }

}
