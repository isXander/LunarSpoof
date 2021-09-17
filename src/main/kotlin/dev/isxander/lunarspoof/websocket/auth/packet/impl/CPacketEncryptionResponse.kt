package dev.isxander.lunarspoof.websocket.auth.packet.impl

import dev.isxander.lunarspoof.websocket.auth.LunarAuthWebSocket
import dev.isxander.lunarspoof.websocket.auth.packet.LunarPacket
import gg.essential.api.utils.JsonHolder
import net.minecraft.util.CryptManager
import java.security.PublicKey
import java.util.*
import javax.crypto.SecretKey

class CPacketEncryptionResponse(
    secretKey: SecretKey,
    publicKey: PublicKey,
    keyHash: ByteArray,
) : LunarPacket() {
    private val secretHash: ByteArray
    private val publicHash: ByteArray
    override val name: String = "CPacketEncryptionResponse"

    override fun process(ws: LunarAuthWebSocket) {}
    override fun processJson(json: JsonHolder) {
        val encoder = Base64.getUrlEncoder()
        json.put("secretKey", String(encoder.encode(secretHash)))
        json.put("publicKey", String(encoder.encode(publicHash)))
    }

    init {
        secretHash = CryptManager.encryptData(publicKey, secretKey.encoded)
        publicHash = CryptManager.encryptData(publicKey, keyHash)
    }
}