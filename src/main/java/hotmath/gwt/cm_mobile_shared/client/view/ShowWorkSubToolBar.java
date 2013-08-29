package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.SexyButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;

public class ShowWorkSubToolBar extends SubToolBar {

    private FlowPanel whiteboardControlView;
    private FlowPanel whiteboardControlHide;
    private SexyButton _viewWhiteboardButton;
    private SexyButton _submitWhiteboard;
    private Callback callback;
    private SexyButton _toggleBackground;
    private boolean showSubmitButtom;
    private boolean showLessonButton;
    private SexyButton lessonButton;

    static public interface Callback {
        void showWhiteboard();

        void showLesson();

        void showProblem(boolean b);

        void whiteboardSubmitted();

        void hideWhiteboard();
    }

    public ShowWorkSubToolBar(boolean showSubmitButton, boolean showLessonButton, Callback callback) {
        super();
        this.showSubmitButtom = showSubmitButton;
        this.showLessonButton = showLessonButton;
        this.callback = callback;
        createUi();
    }

    private void createUi() {
        whiteboardControlView = new FlowPanel();
        whiteboardControlHide = new FlowPanel();
        _viewWhiteboardButton = new SexyButton("Whiteboard", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                callback.showWhiteboard();
            }
        });
        whiteboardControlView.add(_viewWhiteboardButton);

        if(showLessonButton) {
            lessonButton = new SexyButton("Lesson", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    callback.showLesson();
                }
            });
            whiteboardControlView.add(lessonButton);
        }

        whiteboardControlHide.add(new SexyButton("Close Whiteboard", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                callback.hideWhiteboard();
            }
        }));

        _toggleBackground = new SexyButton("Hide Problem");
        _toggleBackground.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (_toggleBackground.getText().startsWith("Show")) {
                    // _showWork.setBackground(true);
                    _toggleBackground.setButtonText("Hide Problem", null);
                    callback.showProblem(true);
                } else {
                    callback.showProblem(false);
                    _toggleBackground.setButtonText("Show Problem", null);
                }
            }
        });
        whiteboardControlHide.add(_toggleBackground);

        if (showSubmitButtom) {
            _submitWhiteboard = new SexyButton("Submit", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    showSubmitWhiteboard(false);
                    callback.whiteboardSubmitted();
                }
            });
            whiteboardControlHide.add(_submitWhiteboard);
        }
    }
    
    public void preventLessonButton(boolean preventIt) {
        if(showLessonButton) {
            lessonButton.setVisible( !preventIt );
        }
    }

    public void setupWhiteboardTools(boolean show) {
        if (show) {
            remove(whiteboardControlView);
            add(whiteboardControlHide);
        } else {
            remove(whiteboardControlHide);
            add(whiteboardControlView);
        }
        showReturnTo(!show);
    }

    public void showSubmitWhiteboard(boolean yesNo) {
        if (showSubmitButtom) {
            _submitWhiteboard.setVisible(yesNo);
        }
    }

    /**
     * Return true if the problem should be displayed
     * 
     * @return
     */
    public boolean getShowProblem() {
        return _toggleBackground.getText().startsWith("Hide");
    }
}
