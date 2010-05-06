package hotmath.gwt.cm_admin.server.model;

import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapter;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapterAll;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSubject;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramType;
import hotmath.testset.ha.CmProgram;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;

import org.apache.log4j.Logger;

public class CmProgramListingDao {

    private static final Logger logger = Logger.getLogger(CmProgramListingDao.class);

    public CmProgramListingDao() {
    }

    public ProgramListing getProgramListing(final Connection conn, int adminId) throws Exception {

        try {
            ProgramListing pr = new ProgramListing();
            //pr.getProgramTypes().add(createProgramType(conn, CmProgram.ALG1_PROF.getProgramType()));
            pr.getProgramTypes().add(createProgramType(conn, "Proficiency"));

            return pr;
        } finally {
            SqlUtilities.releaseResources(null, null, null);
        }
    }

    private ProgramType createProgramType(Connection conn, String type) throws Exception {
        ProgramType programType = new ProgramType(type);

        String[] subjects = { "Pre-Algebra", "Algebra 1", "Algebra 2", "Geometry" };

        for (String subj : subjects) {
        	ProgramSubject ps = new ProgramSubject();
        	ps.setName(subj);
            programType.getProgramSubjects().add(ps);
            
            ProgramChapter progChap = new ProgramChapterAll();
            ps.getChapters().add(progChap);
            for(int i=0;i<30;i++) {
                progChap.getLessons().add(new ProgramLesson("Lesson #" + i));
            }
        	
        }
        return programType;
    }
}
