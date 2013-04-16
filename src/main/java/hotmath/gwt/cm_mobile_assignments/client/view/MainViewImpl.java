package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_mobile_assignments.client.ClientFactory;
import hotmath.gwt.cm_mobile_assignments.client.util.AssAlertBox;

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
    protected ScrollPanel scrollPanel;
    protected HeaderPanel headerPanel;
    protected HeaderButton returnToProgramButton;
    protected HeaderButton aboutButton;
    protected HorizontalPanel buttonBar;    
    protected HeaderButton backButton;
    protected HTML title;
    
    ClientFactory factory;

    Presenter presenter;
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
    

    int _uid;
    public MainViewImpl(ClientFactory factoryIn) {
        this.factory = factoryIn;
        //initWidget(uiBinder.createAndBindUi(this));
        
        main = new LayoutPanel();
        scrollPanel = new ScrollPanel();
        headerPanel = new HeaderPanel();
        main.add(headerPanel);
        main.add(scrollPanel);
        
        aboutButton = new HeaderButton();
        aboutButton.addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                factory.getMain(factory.getAboutView(), "About", true);
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
                History.back();
            }
        });
        
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
        scrollPanel.setWidget(view);
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
    
}
