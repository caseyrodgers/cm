package hotmath.testset.ha;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestRunLessonModel {
    
    String lesson;
    String file;
    List<String> pids = new ArrayList<String>();
    Date viewDate;
    Date completeDate;


    public TestRunLessonModel(String lesson, String file, Date viewDate, Date completeDate) {
        this.lesson = lesson;
        this.file = file;
        this.viewDate = viewDate;
        this.completeDate = completeDate;
    }
    
    public Boolean getViewed() {
        return viewDate != null;
    }


    public Date getViewDate() {
        return viewDate;
    }

    public void setViewDate(Date viewDate) {
        this.viewDate = viewDate;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    public List<String> getPids() {
        return pids;
    }

    public void setPids(List<String> pids) {
        this.pids = pids;
    }

    public TestRunLessonModel() {}

    
    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
