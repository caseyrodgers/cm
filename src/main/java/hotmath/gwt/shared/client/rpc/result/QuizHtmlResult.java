package hotmath.gwt.shared.client.rpc.result;

import hotmath.gwt.shared.client.rpc.Response;

import java.util.List;

public class QuizHtmlResult implements Response {
    String quizHtml;
    int testId;
    int quizSegment;
    int quizSegmentCount;
    String title;
    String subTitle;
    int userId;
    List<Integer> answers;
    
    /** list of indexes that represent the
     *  correct answer for each question.
     * @return
     */
    public List<Integer> getAnswers() {
        return answers;
    }
    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getQuizHtml() {
        return quizHtml;
    }
    public void setQuizHtml(String quizHtml) {
        this.quizHtml = quizHtml;
    }
    public int getTestId() {
        return testId;
    }
    public void setTestId(int testId) {
        this.testId = testId;
    }
    public int getQuizSegment() {
        return quizSegment;
    }
    public void setQuizSegment(int quizSegment) {
        this.quizSegment = quizSegment;
    }
    public int getQuizSegmentCount() {
        return quizSegmentCount;
    }
    public void setQuizSegmentCount(int quizSegmentCount) {
        this.quizSegmentCount = quizSegmentCount;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSubTitle() {
        return subTitle;
    }
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
