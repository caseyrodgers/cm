package hotmath.gwt.hm_mobile.server.rpc;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.gwt.hm_mobile.client.model.BookInfoModel;
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.rpc.GetBookInfoAction;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;
import sb.util.SbFile;

public class HmMobileDatabaseWriter {

	public List<BookModel> getBooksAll() throws Exception {

		Connection conn=null;
		PreparedStatement ps=null;
		
		
		List<BookModel> list = new ArrayList<BookModel>();
		
		try {
			conn = HMConnectionPool.getConnection();
			String sql = 
				"select b.TEXTCODE, " +
				"       b.TEXTNAME, " +
				"       b.IMAGEFILE, " +
				"       b.PUBLISHER, " +
				"       b.COPYRIGHT, " +
				"       b.AUTHOR, " +
				"       b.PUBDATE, " +
	            "       b.is_controlled, " +
				"       c.category " +
				"from   BOOKINFO b " +
				"  JOIN BOOKINFO_CATEGORIES c on c.textcode = b.textcode " +
				"where  b.loadorder > 0 " +
				"and EXISTS (select 'x' from SOLUTIONS where booktitle = b.textcode limit 1) " +
				"order  by c.loadorder  ";
			
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
			    boolean isFree = rs.getInt("is_controlled")==0;
			    BookModel bookModel = new BookModel(rs.getString("textcode"), rs.getString("textname"),rs.getString("imagefile"), 
			    		rs.getString("publisher"),rs.getString("copyright"),rs.getString("author"),
			    		rs.getString("pubdate"),rs.getString("category"),isFree);

			    
				BookInfoModel bookInfo = ActionDispatcher.getInstance().execute(new GetBookInfoAction(bookModel));
				bookModel.setMinPageNumber(bookInfo.getMinPageNumber());
				bookModel.setMaxPageNumber(bookInfo.getMaxPageNumber());
			    
			    list.add(bookModel);
			}
		}
		finally {
			SqlUtilities.releaseResources(null, ps, conn);
		}
		return list;
	}
	
	
	public void writeBookProblemIndex() throws Exception {

		Connection conn=null;
		PreparedStatement ps=null;
		
		
		List<BookModel> list = new ArrayList<BookModel>();
		
		try {
			conn = HMConnectionPool.getConnection();
			String sql = 
				"select b.TEXTCODE, " +
				"       b.TEXTNAME, " +
				"       b.IMAGEFILE, " +
				"       b.PUBLISHER, " +
				"       b.COPYRIGHT, " +
				"       b.AUTHOR, " +
				"       b.PUBDATE, " +
	            "       b.is_controlled, " +
				"       c.category " +
				"from   BOOKINFO b " +
				"  JOIN BOOKINFO_CATEGORIES c on c.textcode = b.textcode " +
				"where  b.loadorder > 0 " +
				"and EXISTS (select 'x' from SOLUTIONS where booktitle = b.textcode limit 1) " +
				"order  by c.loadorder  ";
			
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
			    boolean isFree = rs.getInt("is_controlled")==0;
			    BookModel bookModel = new BookModel(rs.getString("textcode"), rs.getString("textname"),rs.getString("imagefile"), 
			    		rs.getString("publisher"),rs.getString("copyright"),rs.getString("author"),
			    		rs.getString("pubdate"),rs.getString("category"),isFree);

			    
				BookInfoModel bookInfo = ActionDispatcher.getInstance().execute(new GetBookInfoAction(bookModel));
				bookModel.setMinPageNumber(bookInfo.getMinPageNumber());
				bookModel.setMaxPageNumber(bookInfo.getMaxPageNumber());
				
				writeBookIndex(conn, bookInfo);
			}
		}
		finally {
			SqlUtilities.releaseResources(null, ps, conn);
		}
	}	
	
	
	class PageNumberWrapper {
		int page;
		String pid;
		public PageNumberWrapper(int page, String pid) {
			this.page = page;
			this.pid = pid;
		}
		public int getPage() {
			return page;
		}
		
		public String getPid() {
			return pid;
		}
	}
	
	private void writeBookIndex(Connection conn, BookInfoModel bookInfo) throws Exception {
		
		List<PageNumberWrapper> pages = new ArrayList<PageNumberWrapper>();
		PreparedStatement ps=null;
		try {
			String sql =
					"select cast(PROBLEMNUMBER as UNSIGNED) as sortnum , pagenumber, problemindex " +
							"from SOLUTIONS " +
							"where booktitle = ? " +
							" order by chaptertitle, problemset, sortnum";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, bookInfo.getBook().getTextCode());
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				pages.add(new PageNumberWrapper(rs.getInt("pagenumber"),rs.getString("problemindex")));
			}
			
			String dir = CatchupMathProperties.getInstance().getCatchupHome() + "/src/main/java/hotmath/gwt/hm_mobile/public/book_data";
			SbFile sbFile = new SbFile(new File(dir,"_data_BOOK_" + bookInfo.getBook().getTextCode().toLowerCase() + ".json"));
			sbFile.setFileContents(new Gson().toJson(pages));
			sbFile.writeFileOut();
		}
		finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}


	static public void main(String as[]) {
		try {
			
			
			new HmMobileDatabaseWriter().writeBookProblemIndex();
			if(true) {
				System.exit(0);
			}
			
			List<BookModel> books = new HmMobileDatabaseWriter().getBooksAll();
			String json = new Gson().toJson(books);
			System.out.println(json);
			
			/** write out each book's problem index */
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
