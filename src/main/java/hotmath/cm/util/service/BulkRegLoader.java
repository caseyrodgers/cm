package hotmath.cm.util.service;

import hotmath.gwt.shared.client.rpc.result.AutoRegistrationEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Load student/password pairs
 * 
 * @author bob
 * 
 */
public class BulkRegLoader {

    private int errorCount;
    List<AutoRegistrationEntry> entries = new ArrayList<AutoRegistrationEntry>(); 

    public BulkRegLoader(InputStream is) throws IOException {
        BufferedReader isr = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer contents = new StringBuffer();
        String line = null;
        while ((line = isr.readLine()) != null) {
            
            line = line.trim();
            if(line.startsWith("#") || line.length() == 0)
                continue;

            String pair[] = line.split("\t");
            if(pair.length == 2) {
                AutoRegistrationEntry entry = new AutoRegistrationEntry();
                entry.setName(pair[0]);
                entry.setPassword(pair[1]);
                
                entries.add(entry);
            }
            else {
                errorCount++;
            }
        }

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

    public List<AutoRegistrationEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<AutoRegistrationEntry> entries) {
        this.entries = entries;
    }
}
