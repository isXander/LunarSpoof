package co.uk.isxander.lunarspoof.websocket.packet.impl;

import co.uk.isxander.lunarspoof.websocket.LunarAuthWebSocket;
import co.uk.isxander.lunarspoof.websocket.packet.LunarPacket;
import co.uk.isxander.xanderlib.utils.json.BetterJsonObject;
import net.minecraft.util.CryptManager;

import java.security.PublicKey;
import java.util.Base64;

public class SPacketEncryptionRequest extends LunarPacket {

    private PublicKey key;
    private byte[] bytes;

    public PublicKey getKey() {
        return this.key;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public String getName() {
        return "SPacketEncryptionRequest";
    }

    @Override
    public void process(LunarAuthWebSocket ws) {
        ws.acceptEncryption(this);
    }

    @Override
    public void processJson(BetterJsonObject json) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        this.key = CryptManager.decodePublicKey(decoder.decode(json.optString("publicKey")));
        this.bytes = decoder.decode(json.optString("randomBytes"));
    }

}
