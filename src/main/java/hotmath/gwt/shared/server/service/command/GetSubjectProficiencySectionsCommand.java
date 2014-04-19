package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_admin.server.model.CmProgramListingDao;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapter;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSection;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSubject;
import hotmath.gwt.cm_rpc.client.model.program_listing.ProgramType;
import hotmath.gwt.cm_rpc.client.rpc.GetSubjectProficiencySectionsAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;

import java.sql.Connection;
import java.util.List;

/**
 * Provide high level access to subject proficiency section data
 * 
 * @author bob
 * 
 */
public class GetSubjectProficiencySectionsCommand implements ActionHandler<GetSubjectProficiencySectionsAction, CmList<ProgramSection>> {

	CmProgramListingDao dao;
	ProgramChapter chapter;
	ProgramSubject subject;
	
    @Override
    public CmList<ProgramSection> execute(Connection conn, GetSubjectProficiencySectionsAction action) throws Exception {

    	CmList<ProgramSection> sectionList = new CmArrayList<ProgramSection>();

    	dao = new CmProgramListingDao();

    	getSectionList(action.getSubject(), sectionList);

    	for(ProgramSection sect : sectionList) {

    		List<ProgramLesson> lessonList = dao.getLessonsFor(
    				subject.getTestDefId(),
    				sect.getNumber(), chapter.getLabel(), 99);
    		sect.setLessons(lessonList);
    	}
    	return sectionList;
    }

    private void getSectionList(String subjectName, CmList<ProgramSection> sectionList) throws Exception {

    	ProgramListing progListing = dao.getProgramListing();

    	List<ProgramType> typeList = progListing.getProgramTypes();

    	for (ProgramType type : typeList) {
    		if (type.getType().equalsIgnoreCase("PROF")) {
    			List<ProgramSubject> subjList = type.getProgramSubjects();
    			for (ProgramSubject subj : subjList) {
    				subject = subj;
    				if (subj.getName().equalsIgnoreCase(subjectName)) {
    					List<ProgramChapter> chapList = subj.getChapters();
    					for (ProgramChapter chap : chapList){
    						chapter = chap;
    						sectionList.addAll(chap.getSections());
    						// assumes that Proficiency Programs have a single chapter 
    						return;
    					}
    				}
    			}
    		}
    	}
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetSubjectProficiencySectionsAction.class;
    }
}
