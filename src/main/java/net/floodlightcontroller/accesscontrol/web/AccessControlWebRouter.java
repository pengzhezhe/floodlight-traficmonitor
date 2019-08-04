package net.floodlightcontroller.accesscontrol.web;

import net.floodlightcontroller.restserver.RestletRoutable;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class AccessControlWebRouter implements RestletRoutable {
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/user/{id}", UserIdResource.class);
        router.attach("/user", UserResource.class);
        router.attach("/device/{id}", DeviceIdResource.class);
        router.attach("/device", DeviceResource.class);
        router.attach("/rule/{id}", RuleIdResource.class);
        router.attach("/rule", RuleResource.class);
        router.attach("/login", LoginResource.class);
        router.attach("/changePwd", ChangePwdResource.class);
        return router;
    }

    @Override
    public String basePath() {
        return "/wm/accesscontrol";
    }
}
