package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

/** Paged student records
 * 
 * @author casey
 *
 * @param <StudentModelExt>
 */
public class CmStudentPagingLoadResult<StudentModelExt> implements PagingLoadResult<StudentModelExt>, Serializable, Response {

    protected int offset = 0;
    protected int totalLength = 0;
    List<StudentModelExt> studentList = new ArrayList<StudentModelExt>();

    /** needed for serialization
     * 
     */
    public CmStudentPagingLoadResult() {}
    
    public CmStudentPagingLoadResult(List<StudentModelExt> students) {
        this.studentList = students;
    }

    public CmStudentPagingLoadResult(List<StudentModelExt> data, int offset, int totalLength) {
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
    public List<StudentModelExt> getData() {
        return studentList;
    }
}
