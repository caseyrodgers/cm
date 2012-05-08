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

	public int getQuizPDFbytesLength() {
		return (quizPDFbytes != null)?quizPDFbytes.length : 0;
	}

	public String toString() {
		return String.format("runId: %d, quizPDFbytes.length(): %d", runId, getQuizPDFbytesLength());
	}
}