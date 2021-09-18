package dev.isxander.lunarspoof.websocket.asset.packet.impl

import dev.isxander.lunarspoof.websocket.asset.LunarAssetWebSocket
import dev.isxander.lunarspoof.websocket.asset.packet.AbstractWebSocketPacket
import net.minecraft.network.PacketBuffer
import java.util.*

class WSPacketClientUnknownCosmetic : AbstractWebSocketPacket() {
    lateinit var playerId: UUID

    override fun unknown(buf: PacketBuffer) {
    }

    override fun processBuf(buf: PacketBuffer) {
        playerId = UUID(buf.readLong(), buf.readLong())
    }

    override fun processSocket(socket: LunarAssetWebSocket) {
        socket.processUnknownCosmetic(this)
    }
}