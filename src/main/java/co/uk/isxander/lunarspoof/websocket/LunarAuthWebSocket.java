package co.uk.isxander.lunarspoof.websocket;

import co.uk.isxander.lunarspoof.LunarSpoof;
import co.uk.isxander.lunarspoof.websocket.packet.LunarPacket;
import co.uk.isxander.lunarspoof.websocket.packet.impl.CPacketEncryptionResponse;
import co.uk.isxander.lunarspoof.websocket.packet.impl.SPacketAuthenticatedRequest;
import co.uk.isxander.lunarspoof.websocket.packet.impl.SPacketEncryptionRequest;
import co.uk.isxander.xanderlib.utils.Constants;
import co.uk.isxander.xanderlib.utils.json.BetterJsonObject;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.util.CryptManager;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class LunarAuthWebSocket extends WebSocketClient implements Constants {

    private Consumer<String> consumer;
    private boolean whatIsThis = false;

    public LunarAuthWebSocket(Map<String, String> httpHeaders, Consumer<String> consumer) throws URISyntaxException {
        super(new URI("wss://authenticator.lunarclientprod.com"), new Draft_6455(), httpHeaders, 30000);
        this.consumer = consumer;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LunarSpoof.LOGGER.info("Connected to Lunar web socket.");
    }

    @Override
    public void onMessage(String message) {}

    @Override
    public void onMessage(ByteBuffer bytes) {
        processMessage(new BetterJsonObject(new String(bytes.array())));
    }

    public void processMessage(BetterJsonObject json) {
        LunarSpoof.LOGGER.info("Processing Message:\n" + json.toPrettyString());

        LunarPacket packet;
        String packetType = json.optString("packetType");
        switch (packetType) {
            case "SPacketEncryptionRequest":
                LunarSpoof.LOGGER.info("Packet: Encryption Request");

                packet = new SPacketEncryptionRequest();
                break;
            case "SPacketAuthenticatedRequest":
                LunarSpoof.LOGGER.info("Packet: Authentication Request");

                packet = new SPacketAuthenticatedRequest();
                break;
            default:
                return;
        }

        packet.processJson(json);
        packet.process(this);

    }

    public void acceptEncryption(SPacketEncryptionRequest packet) {
        SecretKey secretKey = CryptManager.createNewSharedKey();
        PublicKey publicKey = packet.getKey();
        byte[] keyHash = CryptManager.getServerIdHash("", publicKey, secretKey);
        if (keyHash == null) return;

        String str = (new BigInteger(keyHash)).toString(16);
        try {
            MinecraftSessionService session = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString()).createMinecraftSessionService();
            session.joinServer(mc.getSession().getProfile(), mc.getSession().getToken(), str);
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
        } catch (NullPointerException npe) {
            close();
        }
        sendPacket(new CPacketEncryptionResponse(secretKey, publicKey, packet.getBytes()));
    }

    public void acceptAuthentication(SPacketAuthenticatedRequest packet) {
        this.whatIsThis = true;
        close();
        this.consumer.accept(packet.getJwtKey());
    }

    public void sendPacket(LunarPacket packet) {
        if (!isOpen()) return;

        BetterJsonObject json = new BetterJsonObject();
        json.addProperty("packetType", packet.getName());

        packet.processJson(json);
        send(json.toPrettyString());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LunarSpoof.LOGGER.info(String.format("Connection Closed (%d, \"%s\")", code, reason));
        if (this.whatIsThis) return;
        this.consumer.accept(null);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

}
