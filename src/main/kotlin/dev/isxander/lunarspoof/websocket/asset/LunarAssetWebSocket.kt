package dev.isxander.lunarspoof.websocket.asset

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import dev.isxander.lunarspoof.utils.LunarTimer
import dev.isxander.lunarspoof.websocket.asset.packet.AbstractWebSocketPacket
import dev.isxander.lunarspoof.websocket.asset.packet.impl.WSPacketClientUnknownCosmetic
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketBuffer
import org.apache.logging.log4j.LogManager
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import org.lwjgl.Sys
import java.net.URI
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("unstable")
class LunarAssetWebSocket(
    httpHeaders: Map<String, String>
) : WebSocketClient(
    URI("wss://assetserver.lunarclientprod.com/connect"),
    Draft_6455(),
    httpHeaders,
    30000
) {
    val someCache: Cache<UUID, Boolean> = CacheBuilder.newBuilder().expireAfterWrite(3L, TimeUnit.MINUTES).build()
    var someTimer: LunarTimer? = null
    var currentState: AssetState = AssetState.DISCONNECTED
    var time: Long = 0
    override fun onOpen(handshakedata: ServerHandshake) {
        LOGGER.info("Connection Opened.")
        someTimer = null
        currentState = AssetState.AWAITING_ENCRYPTION_REQUEST
        time = System.currentTimeMillis()
    }

    override fun onMessage(message: String) {}
    override fun onMessage(bytes: ByteBuffer) {
        processPacketBuffer(PacketBuffer(Unpooled.wrappedBuffer(bytes.array())))
    }

    private fun processPacketBuffer(buf: PacketBuffer) {
        val clazz = AbstractWebSocketPacket.REGISTRY.inverse()[buf.readVarIntFromBuffer()]
        try {
            val packet = clazz?.newInstance() ?: return
            LOGGER.debug("Recieved: ${clazz.simpleName}")
            packet.processBuf(buf)
            packet.processSocket(this)
        } catch (e: Exception) {
            LOGGER.error("Error from: $clazz")
            e.printStackTrace()
        }
    }

    fun processUnknownCosmetic(packet: WSPacketClientUnknownCosmetic) {

    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        LOGGER.info(String.format("Connection Closed (%d, \"%s\")", code, reason))
    }

    override fun onError(e: Exception) {
        e.printStackTrace()
    }

    companion object {
        val LOGGER = LogManager.getLogger("LS Assets")
    }
}
