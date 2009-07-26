package hotmath.testset.ha;

public class ChapterInfo {
    
    int chapterNumber;
    String chapterTitle;
    public int getChapterNumber() {
        return chapterNumber;
    }
    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }
    public String getChapterTitle() {
        return chapterTitle;
    }
    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }
    @Override
    public String toString() {
        return "ChapterInfo [chapterNumber=" + chapterNumber + ", chapterTitle=" + chapterTitle + "]";
    }
}
