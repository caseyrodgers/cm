package hotmath.gwt.cm_rpc.server.rpc;



public class MonitorType {
	static public enum MonitorTypeItem {
		STUDENT,ADMIN,ANY,OTHER,HM_MOBILE,TOTAL
	}

	MonitorTypeItem type;
	long monitorProcessingTime;
	int monitorCountActionsExecuted;
	int monitorCountActionsCompleted;
	int monitorCountActionsException;
	
	public MonitorType(MonitorTypeItem type) {
		this.type = type;
	}
}
