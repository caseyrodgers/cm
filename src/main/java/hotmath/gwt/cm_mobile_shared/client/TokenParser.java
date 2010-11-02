package hotmath.gwt.cm_mobile_shared.client;

/** Parse a URL token resource into components
 * 
 * @author casey
 * 
 * Each token needs to represent an absolute resource.  Depending 
 * on the type of resource, the following must be provided:
 * resource_lesson = lesson file (ie, integers.html or integers-add.html) 
 * resource_type = type of resource (ie, practice, video, etc..)
 * resource_ordinal = the ordinal position of the resource in the resource list.
 * 
 *  For example, for video number 3, it might be:
 *  video:integers.html:2 
 *  
 *  Some rules for missing pieces:
 *  1. if ordinal is missing, default to zero
 *  2. if type if missing, default to lesson page
 *  3. if lesson is missing, default to search page
 *
 */
public class TokenParser {
    int ordinal;
    String type;
    String resource;

    public TokenParser() {
    }
    
    public TokenParser(String type, String resource, int ordinal) {
        this.type = type;
        this.resource = resource;
        this.ordinal = ordinal;
    }
    
    public TokenParser(String token) {
        String p[] = token.split(":");
        if(p.length > 2) {
            ordinal = Integer.parseInt(p[2]);
        }
        if(p.length > 1) {
            resource = p[1];
        }
        if(p.length > 0 && p[0].length() > 0) {
            type = p[0];
        }
    }
    
    /** return serialized history token
     * for use in URL.  Contains random
     * number to keep fresh.
     * 
     * @return
     */
    public String getHistoryTag() {
        String out="";
        if(resource != null) {
            out += type + ":" + resource + ":" + ordinal;
        }
        else if(type != null) {
            out += type;
        }
        
        
        /** keep uniq */
        out += ":" + System.currentTimeMillis();

        return out;
    }
    
    
    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLesson() {
        return resource;
    }

    public void setLesson(String lesson) {
        this.resource = lesson;
    }
}
