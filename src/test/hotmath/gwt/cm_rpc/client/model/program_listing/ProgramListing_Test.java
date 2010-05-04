package hotmath.gwt.cm_rpc.client.model.program_listing;

import junit.framework.TestCase;

public class ProgramListing_Test extends TestCase {
    
    public ProgramListing_Test(String name) {
        super(name);
    }

    public void testCreate() throws Exception {
        ProgramListing pr = new ProgramListing();
        assertTrue(pr.getProgramTypes() != null);
    }
    
    public void testCreateTypes() throws Exception {
        ProgramListing pr = new ProgramListing();
        for(ProgramType type: pr.getProgramTypes()) {
            assertTrue(type.getProgramChapters() != null);
        }
    }
    
    public void testCreateLessons() throws Exception {
        ProgramListing pr = new ProgramListing();
        for(ProgramType type: pr.getProgramTypes()) {
            for(ProgramChapter chapters:type.getProgramChapters()) {
                assertTrue(chapters.getLessons() != null);
            }

        }
    }
}
