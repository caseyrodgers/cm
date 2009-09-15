package hotmath.gwt.cm.client.history;



/** Represents a single, distinct location 
 *  within the CM student system.
 *  
 *  
 *   A CM Location designates:
 *   
 *   -- if is quiz / prescription
 *   -- depending on type, either the quiz number or prescription lesson number. 
 *      
 * @author casey
 *
 */
public class CmLocation {

    LocationType locationType;
    int locationNumber;
    String resourceType;
    String resourceId;
    
    public static enum LocationType{PRESCRIPTION, QUIZ};
    
    
    /** Create a CmLocation from using mini language
     * to describe a location.  The mini-lang is defined as:
     * 
     * 
     *  type|typeNumber|resourceType|resourceNumber
     *  
     *  examples:
     *  
     *  p|2|flashcard|3 == The second prescription-lesson, opening the flashcards and selecting the 3rd entry
     *  q|3 || q|3|| == The third quiz
     *  p|3|practice|1  == The third prescription lesson, open the first practice problem
     *  
     *  
     *  
     * @param location
     */
    public CmLocation(String location) {
        
        String p[] = location.split(":");
        if(p.length < 2) {
            warn(location + " is invalid, no sections.");
            return;
        }
        
        if(p[0].equals("p")) {
            locationType = LocationType.PRESCRIPTION;
        }
        else if(p[0].equals("q")) {
            locationType = LocationType.QUIZ;
        }
        else {
            warn(location + " could not determine type");
            return;
        }
        
        locationNumber = Integer.parseInt(p[1]);
            
        // the rest is optional
        if(p.length > 3) {
            resourceType = p[2];
            
            if(p.length > 3) {
                resourceId = p[3];
            }
        }
    }
    
    public CmLocation(LocationType locationType, Integer locationNumber) {
        this.locationType = locationType;
        this.locationNumber = locationNumber;
    }
    
    private void warn(String msg) {
        System.out.println("Warning: " + msg);
    }

    public String getResourceType() {
        return resourceType;
    }


    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }


    public String getResourceId() {
        return resourceId;
    }


    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }


    public LocationType getLocationType() {
        return locationType;
    }


    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }
    
    public int getLocationNumber() {
        return locationNumber;
    }


    public void setLocationNumber(int locationNumber) {
        this.locationNumber = locationNumber;
    }
    
    /** Return the resourceNumber for the resource.
     * 
     * If the resourceId is a number, then that value is returned
     * otherwise -1 is returned.  
     * 
     * If the number if -1, then you can assume the resourceId is a text representation.
     * 
     * @return
     */
    public Integer getResourceNumber() {
        if(resourceId != null && Character.isDigit(resourceId.charAt(0))) {
            return Integer.parseInt(resourceId);
        }
        else
            return -1;
    }
    
    
    public String toString() {
        String s = locationType == LocationType.PRESCRIPTION?"p":"q";
               s += ":" + locationNumber;
               s += resourceType != null?":" + resourceType + ":" + resourceId:"";
        
        return s;
    }
}

