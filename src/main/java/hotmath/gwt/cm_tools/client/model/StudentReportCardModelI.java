package hotmath.gwt.cm_tools.client.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StudentReportCardModelI {

	public String getStudentName();
	
	public void setStudentName(String name);
	
    public Integer getStudentUid();

	public void setStudentUid(Integer uid);

	public Date getFirstActivityDate();

	public void setFirstActivityDate(Date date);
    
	public Date getLastActivityDate();

	public void setLastActivityDate(Date date);
    
	public Integer getAdminUid();
	
	public void setAdminUid(Integer adminUid);
	
	public String getGroupName();
	
	public void setGroupName(String name);
	
	public Date getInitialProgramDate();

	public void setInitialProgramDate(Date date);

	public String getInitialProgramName();

	public void setInitialProgramName(String name);

	public String getInitialProgramShortName();

	public void setInitialProgramShortName(String name);

	public String getInitialProgramStatus();

	public void setInitialProgramStatus(String status);

	public Date getLastProgramDate();

	public void setLastProgramDate(Date date);

	public String getLastProgramName();

	public void setLastProgramName(String name);

	public String getLastProgramStatus();

	public void setLastProgramStatus(String status);

	public Integer getQuizAvgPassPercent();

	public void setQuizAvgPassPercent(Integer percent);
	
	public Integer getQuizCount();

	public void setQuizCount(Integer count);
	
	public Integer getQuizPassCount();

	public void setQuizPassCount(Integer count);
	
	public Date getReportEndDate();
	
	public void setReportEndDate(Date date);
	
	public Date getReportStartDate();
	
	public void setReportStartDate(Date date);
	
	public void setResourceUsage(Map<String, Integer> map);
	
	public Map<String, Integer> getResourceUsage();
	
	public List<String> getPrescribedLessonList();
	
	public void setPrescribedLessonList(List<String> list);
	
	public List<StudentProgramReportModelI> getProgramReportList() throws Exception;

	public void setProgramReportList(List<StudentProgramReportModelI> list) throws Exception;
	
}
