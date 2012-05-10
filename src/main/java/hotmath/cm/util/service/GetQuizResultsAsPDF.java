package hotmath.cm.util.service;

import hotmath.cm.server.model.QuizResultsModel;

import org.apache.log4j.Logger;

/**
 * Get Quiz Results as PDF
 * 
 * @author casey
 *
 */

public class GetQuizResultsAsPDF extends QuizResultsAsPDFBase {

	private static Logger LOGGER = Logger.getLogger(GetQuizResultsAsPDF.class);
	
	public QuizResultsModel getQuizResultsPdfBytes(int runId) throws Exception {
	    return getQuizResultsAccessor().read(runId);
	}
}

