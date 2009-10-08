package hotmath.gwt.cm_tools.client.model;

import java.util.Date;
import java.util.Map;

public interface StudentProgramReportModelI {

    public Integer getStudentUid();

	public void setStudentUid(Integer uid);

	public Date getAssignedDate();
	
	public void setAssignedDate(Date date);
	
	public Date getFirstLoginDate();

	public void setFirstLoginDate(Date date);
    
	public Date getLastLoginDate();

	public void setLastLoginDate(Date date);
    
	public String getProgramDescr();

	public void setProgramDescr(String progDescr);

	public String getProgramStatus();

	public void setProgramStatus(String progDescr);

	public Integer getQuizCount();

	public void setQuizCount(Integer count);
	
	public Integer getQuizPassCount();

	public void setQuizPassCount(Integer count);
	
	public void setResourceUsage(Map<String, Integer> map);
	
	public Map<String, Integer> getResourceUsage();
	
}
