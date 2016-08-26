package hotmath.gwt.hm_mobile.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetSolutionForMobileAction;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.Pid;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.hm_mobile.client.ClientFactory;
import hotmath.gwt.hm_mobile.client.HmMobile;
import hotmath.gwt.hm_mobile.client.event.EnableDisplayZoomEvent;
import hotmath.gwt.hm_mobile.client.place.TutorViewPlace;
import hotmath.gwt.hm_mobile.client.view.BookListView;
import hotmath.gwt.hm_mobile.client.view.TutorView;

public class TutorViewActivity extends AbstractActivity implements TutorView.Presenter {
	private ClientFactory clientFactory;
	private String token;
	

	public TutorViewActivity(TutorViewPlace place, ClientFactory clientFactory) {
		this.token = place.getToken();
		this.clientFactory = clientFactory;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		BookListView view = clientFactory.getBookListView();
		containerWidget.setWidget(view.asWidget());
		view.showBookList(null,null);		
	}

	/**
	 * Ask user before stopping this activity
	 */
	@Override
	public String mayStop() {
		return null; // "Are you sure you want to stop this Yoga Class?";
	}

	/**
	 * Navigate to a new Place in the browser
	 */
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	@Override
    public void getTutor(final String pid, final CallbackOnComplete callback) {
		

		Pid pidO = new Pid(pid);
		String path = "/help/solutions/" + pidO.getTextCode() + "/" + pidO.getChapter() + "/" + pidO.getSection() + "/" + pidO.getProblemSet() + "/" + pidO.getPid();
		
		String url = path + "/tutor_all.txt";
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			Request response = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					System.out.println("Error reading json: " + exception);
				}

				public void onResponseReceived(Request request, Response response) {
					TutorAll tutorAll = parseTutorAll(response.getText());
					
					Pid p = new Pid(pid);
					
					ProblemNumber problemNumber = new ProblemNumber(p.getProblem(), p.getProblemSet(), p.getPid(), p.getPage());
					SolutionResponse solutionResponse = new SolutionResponse(problemNumber,tutorAll.tutorRawHtml, tutorAll.tutorData,false,null);
					
					if(tutorAll.tutorData == null) {
						PopupMessageBox.showMessage("Solution not found!");
					}
					else {
					    clientFactory.getTutorView().loadSolution(solutionResponse);
						callback.isComplete();
					}
				}
			});
		} catch (RequestException e) {
			e.printStackTrace();
		}
		
		if(true) {
			return;
		}
		
		
		
		clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(true));
		GetSolutionForMobileAction action = new GetSolutionForMobileAction(pid);
		HmMobile.getCmService().execute(action,new AsyncCallback<SolutionResponse>() {
			public void onSuccess(SolutionResponse solutionResponse) {
				clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
				clientFactory.getTutorView().loadSolution(solutionResponse);
				
				callback.isComplete();
				
				HmMobile.__clientFactory.getEventBus().fireEvent(new EnableDisplayZoomEvent(true));
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
				arg0.printStackTrace();
				Window.alert(arg0.getMessage());
			}
		});
    }
	
	
	protected TutorAll parseTutorAll(String text) {
		TutorAll ta = new TutorAll();
		int sepStart = text.indexOf("++++++");
		if(sepStart > -1) {
			ta.tutorRawHtml = text.substring(0, sepStart);
			ta.tutorData = text.substring(sepStart+6);
		}
		return ta;
	}


	class TutorAll {
		String tutorRawHtml;
		String tutorData;
	}
}