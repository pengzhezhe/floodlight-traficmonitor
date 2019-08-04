package net.floodlightcontroller.accesscontrol.web;

import net.floodlightcontroller.accesscontrol.IAccessControl;
import net.floodlightcontroller.accesscontrol.domain.Response;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserIdResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(UserIdResource.class);

    @Get("json")
    public Response getUserById() {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        String id = (String) getRequestAttributes().get("id");
        int userId = Integer.parseInt(id);
        return new Response<>(1, "", macTracker.getUserById(userId));
    }

    @Delete("json")
    public Response deleteUser() {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        String id = (String) getRequestAttributes().get("id");
        if (macTracker.deleteUser(Integer.parseInt(id)))
            return new Response<>(1, "delete success", null);
        else
            return new Response<>(-1, "delete failed", null);
    }

}
