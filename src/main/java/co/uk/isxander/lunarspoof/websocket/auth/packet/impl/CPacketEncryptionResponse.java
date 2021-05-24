package co.uk.isxander.lunarspoof.websocket.auth.packet.impl;

import co.uk.isxander.lunarspoof.websocket.auth.LunarAuthWebSocket;
import co.uk.isxander.lunarspoof.websocket.auth.packet.LunarPacket;
import co.uk.isxander.xanderlib.utils.json.BetterJsonObject;
import net.minecraft.util.CryptManager;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.Base64;

public class CPacketEncryptionResponse extends LunarPacket {

    private final byte[] secretHash;
    private final byte[] publicHash;

    public CPacketEncryptionResponse(SecretKey secretKey, PublicKey publicKey, byte[] keyHash) {
        this.secretHash = CryptManager.encryptData(publicKey, secretKey.getEncoded());
        this.publicHash = CryptManager.encryptData(publicKey, keyHash);
    }

    @Override
    public String getName() {
        return "CPacketEncryptionResponse";
    }

    @Override
    public void process(LunarAuthWebSocket ws) {
    }

    @Override
    public void processJson(BetterJsonObject json) {
        Base64.Encoder encoder = Base64.getUrlEncoder();
        json.addProperty("secretKey", new String(encoder.encode(secretHash)));
        json.addProperty("publicKey", new String(encoder.encode(publicHash)));
    }

}
