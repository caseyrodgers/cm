package hotmath.cm.util.service;

import hotmath.cm.server.model.QuizResultDatabaseAccessor;
import hotmath.cm.server.model.QuizResultsAccessor;

/**
 * Save Quiz Results as PDF
 * 
 * @author bob
 * 
 */

public class QuizResultsAsPDFBase {

    /**
     * this needs to be shared by the reader/saver
     * 
     * to keep things simple, let's hard-code for now.
     * 
     * 
     */
    public static QuizResultsAccessor __defaultQuizResultsAccessor = new QuizResultDatabaseAccessor();
    private QuizResultsAccessor quizResultsAccessor = __defaultQuizResultsAccessor;

    public QuizResultsAccessor getQuizResultsAccessor() {
        return quizResultsAccessor;
    }

    public void setQuizResultsAccessor(QuizResultsAccessor quizResultsAccessor) {
        this.quizResultsAccessor = quizResultsAccessor;
    }
}
