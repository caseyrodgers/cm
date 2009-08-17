package hotmath.cm.util.service;

import java.io.InputStream;

import java.util.HashSet;
import java.util.Set;

/**
 * Load student/password pairs
 * 
 * @author bob
 *
 */
public class BulkRegLoader {
	
	private InputStream is;
	private int studentCount;
	private int errorCount;
	
	public BulkRegLoader() {
	}
	
	public void setInputStream(InputStream is) {
		this.is = is;
	}
	
	public void load() {
		;
	}
	
	public int getStudentCount() {
	    return studentCount;
    }

	public int getErrorCount() {
		return errorCount;
	}

	public boolean hasErrors() {
		return (errorCount != 0);
	}
}
