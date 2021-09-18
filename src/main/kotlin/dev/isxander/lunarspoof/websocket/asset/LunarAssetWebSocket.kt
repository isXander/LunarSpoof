package dev.isxander.lunarspoof.websocket.asset

import dev.isxander.lunarspoof.LunarSpoof
import dev.isxander.lunarspoof.feature.indicator.LunarNameTagIcon
import dev.isxander.lunarspoof.websocket.asset.packet.AbstractWebSocketPacket
import dev.isxander.lunarspoof.websocket.asset.packet.impl.WSPacketClientUnknownCosmetic
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketBuffer
import org.apache.logging.log4j.LogManager
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer

@Suppress("unstable")
class LunarAssetWebSocket(
    httpHeaders: Map<String, String>
) : WebSocketClient(
    URI("wss://assetserver.lunarclientprod.com/connect"),
    Draft_6455(),
    httpHeaders,
    30000
) {
    override fun onOpen(handshakedata: ServerHandshake) {
        LOGGER.info("Connection Opened.")
    }

    override fun onMessage(message: String) {}
    override fun onMessage(bytes: ByteBuffer) {
        processPacketBuffer(PacketBuffer(Unpooled.wrappedBuffer(bytes.array())))
    }

    private fun processPacketBuffer(buf: PacketBuffer) {
        val packetId = buf.readVarIntFromBuffer()
        val clazz = AbstractWebSocketPacket.REGISTRY.inverse()[packetId]
        try {
            if (clazz == null) LOGGER.error("Unknown packet ID: $packetId")
            val packet = clazz?.newInstance() ?: return
            LOGGER.debug("Recieved: ${clazz.simpleName}")
            packet.read(buf)
            packet.handle(this)
        } catch (e: Exception) {
            LOGGER.error("Error from: $clazz")
            e.printStackTrace()
        }
    }

    fun sendPacket(packet: AbstractWebSocketPacket) {
        if (!this.isOpen) return
        val packetBuffer = PacketBuffer(Unpooled.buffer())
        packet.write(packetBuffer)
        val data = ByteArray(packetBuffer.readableBytes())
        packetBuffer.readBytes(data)
        packetBuffer.release()
        this.send(data)
        LOGGER.debug("Sent: ${packet::class.simpleName}")
    }

    fun processUnknownCosmetic(packet: WSPacketClientUnknownCosmetic) {
        LOGGER.info("PLAYER: ${packet.playerId}")
        LunarSpoof.lunarUsers[packet.playerId] = LunarNameTagIcon(packet.color, packet.bl)
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
