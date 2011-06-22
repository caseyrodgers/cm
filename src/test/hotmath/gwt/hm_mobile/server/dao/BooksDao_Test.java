package hotmath.gwt.hm_mobile.server.dao;


import hotmath.gwt.hm_mobile.client.model.BookInfoModel;
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.model.ProblemNumber;

import java.util.List;

import junit.framework.TestCase;

public class BooksDao_Test extends TestCase {
    
    public BooksDao_Test(String name) {
        super(name);
    }

    BookModel book = new BookModel("brown97");

    public void testSearchBook() throws Exception {
        List<BookModel> books = BooksDao.getInstance().searchForBooks("test");
        assertTrue(books.size() > 0 && books.get(0).getTextCode() != null);
    }
    public void testProblemNumbers() throws Exception {
        List<ProblemNumber> list = BooksDao.getInstance().getProblemNumbers(book,200);
        assertTrue(list.size() > 0);
    }
    
    public void testGetBookInfo() throws Exception {
        BookInfoModel bim = BooksDao.getInstance().getBookInfo(book);
        assertTrue(bim.getMaxPageNumber() > 0);
    }

}
