package hotmath.cm.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ActionInfo {
    String name;
    int max;
    int avg;
    Date lastUse;
    
    List<String> args = new ArrayList<String>();
    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getAvg() {
        return avg;
    }

    public void setAvg(int avg) {
        this.avg = avg;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    
    public long getLastUseSeconds() {
        if(lastUse != null) {
            return (System.currentTimeMillis() - lastUse.getTime()) / 1000;
        }
        else 
            return -1;
    }
    
    public Date getLastUse() {
        return lastUse;
    }
    
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public void setTimeStamp(String timeStamp) {
        Date dte=null;
        try {
             dte = format.parse(timeStamp);
             if(lastUse == null || dte.getTime() > lastUse.getTime())
                 lastUse = dte;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    int min;
    int count;
    int total;
    
    public ActionInfo(String name) {
        this.name = name;
    }
    
    public void setInfo(int time) {
        count++;
        total += time;
        avg = total / count;
        
        if(time > max)
            max = time;
        if(time < min )
            min = time;
    }
    

    @Override
    public String toString() {
        return "ActionInfo [args=" + args + ", avg=" + avg + ", count=" + count + ", max=" + max + ", min=" + min
                + ", name=" + name + ", total=" + total + "]";
    }


}