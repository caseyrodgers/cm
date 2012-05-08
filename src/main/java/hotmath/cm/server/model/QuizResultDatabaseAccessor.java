package hotmath.cm.server.model;

/**
 * 
 * @author bob
 *
 */
public class QuizResultDatabaseAccessor extends QuizResultsAccessor {

	@Override
	public void save(int runId, byte[] quizPDFbytes) throws Exception {
        QuizResultsPDFDao dao = QuizResultsPDFDao.getInstance();
        QuizResultsModel model = new QuizResultsModel();
        model.setRunId(runId);
        model.setQuizPDFbytes(quizPDFbytes);
        dao.create(model);
	}

	@Override
	public QuizResultsModel read(int runId) throws Exception {
        QuizResultsPDFDao dao = QuizResultsPDFDao.getInstance();
        return dao.read(runId);
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

}
