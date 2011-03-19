package hotmath.testset.ha;

import hotmath.gwt.cm_tools.client.model.StudentProgramReportModelI;
import hotmath.gwt.cm_tools.client.model.StudentReportCardModelI;
import hotmath.testset.ha.info.UserInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class StudentReportCardModel2 implements StudentReportCardModelI {

    public Integer getAdminUid() {
        return adminUid;
    }

    public void setAdminUid(Integer adminUid) {
        this.adminUid = adminUid;
    }


    List<UserInfo> _userInfo;
    Integer adminUid;
    String groupName;
    String studentName;
    Date firstActivityDate;
    
    
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public StudentReportCardModel2() throws Exception {
    }
    
    
  
    @Override
    public Date getFirstActivityDate() {
        return firstActivityDate;
    }
    

    @Override
    public void setFirstActivityDate(Date date) {
        this.firstActivityDate=date;
    }
    

    @Override
    public String getGroupName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getInitialProgramDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getInitialProgramName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getInitialProgramStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getLastActivityDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getLastProgramDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLastProgramName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLastProgramStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getPrescribedLessonList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<StudentProgramReportModelI> getProgramReportList() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getQuizAvgPassPercent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getQuizCount() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getQuizPassCount() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getReportEndDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getReportStartDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Integer> getResourceUsage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getStudentUid() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGroupName(String name) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setInitialProgramDate(Date date) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setInitialProgramName(String name) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setInitialProgramStatus(String status) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLastActivityDate(Date date) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLastProgramDate(Date date) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLastProgramName(String name) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLastProgramStatus(String status) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setPrescribedLessonList(List<String> list) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setProgramReportList(List<StudentProgramReportModelI> list) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setQuizAvgPassPercent(Integer percent) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setQuizCount(Integer count) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setQuizPassCount(Integer count) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setReportEndDate(Date date) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setReportStartDate(Date date) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setResourceUsage(Map<String, Integer> map) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setStudentUid(Integer uid) {
        // TODO Auto-generated method stub
        
    }
    
}
