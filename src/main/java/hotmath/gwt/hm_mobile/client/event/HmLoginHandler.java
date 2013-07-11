package hotmath.gwt.hm_mobile.client.event;

import hotmath.gwt.hm_mobile.client.model.HmMobileLoginInfo;

import com.google.gwt.event.shared.EventHandler;

public interface HmLoginHandler extends EventHandler{
    void userLogin(HmMobileLoginInfo loginInfo);
}
