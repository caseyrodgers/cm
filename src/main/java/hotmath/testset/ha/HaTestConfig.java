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
 *   
 * @author Casey
 *
 */
public class HaTestConfig {
	List<String> chapters = new ArrayList<String>();
	int passPercent=80;
	int segmentCount=4;
	boolean hasAlternateTests;
	
	public boolean isHasAlternateTests() {
        return hasAlternateTests;
    }


    public void setHasAlternateTests(boolean hasAlternateTests) {
        this.hasAlternateTests = hasAlternateTests;
    }


    public int getSegmentCount() {
        return segmentCount;
    }


    public void setSegmentCount(int segmentCount) {
        this.segmentCount = segmentCount;
    }


    /** Create HaTestConfig based on json object
     * 
     * If error, uses default values.
     * 
     * @param json
     * @throws HotMathException
     */
    public HaTestConfig(String json) throws HotMathException {
		try {
			if(json != null && json.length() > 0) {
				JSONObject jo = new JSONObject(json);
				segmentCount = jo.getInt("segments");
				if(!jo.isNull("chapters")) {
				    JSONArray ja = jo.getJSONArray("chapters");
			        for(int i=0;i<ja.length();i++) {
				        chapters.add(ja.getString(i));
			        }
				}
		        if(jo.has("pass_percent"))
		            passPercent = jo.getInt("pass_percent");
			}
		}
		catch(Exception e) {
		    Logger.getLogger(this.getClass()).info("Error creating test config object (using default)", e);
		}
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
