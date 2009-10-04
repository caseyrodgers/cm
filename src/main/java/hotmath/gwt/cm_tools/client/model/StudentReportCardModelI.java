package hotmath.gwt.cm_tools.client.model;

import java.util.Date;
import java.util.Map;

public interface StudentReportCardModelI {

    public Integer getStudentUid();

	public void setStudentUid(Integer uid);

	public Date getFirstLoginDate();

	public void setFirstLoginDate(Date date);
    
	public String getInitialProgramDescr();

	public void setInitialProgramDescr(String progDescr);

	public String getCurrentProgramDescr();

	public void setCurrentProgramDescr(String progDescr);

	public Integer getQuizCount();

	public void setQuizCount(Integer count);
	
	public Integer getQuizPassCount();

	public void setQuizPassCount(Integer count);
	
	public void setResourceUsage(Map<String, Integer> map);
	
	public Map<String, Integer> getResourceUsage();
	
	public Integer getAdminUid();
	
	public void setAdminUid(Integer adminUid);
	
}
