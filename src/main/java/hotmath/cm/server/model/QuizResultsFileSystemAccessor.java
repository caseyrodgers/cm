package hotmath.cm.server.model;

import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 
 * @author bob
 *
 */

public class QuizResultsFileSystemAccessor extends QuizResultsAccessor {

	private static final Logger LOGGER = Logger.getLogger(QuizResultsFileSystemAccessor.class);
	private String path;
	
	@Override
	public void save(int runId, byte[] quizPDFbytes) throws Exception {
		OutputStream os = null;
		try {
            os = new FileOutputStream(getPath() + "/quiz-" + runId + ".pdf");
            os.write(quizPDFbytes);
		    os.flush();
		}
		catch (Exception e) {
			LOGGER.error("ERROR saving Quiz for runId: " + runId, e);
			throw e;
		}
        finally {
        	if (os != null) os.close();
        }
	}

	@Override
	public QuizResultsModel read(int runId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(int runId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public QuizResultsModel update(int runId, byte[] quizPDFbytes)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
