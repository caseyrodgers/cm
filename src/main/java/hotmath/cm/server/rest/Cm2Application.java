package hotmath.cm.server.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;


public class Cm2Application extends Application {
    
    private Set<Object> singletons = new HashSet<Object>();

    public Cm2Application() {
        singletons.add(new ActionDispatcherRest());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

}
