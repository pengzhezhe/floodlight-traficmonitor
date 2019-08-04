package net.floodlightcontroller.accesscontrol.web;

import net.floodlightcontroller.accesscontrol.IAccessControl;
import net.floodlightcontroller.accesscontrol.domain.Device;
import net.floodlightcontroller.accesscontrol.domain.Response;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DeviceIdResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(UserIdResource.class);

    @Get("json")
    public List<Device> listDevicesByUserId() {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        String id = (String) getRequestAttributes().get("id");
        List<Device> devices = macTracker.listDevicesByUserId(Integer.parseInt(id));
        return devices;
    }

    @Delete("json")
    public Response deleteDevice() {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        String id = (String) getRequestAttributes().get("id");
        if (macTracker.deleteDevice(Integer.parseInt(id)))
            return new Response<>(1, "delete success", null);
        else
            return new Response<>(-1, "delete failed", null);
    }
}
