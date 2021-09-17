package dev.isxander.lunarspoof.websocket.asset;

import dev.isxander.lunarspoof.utils.LunarTimer;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class LunarAssetWebSocket extends WebSocketClient {

    public static final Logger LOGGER = LogManager.getLogger("LS Assets");

    public Cache<UUID, Boolean> someCache;
    public LunarTimer someTimer;
    public AssetState currentState;
    public long time;

    public LunarAssetWebSocket(Map<String, String> httpHeaders) throws URISyntaxException {
        super(new URI("wss://assetserver.lunarclientprod.com/connect"), new Draft_6455(), httpHeaders, 30000);
        this.someCache = CacheBuilder.newBuilder().expireAfterWrite(3L, TimeUnit.MINUTES).build();
        this.currentState = AssetState.DISCONNECTED;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LOGGER.info("Connection Opened.");

        this.someTimer = null;
        this.currentState = AssetState.AWAITING_ENCRYPTION_REQUEST;
        this.time = System.currentTimeMillis();
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onMessage(ByteBuffer bytes) {

    }

    private void processMessage() {
        
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LOGGER.info(String.format("Connection Closed (%d, \"%s\")", code, reason));
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

}
