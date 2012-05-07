package hotmath.cm.server.model;

/**
 * 
 * @author bob
 *
 */

public abstract class QuizResultsAccessor {
	
	public abstract void save(int runId, byte[] quizPDFbytes) throws Exception;
	
	public abstract QuizResultsModel read(int runId) throws Exception;
	
	public abstract void delete(int runId) throws Exception;
	
	public abstract QuizResultsModel update(int runId, byte[] quizPDFbytes) throws Exception;

	public void setPath(String path) {
		// empty impl
	}
}
