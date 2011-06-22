package hotmath.gwt.hm_mobile.client.persist;

import hotmath.gwt.hm_mobile.client.model.BookModel;

public class HmMobilePersistedProperties {

    String searchTerm = "";
    BookModel lastBook = new BookModel();

    public HmMobilePersistedProperties() {
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public BookModel getLastBook() {
        return lastBook;
    }

    public void setLastBook(BookModel lastBook) {
        this.lastBook = lastBook;
    }

    @Override
    public String toString() {
        return "HmMobilePersistedProperties [searchTerm=" + searchTerm + ", lastBook=" + lastBook + "]";
    }
}
