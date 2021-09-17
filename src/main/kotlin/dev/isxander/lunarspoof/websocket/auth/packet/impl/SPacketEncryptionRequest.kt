package dev.isxander.lunarspoof.websocket.auth.packet.impl

import dev.isxander.lunarspoof.websocket.auth.LunarAuthWebSocket
import dev.isxander.lunarspoof.websocket.auth.packet.LunarPacket
import gg.essential.api.utils.JsonHolder
import net.minecraft.util.CryptManager
import java.security.PublicKey
import java.util.*

class SPacketEncryptionRequest : LunarPacket() {
    override val name: String = "SPacketEncryptionRequest"

    lateinit var key: PublicKey
        private set
    lateinit var bytes: ByteArray
        private set

    override fun process(ws: LunarAuthWebSocket) {
        ws.acceptEncryption(this)
    }

    override fun processJson(json: JsonHolder) {
        val decoder = Base64.getUrlDecoder()
        key = CryptManager.decodePublicKey(decoder.decode(json.optString("publicKey")))
        bytes = decoder.decode(json.optString("randomBytes"))
    }
}