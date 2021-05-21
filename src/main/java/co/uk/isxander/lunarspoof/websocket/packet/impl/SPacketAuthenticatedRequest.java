package co.uk.isxander.lunarspoof.websocket.packet.impl;

import co.uk.isxander.lunarspoof.websocket.LunarAuthWebSocket;
import co.uk.isxander.lunarspoof.websocket.packet.LunarPacket;
import co.uk.isxander.xanderlib.utils.json.BetterJsonObject;

public class SPacketAuthenticatedRequest extends LunarPacket {

    private String jwtKey;

    public String getJwtKey() {
        return this.jwtKey;
    }

    @Override
    public String getName() {
        return "SPacketAuthenticatedRequest";
    }

    @Override
    public void process(LunarAuthWebSocket ws) {
        ws.acceptAuthentication(this);
    }

    @Override
    public void processJson(BetterJsonObject json) {
        this.jwtKey = json.optString("jwtKey");
    }

}
