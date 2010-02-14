package hotmath.cm.util;


public class ActionInfo {
    String name;
    int max;
    int avg;
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ActionInfo [avg=" + avg + ", count=" + count + ", max=" + max + ", min=" + min + ", name=" + name
                + ", total=" + total + "]";
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
}