package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;


/** An  model for representing a single item in a custom program.
 *
 * Represents a single lesson in a custom program.
 *
 * @author casey
 *
 */
public class CustomLessonModel implements Response {

    static public enum Type{LESSON,QUIZ};

    Type customProgramType;

    String subjectStyleClass;

	private String customProgramItemClass;

	private Integer quidId;

	private String customProgramItem;

	private String subject;

	private String file;

	private Boolean isAnswersViewable;

	private Boolean isInUse;

	private Boolean isArchived;

	private String archiveDate;

	private String styleName;

    public CustomLessonModel(){/** empty */}

    public CustomLessonModel(String lesson, String file, String subject) {
        this.customProgramType = Type.LESSON;

        setLesson(lesson);

        if(subject == null || subject.length() == 0)
            subject = "Alg 1";

        setFile(file);
        setSubject(subject);
    }

    public CustomLessonModel(int quizId, String quizLabel, boolean isAnswersViewable, boolean isInUse,
    		boolean isArchived, String archiveDate) {
        this.customProgramType = Type.QUIZ;
        setQuiz(quizLabel);
        setQuizId(quizId);
        setIsAnswersViewable(isAnswersViewable);
        setIsInUse(isInUse);
        setIsArchived(isArchived);
        setArchiveDate(archiveDate);
        
        if(isArchived)
        	setStyleName("custom-archived");

        this.subjectStyleClass = "is_quiz_subject";
        this.customProgramItemClass = "is_quiz";
    }
    
    public String getSubjectStyleClass() {
		return subjectStyleClass;
	}

	public String getCustomProgramItemClass() {
		return customProgramItemClass;
	}

	public String getLabel() {
    	if(customProgramType == Type.LESSON) {
    		return getLesson();
    	}
    	else {
    		return getQuiz();
    	}
    	
    }
    public String getId() {
    	if(customProgramType == Type.LESSON) {
    		return "LESSON-" + getLesson();
    	}
    	else {
    		return "QUIZ-" + getQuizId();
    	}
    }
    
    public void setQuizId(Integer quizId) {
    	this.quidId = quizId;
    }

    public Integer getQuizId() {
        return this.quidId;
    }

    public void setQuiz(String name) {
        setCustomProgramItem(name);
    }

    public String getCustomProgramItem() {
        return this.customProgramItem;
    }

    public void setCustomProgramItem(String item) {
        this.customProgramItem = item;
    }

    public String getQuiz() {
        return getCustomProgramItem();
    }

    public void setLesson(String lesson) {
        setCustomProgramItem(lesson);
    }

    public String getLesson() {
        return getCustomProgramItem();
    }

    public void setSubject(String subject) {
        this.subject = subject;
        
        /** used to set a style for this lesson when
         *  displayed via the CustomPrescriptionDialog list
         */
        this.subjectStyleClass = subject.toLowerCase().replace(" " ,"");
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return this.file;
    }

    public Type getCustomProgramType() {
        return customProgramType;
    }

    public void setCustomProgramType(Type customProgramType) {
        this.customProgramType = customProgramType;
    }


    public String getSubject() {
        return this.subject;
    }

    public Boolean getIsAnswersViewable() {
    	return this.isAnswersViewable;
    }

    public void setIsAnswersViewable(Boolean isAnswersViewable) {
    	this.isAnswersViewable = isAnswersViewable;
    }

    public Boolean getIsInUse() {
    	return this.isInUse;
    }

    public void setIsInUse(Boolean isInUse) {
    	this.isInUse = isInUse;
    }

    public Boolean getIsArchived() {
    	return this.isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
    	this.isArchived = isArchived;
    }

    public String getArchiveDate() {
    	return this.archiveDate;
    }

    public void setArchiveDate(String archiveDate) {
    	this.archiveDate = archiveDate;
    }

    public String getStyleName() {
        return this.styleName;
    }
    
    public void setStyleName(String style) {
        this.styleName = style;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CustomLessonModel) {
            CustomLessonModel clm = (CustomLessonModel)obj;

            if(clm.getCustomProgramType() == Type.LESSON && clm.getFile().equals(getFile())) {
                return true;
            }
            else if(clm.getCustomProgramType() == Type.QUIZ && (clm.getQuiz() != null && clm.getQuiz().equals(getQuiz()))) {
                return true;
            }
            else
                return false;
        }
        else {
            return super.equals(obj);
        }
    }
    
    
    @Override
    public String toString() {
        return getFile();
    }
}
