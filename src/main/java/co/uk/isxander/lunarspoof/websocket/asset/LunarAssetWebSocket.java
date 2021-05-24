package co.uk.isxander.lunarspoof.websocket.asset;

import co.uk.isxander.lunarspoof.LunarSpoof;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class LunarAssetWebSocket extends WebSocketClient {

    public LunarAssetWebSocket(Map<String, String> httpHeaders) throws URISyntaxException {
        super(new URI("wss://assetserver.lunarclientprod.com/connect"), new Draft_6455(), httpHeaders, 30000);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {
        LunarSpoof.LOGGER.info("Message:\n" + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }

}
