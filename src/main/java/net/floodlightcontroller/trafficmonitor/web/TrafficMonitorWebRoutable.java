package net.floodlightcontroller.trafficmonitor.web;

import net.floodlightcontroller.restserver.RestletRoutable;
import org.restlet.Context;
import org.restlet.routing.Router;

public class TrafficMonitorWebRoutable implements RestletRoutable {
    protected static final String DPID_STR = "dpid";
    protected static final String PORT_STR = "port";
    @Override
    public Router getRestlet(Context context)
    {
        Router router=new Router(context);
        router.attach("/bandwidth/{"+DPID_STR+"}/{"+PORT_STR+"}/json",TrafficMonitorResource.class);//显示所有端口信息
        router.attach("/strategy/list/json", ListStrategyResource.class);//显示策略
        router.attach("/events/json", ListEventsResource.class);//显示所有事件
        router.attach("/strategy/conf/json", StrategyConfigResource.class);//修改策略
        router.attach("/whitelist/json", WhiteListResource.class);//显示所有白名单
        router.attach("/whitelist/conf/json",WhiteListConfigResource.class);
        return router;
    }

    @Override
    public String basePath() {
        return "/wm/trafficmonitor";
    }
}
