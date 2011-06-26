package hotmath.gwt.hm_mobile.client.persist;

import java.util.Map;

import hotmath.gwt.hm_mobile.client.model.BookModel;

import com.google.code.gwt.storage.client.Storage;

public class HmMobilePersistedPropertiesManager {

	static private HmMobilePersistedProperties __instance;

	static final int SEARCH_TERM = 0, TEXTCODE = 1, TITLE = 2, PUBLISHER = 3, COPYRIGHT = 4, IMAGE = 5, PAGENUM = 6,
	        AUTHOR = 7, PUBDATE = 8,CATEGORY = 9;

	static public HmMobilePersistedProperties getInstance() {
		if (__instance == null) {
			if (Storage.isSupported()) {
				String config = Storage.getLocalStorage().getItem("config");
				if (config != null) {
					String p[] = config.split("\\|");
					if (p.length == 10) {
						__instance = new HmMobilePersistedProperties();
						__instance.setSearchTerm(p[SEARCH_TERM]);
						__instance.setLastBook(new BookModel(p[TEXTCODE], p[TITLE], ifn(p[IMAGE]), ifn(p[PUBLISHER]),
						        ifn(p[COPYRIGHT]), ifn(p[AUTHOR]), p[PUBDATE], p[CATEGORY]));
						__instance.getLastBook().setPage(Integer.parseInt(p[PAGENUM]));
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

			}

			if (__instance == null)
				__instance = new HmMobilePersistedProperties();
		}
		return __instance;
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
	
	static public void save() {
		if (Storage.isSupported()) {
			HmMobilePersistedProperties p = __instance;
			BookModel b = p.getLastBook();
			String config = p.getSearchTerm() + "|" + b.getTextCode() + "|" + b.getTitle() + "|" + b.getPublisher()
			        + "|" + b.getCopyRight() + "|" + b.getImage() + "|" + b.getPage() + "|" + b.getAuthor() + "|"
			        + b.getPubDate() + "|" + b.getSubject();
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
		}
	}
}
