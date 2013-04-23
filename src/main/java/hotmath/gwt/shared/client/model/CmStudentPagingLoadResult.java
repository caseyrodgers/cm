package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.data.shared.loader.PagingLoadResult;


/** Paged student records
 * 
 * @author casey
 *
 * @param <StudentModelExt>
 */
public class CmStudentPagingLoadResult<StudentModelI> implements PagingLoadResult<StudentModelI>, Serializable, Response {

    protected int offset = 0;
    protected int totalLength = 0;
    List<StudentModelI> studentList = new ArrayList<StudentModelI>();

    /** needed for serialization
     * 
     */
    public CmStudentPagingLoadResult() {}
    
    public CmStudentPagingLoadResult(List<StudentModelI> students) {
        this.studentList = students;
    }

    public CmStudentPagingLoadResult(List<StudentModelI> data, int offset, int totalLength) {
      this(data);
      this.offset = offset;
      this.totalLength = totalLength;
    }


    public int getOffset() {
      return offset;
    }

    public int getTotalLength() {
      return totalLength;
    }

    public void setOffset(int offset) {
      this.offset = offset;
    }

    public void setTotalLength(int totalLength) {
      this.totalLength = totalLength;
    }

    @Override
    public List<StudentModelI> getData() {
        return studentList;
    }
}
