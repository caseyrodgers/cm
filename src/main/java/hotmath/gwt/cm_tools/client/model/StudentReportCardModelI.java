package hotmath.gwt.cm_tools.client.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StudentReportCardModelI {

    public Integer getStudentUid();

	public void setStudentUid(Integer uid);

	public Date getFirstLoginDate();

	public void setFirstLoginDate(Date date);
    
	public Date getLastLoginDate();

	public void setLastLoginDate(Date date);
    
	public Integer getAdminUid();
	
	public void setAdminUid(Integer adminUid);
	
	public String getInitialProgramName();

	public void setInitialProgramName(String name);

	public String getInitialProgramStatus();

	public void setInitialProgramStatus(String status);

	public String getCurrentProgramName();

	public void setCurrentProgramName(String name);

	public String getCurrentProgramStatus();

	public void setCurrentProgramStatus(String status);

	public Integer getQuizCount();

	public void setQuizCount(Integer count);
	
	public Integer getQuizPassCount();

	public void setQuizPassCount(Integer count);
	
	public void setResourceUsage(Map<String, Integer> map);
	
	public Map<String, Integer> getResourceUsage();
	
	public List<StudentProgramReportModelI> getProgramReportList();

	public void setProgramReportList(List<StudentProgramReportModelI> list);
	
}
