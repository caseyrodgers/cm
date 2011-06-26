package hotmath.gwt.hm_mobile.server.dao;
import hotmath.gwt.hm_mobile.client.model.BookInfoModel;
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.model.CategoryModel;
import hotmath.gwt.hm_mobile.client.model.ProblemNumber;
import hotmath.spring.SpringManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
;

public class BooksDao extends SimpleJdbcDaoSupport {
	
	private static BooksDao __instance;
	public static BooksDao getInstance() throws Exception {
		if(__instance == null) {
			__instance = (BooksDao)SpringManager.getInstance().getBeanFactory().getBean(BooksDao.class.getName());
		}
		return __instance;
	}
	
	private BooksDao(){
		/* empty */
	}
	
	public List<BookModel> getBooksForCategory(CategoryModel category) throws Exception {
		
		String sql = 
			"select b.TEXTCODE, " +
			"       b.TEXTNAME, " +
			"       b.IMAGEFILE, " +
			"       b.PUBLISHER, " +
			"       b.COPYRIGHT, " +
			"       b.AUTHOR, " +
			"       b.PUBDATE, " +			
			"       c.category " +
			"from   BOOKINFO b " +
			"  JOIN BOOKINFO_CATEGORIES c on c.textcode = b.textcode " +
			"where  b.loadorder > 0 " +
			"and c.category = ? " +
			"and EXISTS (select 'x' from SOLUTIONS where booktitle = b.textcode limit 1) " +
			"order  by c.loadorder  ";

		List<BookModel> list = getJdbcTemplate().query(
				sql,
				new Object[]{category.getCategory()},
				new RowMapper<BookModel>() {
					@Override
					public BookModel mapRow(ResultSet rs, int rowNum) throws SQLException {
					    return new BookModel(rs.getString("textcode"), rs.getString("textname"), 
					    		rs.getString("imagefile"), rs.getString("publisher"), 
					    		rs.getString("copyright"),rs.getString("author"),rs.getString("pubdate"),rs.getString("category"));
					}
				});
		return list;
	}
	
	
	BookInfoModel _lastModel;
	
	public BookInfoModel getBookInfo(final BookModel book) throws Exception {
	    if(_lastModel != null && _lastModel.getBook().getTextCode().equals(book.getTextCode())) {
	    	return _lastModel;
	    }
	    
	    final BookModel fullBookModel;
	    if(book.getTitle() == null)
	        fullBookModel = getBookModel(book.getTextCode());
	    else {
	        fullBookModel = book;
	    }
	    
		String sql = 
                "select min(pagenumber) as minPage, max(pagenumber) as maxPage " +
                " from SOLUTIONS where booktitle = ?";
                BookInfoModel bookInfo = getJdbcTemplate().queryForObject(
				sql,
				new Object[]{book.getTextCode()},
				new RowMapper<BookInfoModel>() {
					@Override
					public BookInfoModel mapRow(ResultSet rs, int rowNum) throws SQLException {
					    return new BookInfoModel(fullBookModel, rs.getInt("minPage"), rs.getInt("maxPage"));
					}
				});
                
                
                
        _lastModel = bookInfo;
                
		return bookInfo;
	}
	
   public BookModel getBookModel(String textCode) throws Exception {
        String sql = 
            "select b.TEXTCODE, " +
            "       b.TEXTNAME, " +
            "       b.IMAGEFILE, " +
            "       b.PUBLISHER, " +
            "       b.COPYRIGHT, " +
            "       b.AUTHOR, " +
            "       b.PUBDATE, " +
            "       c.category " +
            "from   BOOKINFO b " +
            "  JOIN BOOKINFO_CATEGORIES c on c.textcode = b.textcode " +
            " where b.textcode = ?";
        
        BookModel book = getJdbcTemplate().queryForObject(
                sql,
                new Object[]{textCode},
                new RowMapper<BookModel>() {
                    @Override
                    public BookModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new BookModel(rs.getString("textcode"), rs.getString("textname"), 
                                rs.getString("imagefile"), rs.getString("publisher"), 
                                rs.getString("copyright"),rs.getString("author"),rs.getString("pubdate"),rs.getString("category"));
                    }
                });
        return book;
    }
	
	/** Get problem numbers that are around page ...
	 * 
	 *   Gets the problem numbers that are between
	 *   page-1 and page+1
	 *    
	 * @param book
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<ProblemNumber> getProblemNumbers(BookModel book, int page) throws Exception {
		String sql = 
			"select PROBLEMNUMBER, PROBLEMINDEX, PROBLEMSET " +
			" from SOLUTIONS " +
			" where booktitle = ? " +
			" and PAGENUMBER between ? and ? " +
			" order by problemset, CAST(problemnumber AS UNSIGNED)";
		
		List<ProblemNumber> list = getJdbcTemplate().query(
				sql,
				new Object[]{book.getTextCode(),page-1,page+1},
				new RowMapper<ProblemNumber>() {
					@Override
					public ProblemNumber mapRow(ResultSet rs, int rowNum) throws SQLException {
					    return new ProblemNumber(rs.getString("problemnumber"), rs.getString("problemset"),rs.getString("problemindex"));
					}
				});
		return list;
	}
	
	public List<BookModel> searchForBooks(String searchFor) throws Exception {
		String sql = 
			"select b.TEXTCODE, " +
			"       b.TEXTNAME, " +
			"       b.IMAGEFILE, " +
			"       b.PUBLISHER, " +
			"       b.COPYRIGHT, " +
			"       b.AUTHOR, " +
			"       b.PUBDATE, " +			
			"       c.category " +
			"from   BOOKINFO b " +
			"  JOIN BOOKINFO_CATEGORIES c on c.textcode = b.textcode " +
			"where  EXISTS (select 'x' from SOLUTIONS where booktitle = b.textcode limit 1) " +
			"AND ( " +
			"        b.textname like ? " +
			"        or publisher like ? " +
			"        or pubdate like ? " +
			"        or ISBN like ? " +
			"        or b.textcode like ? " +
			"        or author like ? " +
			")        " +
			"order  by b.textname, " +
			"          b.publisher, " +
			"          b.pubdate ";

		
		String sf = "%" + searchFor + "%";
		List<BookModel> list = getJdbcTemplate().query(
				sql,
				new Object[]{sf,sf,sf,sf,sf,sf},
				new RowMapper<BookModel>() {
					@Override
					public BookModel mapRow(ResultSet rs, int rowNum) throws SQLException {
					    return new BookModel(rs.getString("textcode"), rs.getString("textname"), 
					    		rs.getString("imagefile"), rs.getString("publisher"), 
					    		rs.getString("copyright"),rs.getString("author"),rs.getString("pubdate"),rs.getString("category"));
					}
				});
		return list;
	}
}
