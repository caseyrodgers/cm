package hotmath.gwt.hm_mobile.client.persist;

import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.model.HmMobileLoginInfo;

import java.util.HashMap;
import java.util.Map;

public class HmMobilePersistedProperties {

    String searchTerm = "";
    BookModel lastBook = new BookModel();
    Map<String,Integer> bookPages = new HashMap<String, Integer>();
    private HmMobileLoginInfo loginInfo;

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

    public Map<String,Integer> getBookPages() {
		return bookPages;
	}

	public void setBookPages(Map<String,Integer> bookPages) {
		this.bookPages = bookPages;
	}

    public HmMobileLoginInfo getLoginInfo() {
        return  loginInfo;
    }

    public void setLoginInfo(HmMobileLoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    @Override
    public String toString() {
        return "HmMobilePersistedProperties [searchTerm=" + searchTerm + ", lastBook=" + lastBook + ", bookPages=" + bookPages + ", loginInfo=" + loginInfo
                + "]";
    }

    public String getLoginInfoTokenized() {
        if(loginInfo == null) {
            return "";
        }
        else {
            return loginInfo.getUser() + "|" + loginInfo.getPassword() + "|" + loginInfo.getAccountType() + "|" + HmMobilePersistedPropertiesManager._expiredDateFormat.format(loginInfo.getDateExpired());
        }
    }
}
