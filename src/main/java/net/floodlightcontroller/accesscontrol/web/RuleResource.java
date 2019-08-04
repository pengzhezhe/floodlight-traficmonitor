package net.floodlightcontroller.accesscontrol.web;

import net.floodlightcontroller.accesscontrol.IAccessControl;
import net.floodlightcontroller.accesscontrol.domain.Response;
import net.floodlightcontroller.accesscontrol.domain.Rule;
import net.floodlightcontroller.accesscontrol.utils.JsonUtil;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RuleResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(UserIdResource.class);

    @Get("json")
    public Response listAllRoles() {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        List<Rule> rules = macTracker.listAllRules();
        return new Response<>(1, "", rules);
    }

    @Post("json")
    public Response addRole(String json) {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        Rule rule = JsonUtil.readValue(json, Rule.class);
        if (macTracker.addRule(rule))
            return new Response<>(1, "add success", null);
        else
            return new Response<>(-1, "add failed", null);
    }

    @Put("json")
    public Response updateRole(String json) {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        Rule rule = JsonUtil.readValue(json, Rule.class);
        if (macTracker.updateRule(rule))
            return new Response<>(1, "update success", null);
        else
            return new Response<>(-1, "update failed", null);
    }
}
