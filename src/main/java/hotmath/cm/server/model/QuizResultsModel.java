package hotmath.cm.server.model;

/**
 * 
 * @author bob
 *
 */

public class QuizResultsModel {
	
	int runId;
	
	byte[] quizPDFbytes;

	public int getRunId() {
		return runId;
	}

	public void setRunId(int runId) {
		this.runId = runId;
	}

	public byte[] getQuizPDFbytes() {
		return quizPDFbytes;
	}

	public void setQuizPDFbytes(byte[] quizPDFbytes) {
		this.quizPDFbytes = quizPDFbytes;
	}
}