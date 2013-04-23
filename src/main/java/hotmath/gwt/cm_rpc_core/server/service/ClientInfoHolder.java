package hotmath.gwt.cm_rpc_core.server.service;

import hotmath.gwt.cm_rpc_core.client.ClientInfo;


public class ClientInfoHolder {

	private static ThreadLocal<ClientInfo> holder = new ThreadLocal<ClientInfo>();
	
	public static ClientInfo get() {
		return holder.get();
	}
	
    public static void set(ClientInfo clientInfo) {
        holder.set(clientInfo);
    }

    public static void remove() {
    	holder.remove();
    }

}
