package net.floodlightcontroller.accesscontrol.web;

import net.floodlightcontroller.accesscontrol.IAccessControl;
import net.floodlightcontroller.accesscontrol.domain.Response;
import net.floodlightcontroller.accesscontrol.domain.User;
import net.floodlightcontroller.accesscontrol.utils.JsonUtil;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(UserIdResource.class);

    @Get("json")
    public Response listUsers() {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        return new Response<>(1, "", macTracker.listAllUsers());
    }

    @Post("json")
    public Response addUser(String json) {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        User user = JsonUtil.readValue(json, User.class);
        if (macTracker.addUser(user))
            return new Response<>(1, "add success", null);
        else
            return new Response<>(-1, "add failed", null);
    }

    @Put("json")
    public Response updateUser(String json) {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        User user = JsonUtil.readValue(json, User.class);
        if (macTracker.updateUser(user))
            return new Response<>(1, "update success", null);
        else
            return new Response<>(-1, "update failed", null);
    }
}
