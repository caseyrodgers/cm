package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class TeacherIdentity implements Response {
    private int adminId;
    private int teacherId;
    String teacherName;
    static final int UNKNOWN = -1;
    public TeacherIdentity() {}
    public TeacherIdentity(int adminId, String teacherName, int teacherId) {
        this.adminId = adminId;
        this.teacherName = teacherName;
        this.teacherId = teacherId;
    }
    
    public TeacherIdentity(String serializedTeacher) {
        if(serializedTeacher != null) {
            String p[] = serializedTeacher.split("\\|");
            this.adminId = Integer.parseInt(p[0]);
            this.teacherId = Integer.parseInt(p[1]);
            this.teacherName = p[2];
        }
    }
    
    public boolean isUnknown() {
        return teacherId == UNKNOWN;
    }

    public int getTeacherId() {
        return teacherId;
    }
    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }
    public int getAdminId() {
        return adminId;
    }
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    public String getTeacherName() {
        return teacherName;
    }
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    
    public static TeacherIdentity getUnknownTeacher() {
        return new TeacherIdentity(UserInfoBase.getInstance().getUid(),"Unknown Teacher", UNKNOWN);
    }
}
