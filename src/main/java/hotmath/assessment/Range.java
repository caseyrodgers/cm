package hotmath.assessment;

import java.util.ArrayList;
import java.util.List;

import sb.util.SbUtilities;

/** Represents a single solution range 
 * 
 * deal with parsing range and extacting grade level, in the form:
 * 
 * RANGE : GRADE
 * 
 * @author casey
 *
 */
public class Range {
    String range;
    List<Integer> gradeLevels = new ArrayList<Integer>();
    
    public Range(String range) {
        if(range == null) {
            //
        }
        else {
            if(range.startsWith("{"))  {
                this.range = range; 
            }
            else {
                String p[] = range.split("\\:");
                if(p.length==1) {
                    this.range = range;
                }
                else {
                    this.range = p[0];
                    gradeLevels.addAll(parseGradeLevels(p[1]));
                }
            }
        }
    }
    
    static public List<Integer> parseGradeLevels(String gradeLevelRange) {
        List<Integer> list = new ArrayList<Integer>();
        String p[] = gradeLevelRange.split("-");
        if(p.length == 1) {
            list.add(Integer.parseInt(p[0]));
        }
        else {
            int start=SbUtilities.getInt(p[0]);
            int end=SbUtilities.getInt(p[1]);
            
            for(int i=start;i<(end+1);i++) {
                list.add(new Integer(i));
            }
        }
        return list;
    }
    
    public String getRange() {
        return range;
    }
    
    public List<Integer> getGradeLevels() {
        return gradeLevels;
    }
}
