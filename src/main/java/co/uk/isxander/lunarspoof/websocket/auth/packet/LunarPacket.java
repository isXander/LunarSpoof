package co.uk.isxander.lunarspoof.websocket.auth.packet;

import co.uk.isxander.lunarspoof.websocket.auth.LunarAuthWebSocket;
import co.uk.isxander.xanderlib.utils.json.BetterJsonObject;

public abstract class LunarPacket {

    public abstract String getName();
    public abstract void process(LunarAuthWebSocket ws);

    public void processJson(BetterJsonObject json) {
    }

}
