package net.floodlightcontroller.trafficmonitor.web;

import net.floodlightcontroller.trafficmonitor.ITrafficMonitorService;
import org.projectfloodlight.openflow.types.U64;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import java.util.HashMap;

public class StrategyConfigResource extends ServerResource {
    @Post
    @Put
    public String config(String json){
        ITrafficMonitorService trafficMonitorService = (ITrafficMonitorService) getContext().getAttributes().get(ITrafficMonitorService.class.getCanonicalName());
        HashMap<String,String> strategy=jsonToHashMap(json);

        //取出属性逐一赋值
        U64 maxSpeed=U64.of(Long.valueOf(strategy.get("maxSpeed")));
        String action=strategy.get("action");
        Long actionDuring=Long.valueOf(strategy.get("actionDuring"));
        trafficMonitorService.setStrategy(maxSpeed,action,actionDuring);
        return "{\"status\":\"Strategy updated\"}";
    }

    /**
     * 将字符串格式的json转换成Hashmap格式（field和value之间的映射）
     * @param 字符串格式json
     * @return Hashmap格式json
     */
    private HashMap jsonToHashMap(String json){
        HashMap<String, String> result = new HashMap<>();
        String subString = removeBraces(json);

        String[] rows = subString.split(",");
        for(String row : rows){
            String[] columns = row.split(":");

            String key,value;
            key = columns[0].substring(1, columns[0].length()-1);
            value = columns[1].substring(1, columns[1].length()-1);

            result.put(key, value);
        }
        return result;

    }
    /**
     * 去掉字符串中指定位置的'{'和'}'
     * @param string
     * @return 去掉大括号后的string
     */
    private String removeBraces(String string){
        String result = null;
        if(string.charAt(0) == '{' && string.charAt(string.length()-1) == '}'){
            result = string.substring(1, string.length()-1);
        }
        return result;
    }
}
