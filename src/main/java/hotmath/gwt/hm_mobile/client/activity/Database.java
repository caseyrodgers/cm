package hotmath.gwt.hm_mobile.client.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.hm_mobile.client.model.BookInfoModel;
import hotmath.gwt.hm_mobile.client.model.BookModel;

public class Database {

	static interface Callback {
		void dbLoaded(Database database);
	}

	static private Database __instance;

	public static Database getInstance(Callback callback) {
		if (__instance == null) {
			__instance = new Database(callback);
		} else {
			callback.dbLoaded(__instance);
		}
		return __instance;
	}

	List<BookModel> _books = new ArrayList<BookModel>();

	private Database(final Callback callback) {

		String url = "_data_BOOKINFO.json";
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

		try {
			Request response = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					System.out.println("Error reading json: " + exception);
				}

				public void onResponseReceived(Request request, Response response) {
					String json = response.getText();
					_jsni_initializeBooks(json);

					int cnt = _jsni_getBookCount();
					for (int i = 0; i < cnt; i++) {
						BookModelWrapper bookWrapper = _jsni_getBookAt(i);
						BookModel book = new BookModel(bookWrapper.getTextCode());
						book.setSubject(bookWrapper.getSubject());
						book.setTextCode(bookWrapper.getTextCode());
						book.setTitle(bookWrapper.getTitle());
						book.setImage(bookWrapper.getImage());
						book.setPublisher(bookWrapper.getPublisher());
						book.setCopyRight(bookWrapper.getCopyRight());
						book.setAuthor(bookWrapper.getAuthor());
						book.setFree(bookWrapper.getFree());
						book.setPage(bookWrapper.getPage());
						book.setMinPageNumber(bookWrapper.getMinPageNumber());
						book.setMaxPageNumber(bookWrapper.getMaxPageNumber());
						_books.add(book);
					}

					callback.dbLoaded(Database.this);
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}

	native private BookModelWrapper _jsni_getBookAt(int i) /*-{
															return $wnd.Database.getBookAt(i);
															}-*/;

	native private int _jsni_getBookCount() /*-{
											return $wnd.Database.getBookCount();
											}-*/;

	native private void _jsni_initializeBooks(String json) /*-{
															$wnd.Database.initializeBooks(json);
															}-*/;

	native private JsArray<BookProblemWrapper> _jsni_initializeBookIndex(String textCode, String json) /*-{
																									return $wnd.Database.initializeBookIndex(textCode, json);
																									}-*/;

	public CmList<BookModel> getBooksInCategories(String subject) {
		CmList<BookModel> books = new CmArrayList<BookModel>();
		for (BookModel bmw : _books) {
			if (bmw.getSubject().equals(subject)) {
				books.add(bmw);
			}
		}
		return books;
	}

	// Use JSNI to grab the JSON object we care about
	// The JSON object gets its Java type implicitly
	// based on the method's return type
	private native BookModelWrapper getBookAt(int which) /*-{
															return $wnd.Database.getBookModel(which);
															}-*/;

	public BookInfoModel getBookInfo(String textCode) {
		for (int i = 0; i < this._books.size(); i++) {
			BookModel bi = this._books.get(i);
			if (bi.getTextCode().equals(textCode)) {
				return new BookInfoModel(bi, bi.getMinPageNumber(), bi.getMaxPageNumber());
			}
		}
		return null;
	}

	static public interface CallbackBook {
		void bookLoaded(BookIndex loaded);
	}

	public void getBookIndex(String textCode, int page, CallbackBook callback) {
		getBookIndexFor(textCode, callback);
	}

	Map<String, BookIndex> bookIndexes = new HashMap<String, BookIndex>();
	private void getBookIndexFor(final String textCode, final CallbackBook callback) {
		
		/** check cache */
		if(bookIndexes.containsKey(textCode)) {
			callback.bookLoaded(bookIndexes.get(textCode));
			return;
		}

		
		/** process from server */
		String url = "book_data/_data_BOOK_" + textCode.toLowerCase() + ".json";
		 RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		    try {
		      builder.sendRequest(null, new RequestCallback() {
		        public void onError(Request request, Throwable exception) {
		          System.out.println("Error reading json: " + exception);
		        }

		        public void onResponseReceived(Request request, Response response) {
		        	String json = response.getText();
		        	
		        	JsArray<BookProblemWrapper> bookIndexWrappers = _jsni_initializeBookIndex(textCode, json);
		        	BookIndex bookIndex = new BookIndex(new BookModel(textCode));
		        	bookIndex.getProblemNumbers().clear();
					for(int i=0;i<bookIndexWrappers.length();i++) {
						bookIndex.getProblemNumbers().add(bookIndexWrappers.get(i));		
		        	}
					
					bookIndexes.put(textCode,  bookIndex);
		        	callback.bookLoaded(bookIndex);
		        }
		      });
		    } catch (RequestException e) {
		      e.printStackTrace();
		    }
		callback.bookLoaded(null);
	}
	
	static public class BookProblemWrapper extends JavaScriptObject {
		protected BookProblemWrapper() {
		}
		public final native String getPid() /*-{ return this.pid; }-*/;
		public final native int getPage() /*-{ return this.page; }-*/;
	}

	static class BookModelWrapper extends JavaScriptObject {
		protected BookModelWrapper() {
		}

		public final native String getTextCode() /*-{ return this.textCode; }-*/;

		public final native String getTitle() /*-{ return this.title; }-*/;

		public final native String getImage() /*-{ return this.image; }-*/;

		public final native String getPublisher() /*-{ return this.publisher; }-*/;

		public final native String getCopyRight() /*-{ return this.copyRight; }-*/;

		public final native String getAuthor() /*-{ return this.author; }-*/;

		public final native String getSubject() /*-{ return this.subject; }-*/;

		public final native boolean getFree() /*-{ return true; }-*/;

		public final native int getPage() /*-{ return this.page; }-*/;

		public final native int getMinPageNumber() /*-{ return this.minPageNumber; }-*/;

		public final native int getMaxPageNumber() /*-{ return this.maxPageNumber; }-*/;
	}

}
