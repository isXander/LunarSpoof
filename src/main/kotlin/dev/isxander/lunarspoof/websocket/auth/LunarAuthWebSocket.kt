package dev.isxander.lunarspoof.websocket.auth

import com.mojang.authlib.exceptions.AuthenticationException
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import dev.isxander.lunarspoof.mc
import dev.isxander.lunarspoof.websocket.auth.packet.LunarPacket
import dev.isxander.lunarspoof.websocket.auth.packet.impl.CPacketEncryptionResponse
import dev.isxander.lunarspoof.websocket.auth.packet.impl.SPacketAuthenticatedRequest
import dev.isxander.lunarspoof.websocket.auth.packet.impl.SPacketEncryptionRequest
import gg.essential.api.utils.JsonHolder
import net.minecraft.util.CryptManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.math.BigInteger
import java.net.Proxy
import java.net.URI
import java.nio.ByteBuffer
import java.util.*


class LunarAuthWebSocket(
    httpHeaders: Map<String, String>,
    private val consumer: (String?) -> Unit
) : WebSocketClient(
    URI("wss://authenticator.lunarclientprod.com"),
    Draft_6455(),
    httpHeaders,
    30000
) {
    private var authenticated = false
    override fun onOpen(handshakedata: ServerHandshake) {
        LOGGER.info("Connected.")
    }

    override fun onMessage(message: String) {}
    override fun onMessage(bytes: ByteBuffer) {
        processMessage(JsonHolder(String(bytes.array())))
    }

    fun processMessage(json: JsonHolder) {
        val packet: LunarPacket
        val packetType = json.optString("packetType")
        packet = when (packetType) {
            "SPacketEncryptionRequest" -> SPacketEncryptionRequest()
            "SPacketAuthenticatedRequest" -> SPacketAuthenticatedRequest()
            else -> return
        }
        packet.processJson(json)
        packet.process(this)
        LOGGER.info("Processed Packet: " + packet.name)
    }

    fun acceptEncryption(packet: SPacketEncryptionRequest) {
        LOGGER.info("Accepting Encryption")
        val secretKey = CryptManager.createNewSharedKey()
        val publicKey = packet.key
        val keyHash = CryptManager.getServerIdHash("", publicKey, secretKey) ?: return
        val str = BigInteger(keyHash).toString(16)
        try {
            val session = YggdrasilAuthenticationService(
                Proxy.NO_PROXY,
                UUID.randomUUID().toString()
            ).createMinecraftSessionService()
            session.joinServer(mc.session.profile, mc.session.token, str)
        } catch (ex: AuthenticationException) {
            ex.printStackTrace()
        } catch (npe: NullPointerException) {
            close()
        }
        sendPacket(CPacketEncryptionResponse(secretKey, publicKey, packet.bytes))
    }

    fun acceptAuthentication(packet: SPacketAuthenticatedRequest) {
        authenticated = true
        close()
        consumer(packet.jwtKey)
    }

    fun sendPacket(packet: LunarPacket) {
        if (!isOpen) return
        LOGGER.info("Sending Packet: " + packet.name)
        val json = JsonHolder()
        json.put("packetType", packet.name)
        packet.processJson(json)
        send(json.toString())
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        if (code == 1000) {
            LOGGER.info("Authentication Completed.")
        }
        LOGGER.info(String.format("Connection Closed (%d, \"%s\")", code, reason))
        if (authenticated) return
        consumer(null)
    }

    override fun onError(e: Exception) {
        e.printStackTrace()
    }

    companion object {
        val LOGGER: Logger = LogManager.getLogger("LS Auth")
    }
}