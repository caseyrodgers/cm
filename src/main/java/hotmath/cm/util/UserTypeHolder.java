package hotmath.cm.util;

public class UserTypeHolder {

	public enum UserType {
		STUDENT,
		ADMIN
	}

	private static ThreadLocal<UserType> holder = new ThreadLocal<UserType>();
	
	public static UserType get() {
		return holder.get();
	}
	
    public static void set(UserType cmdUserType) {
        holder.set(cmdUserType);
    }

}
