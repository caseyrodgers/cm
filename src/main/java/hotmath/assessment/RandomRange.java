package hotmath.assessment;

import java.sql.Connection;

/** problems based on random ranges
 * 
 * @author casey
 *
 */
public class RandomRange {

    private int start;
    private int end;
    private int numToCreate;

    public RandomRange(Connection conn, int start, int end, int numToCreate) {
        this.start = start;
        this.end = end;
        this.numToCreate = numToCreate;
    }

}
