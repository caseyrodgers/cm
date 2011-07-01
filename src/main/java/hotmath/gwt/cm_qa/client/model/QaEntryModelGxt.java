package hotmath.gwt.cm_qa.client.model;


import hotmath.gwt.cm_rpc.client.model.QaEntryModel;
import hotmath.gwt.cm_rpc.client.rpc.CmList;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;

public class QaEntryModelGxt extends BaseModel {

    public QaEntryModelGxt(String item, String description, Boolean verified,Boolean problem) {
        set("item",item );
        set("description", description);
        set("verified", verified);
        set("problem", problem);
    }

    public <X extends Object> X set(String name, X value) {
        X x = super.set(name, value);
        return x;
    }

    public QaEntryModel convertTo() {
        return new QaEntryModel((String)get("item"), (String)get("description"), (Boolean)get("verified"), (Boolean)get("problem"));
    }
    public static List<QaEntryModelGxt> convert(CmList<QaEntryModel> fromServer) {
        List<QaEntryModelGxt> models = new ArrayList<QaEntryModelGxt>();
        for(int i=0,t=fromServer.size();i<t;i++) {
            QaEntryModel o = fromServer.get(i);
            models.add(new QaEntryModelGxt(o.getItem(), o.getDescription(), o.isVerified(), o.isProblem()));
        }
        return models;
    }

    
    public String getDescription() {
    	return get("description");
    }
    
    public void setDescription(String d) {
    	set("description", d);
    }
    
    public String getItem() {
    	return get("item");
    }

}
