package net.floodlightcontroller.pktinhistory;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.types.SwitchMessagePair;
import net.floodlightcontroller.util.ConcurrentCircularBuffer;

public interface IPktInHistoryService extends IFloodlightService {
    public ConcurrentCircularBuffer<SwitchMessagePair> getBuffer();
}
