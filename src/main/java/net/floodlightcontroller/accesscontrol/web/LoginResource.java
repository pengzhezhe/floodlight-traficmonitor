package net.floodlightcontroller.accesscontrol.web;

import net.floodlightcontroller.accesscontrol.IAccessControl;
import net.floodlightcontroller.accesscontrol.domain.Response;
import net.floodlightcontroller.accesscontrol.domain.User;
import net.floodlightcontroller.accesscontrol.utils.JsonUtil;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.io.IOException;
import java.util.Map;

public class LoginResource extends ServerResource {
    @Post("json")
    public Response login(String json) throws IOException {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        Map map = JsonUtil.readValue(json);
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        User user = macTracker.getUserByUsername(username);
        if (password.equals(user.getPassword()))
            return new Response<>(1, "登陆成功", user.getUserId());
        return new Response<>(-1, "用户名或密码错误", null);
    }

}
