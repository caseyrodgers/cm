package hotmath.testset.ha.info;

import hotmath.testset.ha.HaUser;

import java.util.List;

/** Represents a user and all its user program information
 * 
 * @author casey
 *
 */
public class UserInfo {

    HaUser user;
    List<UserProgramInfo> programs;
    
    public HaUser getUser() {
        return user;
    }
    public void setUser(HaUser user) {
        this.user = user;
    }
    public List<UserProgramInfo> getPrograms() {
        return programs;
    }
    public void setPrograms(List<UserProgramInfo> programs) {
        this.programs = programs;
    }
}
