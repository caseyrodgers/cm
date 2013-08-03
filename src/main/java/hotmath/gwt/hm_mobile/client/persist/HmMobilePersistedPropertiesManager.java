package hotmath.gwt.hm_mobile.client.persist;

import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.model.HmMobileLoginInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.google.code.gwt.storage.client.Storage;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;

public class HmMobilePersistedPropertiesManager {

	static private HmMobilePersistedProperties __instance;

	static final int SEARCH_TERM = 0, TEXTCODE = 1, TITLE = 2, PUBLISHER = 3, COPYRIGHT = 4, IMAGE = 5, PAGENUM = 6,
	        AUTHOR = 7, PUBDATE = 8,CATEGORY = 9,IS_FREE = 10;
	

    public static DateTimeFormat _expiredDateFormat = DateTimeFormat.getFormat("dd-MMM-yyyy");
    
	static public HmMobilePersistedProperties getInstance() {
		if (__instance == null) {
            __instance = new HmMobilePersistedProperties();
			if (Storage.isSupported()) {
				String config = Storage.getLocalStorage().getItem("config");
				if (config != null) {
					String p[] = config.split("\\|");
					if (p.length > 9) {
					    try {
    						__instance.setSearchTerm(p[SEARCH_TERM]);
    						__instance.setLastBook(new BookModel(p[TEXTCODE], p[TITLE], ifn(p[IMAGE]), ifn(p[PUBLISHER]),
    						        ifn(p[COPYRIGHT]), ifn(p[AUTHOR]), p[PUBDATE], p[CATEGORY],p[IS_FREE]=="0"));
    						__instance.getLastBook().setPage(Integer.parseInt(p[PAGENUM]));
					    }
					    catch(Exception e) {
					        Log.error("Error parsing book info", e);
					    }
					}
				}
				
				/** read list of last viewed book page numbers stored as
				 * book=PAGE|book2=PAGE|.etc..
				 */
				String config2 = Storage.getLocalStorage().getItem("book_views");
				if(config2 != null && config2.length() > 0) {
					String p[] = config2.split("\\|");
					String bookPages="";
					for(String bookPagePair: p) {
						String bpp[] = bookPagePair.split("=");
						if(bpp.length==2) {
							try {
								__instance.getBookPages().put(bpp[0],Integer.parseInt(bpp[1]));
							}
							catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				
				
				String loginInfo = Storage.getLocalStorage().getItem("login_info");
				if(loginInfo != null && loginInfo.length() > 0) {
				    __instance.setLoginInfo(new HmMobileLoginInfo(loginInfo));
				}
				else {
				    __instance.setLoginInfo(null);
				}

			}

			if (__instance == null)
				__instance = new HmMobilePersistedProperties();
		}
		return __instance;
	}
	
	/** Set the book and current page number (if page>-1)
	 * 
	 * @param book
	 * @param page
	 */
	static public void setLastBookPlace(BookModel book, int page) {
        getInstance().setLastBook(book);
        if(page > -1) {
        	getInstance().getBookPages().put(book.getTextCode(), page);
        }
        
        save();
	}
	
	static public void setLoginInfo(HmMobileLoginInfo loginInfo) {
	    getInstance().setLoginInfo(loginInfo);
	    save();
	}

	/** Convenience method to set search term and persist.
	 * 
	 * @param term
	 */
	static public void setSearchTerm(String term) {
		try {
			getInstance().setSearchTerm(term);
			save();
		}
		catch(Exception e) {
			Window.alert("There was a problem saving local storage: " + e.getMessage());
		}
	}
	
	/** If string is any type of null return null.
	 *   
	 * @param s
	 * @return
	 */
	static private String ifn(String s) {
		if(s == null || s.length() == 0 || s.equals("null")) {
			return null;
		}
		else {
			return s;
		}
	}
	
	static private void save() {
		if (Storage.isSupported()) {
			HmMobilePersistedProperties p = __instance;
			BookModel b = p.getLastBook();
			String config = p.getSearchTerm() + "|" + b.getTextCode() + "|" + b.getTitle() + "|" + b.getPublisher()
			        + "|" + b.getCopyRight() + "|" + b.getImage() + "|" + b.getPage() + "|" + b.getAuthor() + "|"
			        + b.getPubDate() + "|" + b.getSubject() + "|" + (b.isFree()?"1":"0");
			Storage.getLocalStorage().setItem("config", config);
			
			
			String bookViews = "";
			for(String book: p.getBookPages().keySet()) {
				int page = p.getBookPages().get(book);
				
				if(bookViews.length() > 0) {
					bookViews += "|";
				}
				bookViews += book+"="+page;
			}
			Storage.getLocalStorage().setItem("book_views", bookViews);
			
			
			String loginInfo = p.getLoginInfoTokenized();
			Storage.getLocalStorage().setItem("login_info",  loginInfo);
		}
	}
}
