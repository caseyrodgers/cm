package hotmath.cm.util.service;

import hotmath.cm.server.model.QuizResultDatabaseAccessor;
import hotmath.cm.server.model.QuizResultsAccessor;
import hotmath.cm.server.model.QuizResultsFileSystemAccessor;
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
		save.setWebPath("../hotmath2/web/");
		
		QuizResultsAccessor accessor = new QuizResultsFileSystemAccessor();
		accessor.setPath("/tmp/");
		save.setQuizResultsAccessor(accessor);
    	save.setRunInSeparateThread(false);
    	save.doIt(272938);
    }

    public void testCreatePDFinDB() throws Exception {
    	SaveQuizResultsAsPDF save = new SaveQuizResultsAsPDF();
		save.setAssetsPath("src/main/webapp/assets/images/");
		save.setWebPath("../hotmath2/web/");
		
		QuizResultsAccessor accessor = new QuizResultDatabaseAccessor();
		save.setQuizResultsAccessor(accessor);
    	save.setRunInSeparateThread(false);
    	save.doIt(272938);
    }

}
