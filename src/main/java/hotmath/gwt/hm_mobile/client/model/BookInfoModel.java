package hotmath.gwt.hm_mobile.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class BookInfoModel implements Response {
	int minPageNumber;
	int maxPageNumber;
	BookModel book;
	
	
	public BookInfoModel() {}
	
	public BookInfoModel(BookModel book, int minPage, int maxPage) {
		this.book = book;
		this.minPageNumber = minPage;
		this.maxPageNumber = maxPage;
	}

	public int getMinPageNumber() {
    	return minPageNumber;
    }

	public void setMinPageNumber(int minPageNumber) {
    	this.minPageNumber = minPageNumber;
    }

	public int getMaxPageNumber() {
    	return maxPageNumber;
    }

	public void setMaxPageNumber(int maxPageNumber) {
    	this.maxPageNumber = maxPageNumber;
    }

	public BookModel getBook() {
        return book;
    }

    public void setBook(BookModel book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return "BookInfoModel [minPageNumber=" + minPageNumber + ", maxPageNumber=" + maxPageNumber + ", book=" + book
                + "]";
    }
}
