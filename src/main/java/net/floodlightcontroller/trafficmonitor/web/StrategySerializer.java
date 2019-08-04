package net.floodlightcontroller.trafficmonitor.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.floodlightcontroller.trafficmonitor.Strategy;

import java.io.IOException;

public class StrategySerializer extends JsonSerializer<Strategy> {

    @Override
    public void serialize(Strategy strategy, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();
        //注意getBigInteger()
        jsonGenerator.writeStringField("maxSpeed",strategy.getMaxSpeed().getBigInteger().toString());
        jsonGenerator.writeStringField("action",strategy.getAction());
        jsonGenerator.writeStringField("actionDuring",String.valueOf(strategy.getActionDuration()));
        jsonGenerator.writeEndObject();
    }
}
