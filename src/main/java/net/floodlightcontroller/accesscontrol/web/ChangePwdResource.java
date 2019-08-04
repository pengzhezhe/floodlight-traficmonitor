package net.floodlightcontroller.accesscontrol.web;

import net.floodlightcontroller.accesscontrol.IAccessControl;
import net.floodlightcontroller.accesscontrol.domain.Response;
import net.floodlightcontroller.accesscontrol.domain.User;
import net.floodlightcontroller.accesscontrol.utils.JsonUtil;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.io.IOException;
import java.util.Map;

public class ChangePwdResource extends ServerResource {
    @Post("json")
    public Response changePwd(String json) throws IOException {
        IAccessControl macTracker = (IAccessControl) getContext().getAttributes().get(IAccessControl.class.getCanonicalName());
        Map map = JsonUtil.readValue(json);
        int userId = Integer.parseInt((String) map.get("userId"));
        String oldPwd = (String) map.get("oldPwd");
        String newPwd = (String) map.get("newPwd");
        User user = macTracker.getUserById(userId);
        if (!oldPwd.equals(user.getPassword()))
            return new Response<>(-1, "密码错误", null);
        user.setPassword(newPwd);
        if (macTracker.updateUser(user))
            return new Response<>(1, "修改成功", null);
        return new Response<>(-2, "修改失败", null);
    }
}
