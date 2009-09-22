package hotmath.testset.ha;

public class TestRunLessonModel {
    
    String lesson;
    String file;
    

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
