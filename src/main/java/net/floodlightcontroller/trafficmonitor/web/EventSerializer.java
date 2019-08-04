package net.floodlightcontroller.trafficmonitor.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.floodlightcontroller.trafficmonitor.Event;

import java.io.IOException;
import java.text.DateFormat;

public class EventSerializer extends JsonSerializer<Event> {


    @Override
    public void serialize(Event event, JsonGenerator jGen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jGen.writeStartObject();
        jGen.writeStringField("time", DateFormat.getDateTimeInstance().format(event.getTime()));
        jGen.writeFieldName("source");
        jGen.writeStartObject();
        jGen.writeStringField("dpid", event.getSource().getNodeId().toString());
        jGen.writeStringField("port_no", event.getSource().getPortId().toString());
        jGen.writeEndObject();
        jGen.writeFieldName("description");
        jGen.writeStartObject();
        jGen.writeStringField("traffic_threshold", event.getStrategy().getMaxSpeed().getBigInteger().toString());
        jGen.writeStringField("rx_speed", event.getRxSpeed().getBigInteger().toString());
        jGen.writeStringField("tx_speed", event.getTxSpeed().getBigInteger().toString());
        jGen.writeEndObject();
        jGen.writeFieldName("system_action");
        jGen.writeStartObject();
        jGen.writeStringField("action", event.getStrategy().getAction());
        jGen.writeStringField("action_duration", String.valueOf(event.getStrategy().getActionDuration()));
        jGen.writeEndObject();
        jGen.writeEndObject();
    }
}
