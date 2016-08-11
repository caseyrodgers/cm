package hotmath.cm.server.rest; 

import com.google.gson.Gson;


/** Encapsulates the handling of results
 *  from a REST call.  Generalizes how the 
 *  client handles the results of REST calls.
 *  
 * @author casey
 *
 */
public class RestResult  {
	
	public enum ResultStatus{OK,ERROR};
	
	String model;
	String message;
	ResultStatus status;
	public RestResult(ResultStatus status, String model, String message) {
		this.status = status;
		this.model = model;
		this.message = message;
	}
	
	public RestResult(String model) {
		this(ResultStatus.OK,model,null);
	}
	
	
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status.name();
	}

	public void setStatus(ResultStatus status) {
		this.status = status;
	}

	
	static public String getResultObject(CmRestCommand command) throws Exception {
		return getResultObject(-1,command);
	}
	static public String getResultObject(int companyId, CmRestCommand command) throws Exception {
		RestResult rResult=null;
		try {
			String result = command.execute();
			rResult = new RestResult(result);
		}
		catch(Exception e) {
			e.printStackTrace();
			
			String error = handleExceptionLocally(e);
			if(error != null) {
				rResult = new RestResult(ResultStatus.ERROR,null,error);
			}
			else {
				// unknown exception, throw default
				throw e;
			}
		}
		return new Gson().toJson(rResult);
	}
	

	
    /** look for specific message patterns and return 
     *  user facing message
     *  
     * @param e
     * @return
     */
	static private String handleExceptionLocally(Exception e) {
		String msg = e.getMessage().toLowerCase();
		if(msg.contains("duplicate")) {
			return "duplicate record";
		}
		else if(msg.contains(" user") || msg.contains("company")) {
			return msg;
		}
		else {
			return null;
		}
	}	
}
