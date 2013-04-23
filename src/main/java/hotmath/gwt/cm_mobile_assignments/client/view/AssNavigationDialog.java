package hotmath.gwt.cm_mobile_assignments.client.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.dialog.AnimatableDialogBase;
import com.googlecode.mgwt.ui.client.dialog.Dialog;
import com.googlecode.mgwt.ui.client.theme.base.HeaderCss;

public class AssNavigationDialog implements HasOneWidget, Dialog {
    

      /**
       * The menu of an ipad overlay
       * 
       * @author Daniel Kurka
       * 
       */
      public static class IpadMenu extends Composite {

        private FlowPanel main;

        private FlowPanel menuArrow;

        private FlowPanel content;

        /**
         * Construct an {@link IpadMenu}
         */
        public IpadMenu() {
          this(MGWTStyle.getTheme().getMGWTClientBundle().getHeaderCss());
        }

        /**
         * Construct an {@link IpadMenu} with a given css
         * 
         * @param css the css to use
         */
        public IpadMenu(HeaderCss css) {
          main = new FlowPanel();
          css.ensureInjected();
          initWidget(main);

          setStylePrimaryName(css.main());

          // arrow
          menuArrow = new FlowPanel();
          menuArrow.setStylePrimaryName(css.arrow());
          main.add(menuArrow);

          content = new FlowPanel();
          content.addStyleName(css.content());

          main.add(content);

        }

        /**
         * get the body of the menu
         * 
         * @return the body of the menu
         */
        public FlowPanel getBody() {
          return content;
        }
      }

      private AnimatableDialogBase popinDialog;
      private IpadMenu ipadMenu;

      /**
       * Construct a TabletOverlay
       */
      public AssNavigationDialog() {
        popinDialog = new AnimatableDialogBase(MGWTStyle.getTheme().getMGWTClientBundle().getDialogCss()) {

          @Override
          protected Animation getShowAnimation() {
            return null;
          }

          @Override
          protected Animation getHideAnimation() {
            return null;
          }
        };

        ipadMenu = new IpadMenu();

        popinDialog.setCenterContent(false);
        popinDialog.setShadow(false);
        popinDialog.add(ipadMenu);
        popinDialog.setHideOnBackgroundClick(true);
      }

      @Override
      public void setWidget(IsWidget w) {
        ipadMenu.getBody().clear();
        ipadMenu.getBody().add(w);
      }


      @Override
      public Widget getWidget() {
        if (ipadMenu.getBody().getWidgetCount() > 0) {
          return ipadMenu.getBody().getWidget(0);
        } else {
          return null;
        }
      }

      @Override
      public void setWidget(Widget w) {
        ipadMenu.getBody().clear();
        ipadMenu.getBody().add(w);

      }

      @Override
      public void show() {
        popinDialog.show();
      }

    }

