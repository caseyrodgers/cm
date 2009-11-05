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
    private boolean dupNamePasswd;
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
        while ((line = isr.readLine()) != null) {

            line = line.trim();
            if(line.startsWith("#") || line.length() == 0)
                continue;

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
                    dupNamePasswd = true;
                    errorCount++;
                }
                else {
                    /** if error is simply not enough tokens, eat it.
                     * 
                     * This is to deal with filename added to input by IE.
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
    

    @Override
    public String toString() {
        return "BulkRegLoader [dupNamePasswd=" + dupNamePasswd + ", entries=" + entries + ", errorCount=" + errorCount
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

    public boolean hasDupNamePasswd() {
    	return dupNamePasswd;
    }

    public List<AutoRegistrationEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<AutoRegistrationEntry> entries) {
        this.entries = entries;
    }
}
