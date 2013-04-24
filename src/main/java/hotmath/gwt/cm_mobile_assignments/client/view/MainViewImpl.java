package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_mobile_assignments.client.AboutPlace;
import hotmath.gwt.cm_mobile_assignments.client.ClientFactory;
import hotmath.gwt.cm_mobile_assignments.client.util.AssAlertBox;
import hotmath.gwt.cm_mobile_assignments.client.util.ChromeWorkaround;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;

/** Provides basic wrapper container for 
 * AssignmentsMobile , or AM.
 * 
 * @author casey
 *
 */
public class MainViewImpl extends BaseComposite implements MainView {
    protected LayoutPanel main;
    protected LayoutPanel mainContent;
    protected ScrollPanel scrollPanel;
    protected HeaderPanel headerPanel;
    protected HeaderButton returnToProgramButton;
    protected HeaderButton aboutButton;
    protected HorizontalPanel leftButtonBar;
    protected HorizontalPanel buttonBar;
    protected HorizontalPanel customButtonBar;
    protected HeaderButton backButton;
    protected HTML title;
    
    ClientFactory factory;

    Presenter presenter;
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
    

    int _uid;
    private BaseView currentView;
    public MainViewImpl(ClientFactory factoryIn) {
        this.factory = factoryIn;
        //initWidget(uiBinder.createAndBindUi(this));
        
        main = new LayoutPanel();
        mainContent = new LayoutPanel();
        scrollPanel = new ScrollPanel();
        headerPanel = new HeaderPanel();
        
        main.add(headerPanel);
        
        aboutButton = new HeaderButton();
        aboutButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                factory.getPlaceController().goTo(new AboutPlace());
            }
        });
        aboutButton.setText("About");
        returnToProgramButton = new HeaderButton();
        returnToProgramButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                new AssAlertBox("Return to program not implemented");    
            }
        });
        returnToProgramButton.setRoundButton(true);
        returnToProgramButton.setText("Return");
        headerPanel.setCenter("Assignments Home");
        // set if something to return to.
        //headerPanel.setLeftWidget(returnToProgramButton);
        
        backButton = new HeaderButton();
        backButton.setText("<<");
        backButton.setBackButton(true);
        backButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                Place place = currentView.getBackPlace();
                if(place != null) {
                    factory.getPlaceController().goTo(place);
                }
                else {
                    History.back();
                }
            }
        });

        
        headerPanel.setLeftWidget(leftButtonBar);
        
        customButtonBar = new HorizontalPanel();

        leftButtonBar = new HorizontalPanel();
        leftButtonBar.add(backButton);
        leftButtonBar.add(customButtonBar);
        
        headerPanel.setLeftWidget(backButton);
        
        buttonBar = new HorizontalPanel();
        buttonBar.add(aboutButton);
     
        
        headerPanel.setRightWidget(buttonBar);

        initWidget(main);
    }
    
    
    @Override
    public Widget asWidget() {
        return this;
    }


    @Override
    public void setView(BaseView view, String title, boolean needsBackButton) {
        this.currentView = view;
        view.setMain(this);
        
        customButtonBar.clear();
        
        main.remove(scrollPanel);
        main.remove(mainContent);
        
        if(view.useScrollPanel()) {
            scrollPanel.setWidget(view);
            main.add(scrollPanel);
        }
        else {
            mainContent.clear();
            mainContent.add(view.asWidget());
            main.add(mainContent);
        }
        ChromeWorkaround.maybeUpdateScroller(scrollPanel);
        setHeaderTitle(title);
        setNeedsBackButton(needsBackButton);
    }
    
    @Override
    public void setHeaderTitle(String title) {
        headerPanel.setCenter(title);
    }


    @Override
    public void setNeedsBackButton(boolean yesNo) {
        if(yesNo) {
            backButton.setVisible(true);
            //headerPanel.setRightWidget(backButton);
        }
        else {
            backButton.setVisible(false);
        }
    }
    
    @Override
    public void addCustomHeaderButton(HeaderButton btn) {
        
        // not working?
        customButtonBar.add(btn);
    }
    
}
