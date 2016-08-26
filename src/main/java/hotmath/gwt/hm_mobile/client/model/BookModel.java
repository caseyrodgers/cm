package hotmath.gwt.hm_mobile.client.model;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
;

public class BookModel implements Response {

	String textCode, title, image, publisher,copyRight,author,pubDate,subject;
	int maxPageNumber;
	int minPageNumber;
	boolean free;
	int page=0;
	
	public BookModel() {}
	public BookModel(String textCode, String title, String image, String publisher, String copyRight,String author,String pubDate,String subject, boolean isFree) {
		this.textCode = textCode;
		this.title = title;
		this.image = image;
		this.publisher = publisher;
		this.copyRight = copyRight;
		this.author = author;
		this.pubDate = pubDate;
		this.subject = subject;
		this.free = isFree;
	}
	
	public void setMaxPageNumber(int maxPageNumber) {
		this.maxPageNumber = maxPageNumber;
	}
	
	public int getMaxPageNumber() {
		return maxPageNumber;
	}
	
	public void setMinPageNumber(int minPageNumber) {
		this.minPageNumber = minPageNumber;
	}
	
	public int getMinPageNumber() {
		return minPageNumber;
	}
	public boolean isFree() {
        return free;
    }
    public void setFree(boolean free) {
        this.free = free;
    }
    public String getPubDate() {
    	return pubDate;
    }
	public void setPubDate(String pubDate) {
    	this.pubDate = pubDate;
    }
	public BookModel(String textCode) {
		this.textCode = textCode;
	}
	public String getAuthor() {
    	return author;
    }
	public void setAuthor(String author) {
    	this.author = author;
    }
	public String getTextCode() {
    	return textCode;
    }
	public String getCopyRight() {
    	return copyRight;
    }
	public void setCopyRight(String copyRight) {
    	this.copyRight = copyRight;
    }
	public void setTextCode(String textCode) {
    	this.textCode = textCode;
    }
	public String getTitle() {
    	return title;
    }
	public void setTitle(String title) {
    	this.title = title;
    }
	
	public String getImage() {
    	return image;
    }
	public void setImage(String image) {
    	this.image = image;
    }
	public String getPublisher() {
    	return publisher;
    }
	public void setPublisher(String publisher) {
    	this.publisher = publisher;
    }
	
	public int getPage() {
    	return page;
    }
	public void setPage(int page) {
    	this.page = page;
    }
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	@Override
    public String toString() {
        return "BookModel [textCode=" + textCode + ", title=" + title + ", image=" + image + ", publisher=" + publisher + ", copyRight=" + copyRight
                + ", author=" + author + ", pubDate=" + pubDate + ", subject=" + subject + ", free=" + free + ", page=" + page + "]";
    }

}
