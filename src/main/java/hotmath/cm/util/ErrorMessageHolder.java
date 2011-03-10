package hotmath.cm.util;

import hotmath.gwt.cm_rpc.client.ErrorMessage;

public class ErrorMessageHolder {

	private static ThreadLocal<ErrorMessage> holder = new ThreadLocal<ErrorMessage>();
	
	public static ErrorMessage get() {
		return holder.get();
	}
	
    public static void set(ErrorMessage ErrorMessage) {
        holder.set(ErrorMessage);
    }

    public static void remove() {
    	holder.remove();
    }

}
