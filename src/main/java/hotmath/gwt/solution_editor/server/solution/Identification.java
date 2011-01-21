package hotmath.gwt.solution_editor.server.solution;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;


@Root (name="identification")
public class Identification {
    
    @Attribute
    String book;
        
    @Attribute
    String chapter;
    
    @Attribute 
    String section;
    
    @Attribute 
    String set;
    
    @Attribute (name="problemnumber") 
    String problemNumber;
    
    @Attribute 
    Integer page;
    
    public Identification() {}
    
    public Identification(String book, String chapter, String section, String set, String problemNumber, Integer page) {
        this.book = book;
        this.chapter = chapter;
        this.section = section;
        this.set = set;
        this.problemNumber = problemNumber;
        this.page = page;
    }


    public String getBook() {
        return book;
    }


    public void setBook(String book) {
        this.book = book;
    }


    public String getChapter() {
        return chapter;
    }


    public void setChapter(String chapter) {
        this.chapter = chapter;
    }


    public String getSection() {
        return section;
    }


    public void setSection(String section) {
        this.section = section;
    }


    public String getSet() {
        return set;
    }


    public void setSet(String set) {
        this.set = set;
    }


    public String getProblemNumber() {
        return problemNumber;
    }


    public void setProblemNumber(String problemNumber) {
        this.problemNumber = problemNumber;
    }


    public Integer getPage() {
        return page;
    }


    public void setPage(Integer page) {
        this.page = page;
    }
    
    
    @Override
    public String toString() {
        return (book + "_" + chapter + "_" + section + "_" + set + "_" + problemNumber + "_"  + page).toLowerCase();
    }
}

