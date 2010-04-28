package hotmath.gwt.shared.server.service.command;

import hotmath.cm.test.HaTestSet;

import java.io.Serializable;

public class QuizCacheInfo implements Serializable{

    String quizHtml;
    HaTestSet testSet;
    String subTitle;

    public QuizCacheInfo() {}
    
    public QuizCacheInfo(String quizHtml, HaTestSet testSet, String subTitle) {
        this.quizHtml = quizHtml;
        this.testSet = testSet;
        this.subTitle = subTitle;
    }
    
    public String getQuizHtml() {
        return quizHtml;
    }

    public void setQuizHtml(String quizHtml) {
        this.quizHtml = quizHtml;
    }

    public HaTestSet getTestSet() {
        return testSet;
    }

    public void setTestSet(HaTestSet testSet) {
        this.testSet = testSet;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
