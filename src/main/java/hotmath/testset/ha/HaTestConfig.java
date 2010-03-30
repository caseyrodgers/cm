package hotmath.testset.ha;

import hotmath.HotMathException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/** Custom configuration settings for individual 
 *   test definitions.
 *   
 *   Example:
 *   
 *   {segments:4,chapters:['test','test2']}
 *   or
 *   {segments:5,custom_program_id:3000}
 *   where custom_program_id is a PK into
 *   HA_CUSTOM_PROGRAM that defines a custom
 *   program's list of topics in which to build
 *   the program's pid source.
 *   
 *   
 * @author Casey
 *
 */
public class HaTestConfig {
	List<String> chapters = new ArrayList<String>();
	int passPercent=80;
	int segmentCount=4;
	String json;


    public int getSegmentCount() {
        return segmentCount;
    }


    public void setSegmentCount(int segmentCount) {
        this.segmentCount = segmentCount;
    }

    public HaTestConfig() {}

    public HaTestConfig(String json) throws HotMathException {
        this(null, json);
    }
    
    /** Create HaTestConfig based on json object
     * 
     * If error, uses default values.
     * 
     * @param json
     * @throws HotMathException
     */
    public HaTestConfig(Integer passPercent, String json) throws HotMathException {
        this.json = json;
        if(passPercent != null)
           this.passPercent = passPercent;
        
		try {
			if(json != null && json.length() > 0) {
				JSONObject jo = new JSONObject(json);
				segmentCount = 1;
				if(jo.has("segments"))
				    segmentCount = jo.getInt("segments");
				
				if(!jo.isNull("chapters")) {
				    JSONArray ja = jo.getJSONArray("chapters");
			        for(int i=0;i<ja.length();i++) {
				        chapters.add(ja.getString(i));
			        }
				}
				
				/** If pass percent is in JSON, allow override 
				 * 
				 */
		        if(jo.has("pass_percent"))
		            passPercent = jo.getInt("pass_percent");
		        
			}
		}
		catch(Exception e) {
		    Logger.getLogger(this.getClass()).info("Error creating test config object (using default)", e);
		}
	}
	
	public String getJson() {
        return json;
    }


    public void setJson(String json) {
        this.json = json;
    }


    public List<String> getChapters() {
		return chapters;
	}
	public void setChapters(List<String> chapters) {
		this.chapters = chapters;
	}
	public int getPassPercent() {
		return passPercent;
	}
	public void setPassPercent(int passPercent) {
		this.passPercent = passPercent;
	}


    @Override
    public String toString() {
        return "HaTestConfig [chapters=" + chapters + ", passPercent=" + passPercent + ", segmentCount=" + segmentCount
                + "]";
    }
}
