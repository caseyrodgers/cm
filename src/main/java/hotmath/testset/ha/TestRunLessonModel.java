package hotmath.testset.ha;

import java.util.ArrayList;
import java.util.List;

public class TestRunLessonModel {
    
    String lesson;
    String file;
    List<String> pids = new ArrayList<String>();
    

    public List<String> getPids() {
        return pids;
    }

    public void setPids(List<String> pids) {
        this.pids = pids;
    }

    public TestRunLessonModel() {}

    public TestRunLessonModel(String lesson, String file) {
        this.lesson = lesson;
        this.file = file;
    }
    
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
