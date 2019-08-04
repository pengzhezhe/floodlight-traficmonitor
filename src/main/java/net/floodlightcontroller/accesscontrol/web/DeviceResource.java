package net.floodlightcontroller.accesscontrol.web;

import net.floodlightcontroller.accesscontrol.IAccessControl;
import net.floodlightcontroller.accesscontrol.domain.Device;
import net.floodlightcontroller.accesscontrol.domain.Response;
import net.floodlightcontroller.accesscontrol.utils.JsonUtil;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DeviceResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(UserIdResource.class);

    @Get("json")
    public Response listAllDevices() {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        List<Device> devices = macTracker.listAllDevices();
        return new Response<>(1, "", devices);
    }

    @Post("json")
    public Response addDevice(String json) {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        Device device = JsonUtil.readValue(json, Device.class);
        if (macTracker.addDevice(device))
            return new Response<>(1, "add success", null);
        else
            return new Response<>(-1, "add failed", null);
    }

    @Put("json")
    public Response updateDevice(String json) {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        Device device = JsonUtil.readValue(json, Device.class);
        if (macTracker.updateDevice(device))
            return new Response<>(1, "update success", null);
        else
            return new Response<>(-1, "update failed", null);
    }
}
