package hotmath.cm.server.model;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.cm.server.model.QuizResultsModel;

public class QuizResultsPDFDao_Test extends CmDbTestCase { 
    
    public QuizResultsPDFDao_Test(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCreateQuizResultsPDF() throws Exception {
    	byte[] bytes = new byte[10];
    	byte x = 1;
    	for (int i=0; i<bytes.length; i++) {
    		bytes[i] = x++;
    	}
    	QuizResultsModel model = new QuizResultsModel();
    	model.setRunId(2);
    	model.setQuizPDFbytes(bytes);
    	QuizResultsPDFDao.getInstance().create(model);
    }
    
    public void testReadQuizResultsPDF() throws Exception {
    	QuizResultsPDFDao dao = QuizResultsPDFDao.getInstance();
    	QuizResultsModel model = dao.read(2);
    	assert(model.getQuizPDFbytes().length == 10);
    }

}
