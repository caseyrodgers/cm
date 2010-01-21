package hotmath.cm.util.service;

import hotmath.gwt.shared.client.rpc.result.AutoRegistrationEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Load student/password pairs
 * 
 * @author bob
 * 
 */
public class BulkRegLoader {

    private int errorCount;
    private List<String> dupNames;
    private List<String> dupPasswords;
    String key;

    private List<AutoRegistrationEntry> entries = new ArrayList<AutoRegistrationEntry>(); 

    /** Read the input stream and extract entries
     * 
     * 
     *  IE includes the filename in the data, which does not conform to our 
     *  expectations.  We need to identify this problem and ignore the row.
     *  
     * @param is
     * @throws IOException
     */
    public void readStream(InputStream is) throws IOException {
        BufferedReader isr = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line;
        Set<String> nameSet = new HashSet<String>();
        Set<String> passwdSet = new HashSet<String>();
        dupNames = new ArrayList<String> ();
        dupPasswords = new ArrayList<String> ();
        
        while ((line = isr.readLine()) != null) {

            line = line.trim();
            if(line.startsWith("#") || line.length() == 0)
                continue;

            line = removeSpecialCharacters(line);
            
            String pair[] = line.split("\t");
            if(pair.length == 2 && !nameSet.contains(pair[0]) && !passwdSet.contains(pair[1]) ) {
                AutoRegistrationEntry entry = new AutoRegistrationEntry();
                entry.setName(pair[0]);
                entry.setPassword(pair[1]);
                
                nameSet.add(pair[0]);
                passwdSet.add(pair[1]);
                
                entries.add(entry);
            }
            else {
                if ((pair.length > 0 && nameSet.contains(pair[0])) ||
                    (pair.length > 1 && passwdSet.contains(pair[1]))) {
                    errorCount++;
                    if (nameSet.contains(pair[0])) {
                    	if (! dupNames.contains(pair[0]))
                        	dupNames.add(pair[0]);
                    }
                    if (pair.length > 1 && passwdSet.contains(pair[1])) {
                    	if (! dupPasswords.contains(pair[1]))
                        	dupPasswords.add(pair[1]);
                    }
                }
                else {
                    /** if error is simply not enough tokens, eat it.
                     * 
                     * @TODO: find better way to know lines to ignore
                     * (This is to deal with filename added to input by IE.)
                     */
                }
            }
        }
        
        this.key = "upload_" + System.currentTimeMillis();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /** remove any special characters such as quotes and double quotes
     * 
     */
    private String removeSpecialCharacters(String s) {
        
        String r = s.replace("\"","");
        r = r.replace("'","");
        return r;
    }
    

    @Override
    public String toString() {
        return "BulkRegLoader [duplicateNames=" + dupNames + ", duplicatePasswords="
                + dupPasswords + ", entries=" + entries + ", errorCount=" + errorCount
                + ", key=" + key + "]";
    }

    public int getStudentCount() {
        return entries.size();
    }

    public int getErrorCount() {
        return errorCount;
    }

    public boolean hasErrors() {
        return (errorCount != 0);
    }

    public boolean hasDuplicateNames() {
    	return (dupNames.size() > 0);
    }
    
    public boolean hasDuplicatePasswords() {
    	return (dupPasswords.size() > 0);
    }

    public List<AutoRegistrationEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<AutoRegistrationEntry> entries) {
        this.entries = entries;
    }
    
    public List<String> getDuplicateNames() {
    	return dupNames;
    }
    
    public List<String> getDuplicatePasswords() {
    	return dupPasswords;
    }
}
