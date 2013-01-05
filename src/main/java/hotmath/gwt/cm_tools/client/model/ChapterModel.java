package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class ChapterModel implements Response {

    String number;
    String title;
    String chapter;
    String styleIsFree;

    public ChapterModel() {
    }

    public ChapterModel(String number, String title) {
        this.number = number;
        this.title = title;
        this.chapter = number + " " + title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getStyleIsFree() {
        return styleIsFree;
    }

    public void setStyleIsFree(String styleIsFree) {
        this.styleIsFree = styleIsFree;
    }

}
