package hotmath.testset.ha;

abstract public class HaBasicUserImpl implements HaBasicUser {
    
    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    String password;
    String userName;

    public String getPassword() {
        return password;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    abstract public Object getUserObject();
    abstract public UserType getUserType();
}
