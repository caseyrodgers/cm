package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.shared.client.model.QuizQuestion;

import com.google.gwt.user.client.Random;

public class QuizQuestionModel  {
    private String lesson;
	private String programName;
	private String question;
	private String questionId;
	private String pid;
	private Integer correctAnswer;

	public QuizQuestionModel(QuizQuestion question) {
        setValues(question);
    }

    private void setValues(QuizQuestion question) {
        this.lesson = question.getLesson();
        this.programName = question.getProgramName();
        this.question = question.getQuizHtml();
        this.questionId = question.getQuestionId();
        this.pid = question.getPid();
        this.correctAnswer = question.getCorrectAnswer();
    }

    
    /** Create a deep copy of an existing model
     * 
     * @param model
     */
    public QuizQuestionModel(QuizQuestionModel model) {
    	
    	// QuizQuestion q = new QuizQuestion(model.getQuestionId(), model.getLesson(),(String) model.getProgram(), model.getPid(), , model.getCorrectAnswer());
    	
    	this.lesson = model.getLesson();
    	this.programName = model.getProgram();
    	this.question = createUniqRadioButtonName(model.getQuestion());
    	this.questionId = model.getQuestionId();
    	this.pid = model.getPid();
    	this.correctAnswer = model.getCorrectAnswer();
    }

    
    /** Update the name='XXX' attribute in the radio buttons that make 
     *  up this question HTML.  That way we can set the value dynamically
     *  via JS without altering any copy.
     *  
     * @param html
     * @return
     */
    private String createUniqRadioButtonName(String html) {
        // html = "before name=\"question_256\" after";
        String newNameTag = "quiz_question_" + Random.nextInt(1000);
        String ret = html.replaceAll("name=\"question_[0-9]*\"", "\"" + newNameTag + "\"");
        return ret;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getLesson() {
        return this.lesson;
    }

    public String getProgram() {
        return this.programName;
    }

    public String getQuestionId() {
        return this.questionId;
    }

    public String getPid() {
        return this.pid;
    }

    public Integer getCorrectAnswer() {
        return this.correctAnswer;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof QuizQuestionModel) {
            return ((QuizQuestionModel)obj).getPid().equals(getPid());
        }
        else {
            return super.equals(obj);
        }
    }
}
