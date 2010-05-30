package hotmath.assessment;

import hotmath.ProblemID;


public class RppWidget {

    String file;
    String title;
    ProblemID pid;

    /**
     * form of [PATH_TO_WIDGET|TITLE_OF_WIDGET]
     * 
     * @param def
     */
    public RppWidget(String def) {
        String p[] = def.substring(1, def.length() - 1).split("\\|");
        file = p[0];
        title = p[1];
    }

    public RppWidget(ProblemID pid) {
        this.pid = pid;
    }

    public ProblemID getPid() {
        return pid;
    }

    public void setPid(ProblemID pid) {
        this.pid = pid;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public boolean isSolution() {
        return this.pid != null;
    }
    
    @Override
    public String toString() {
        return String.format("pid=%s,file=%s",pid,file);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RppWidget) {
            RppWidget w = (RppWidget)obj;
            if(this.isSolution() && w.isSolution()) {
                return this.getPid().equals(w.getPid());
            }
            else {
                return this.getFile().equals(w.getFile());
            }
        }
        else {
            return super.equals(obj);
        }
    }
}
