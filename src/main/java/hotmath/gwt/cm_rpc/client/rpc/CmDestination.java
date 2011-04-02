package hotmath.gwt.cm_rpc.client.rpc;


public class CmDestination implements Response {
    public CmDestination(){}
    
    CmPlace place;
    
    public CmDestination(String placeName) {
        String v = CmPlace.END_OF_PROGRAM.toString();
        if(placeName.equals(CmPlace.QUIZ.toString())) {
            place = CmPlace.QUIZ;
        }
        else if(placeName.equals(CmPlace.PRESCRIPTION.toString())) {
            place = CmPlace.PRESCRIPTION;
        }
        else if(placeName.equals(CmPlace.END_OF_PROGRAM.toString())) {
            place = CmPlace.END_OF_PROGRAM;
        }
        else if(placeName.equals(CmPlace.WELCOME.toString())) {
            place = CmPlace.WELCOME;
        }
    }
    public CmDestination(CmPlace place) {
        this.place = place;
    }
    
    public CmPlace getPlace() {
        return place;
    }

    public void setPlace(CmPlace place) {
        this.place = place;
    }
}
