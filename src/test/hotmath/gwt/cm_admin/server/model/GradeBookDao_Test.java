package hotmath.gwt.cm_admin.server.model;

import hotmath.gwt.cm_tools.client.model.GradeBookModel;

import java.util.List;

import junit.framework.TestCase;

/** Provide DAO access for GradeBook data.
 * 
 * @author casey
 *
 */
public class GradeBookDao_Test extends TestCase {
    
    public GradeBookDao_Test(String name) {
        super(name);
    }
    
    public void testGetData() throws Exception {
        List<GradeBookModel> data = GradeBookDao.getInstance().getGradeBookData(2);
        assertTrue(data.size() > 0);
    }
}
