package dev.isxander.lunarspoof.websocket.asset.packet.impl

import dev.isxander.lunarspoof.websocket.asset.LunarAssetWebSocket
import dev.isxander.lunarspoof.websocket.asset.packet.AbstractWebSocketPacket
import net.minecraft.network.PacketBuffer
import java.util.*
import kotlin.properties.Delegates

class WSPacketClientUnknownCosmetic : AbstractWebSocketPacket() {
    lateinit var playerId: UUID
    var color by Delegates.notNull<Int>()
    var bl by Delegates.notNull<Boolean>()

    override fun write(buf: PacketBuffer) {
    }

    override fun read(buf: PacketBuffer) {
        playerId = UUID(buf.readLong(), buf.readLong())

        repeat(buf.readVarIntFromBuffer()) {
            buf.readVarIntFromBuffer()
            buf.readBoolean()
        }

        color = buf.readInt()
        bl = buf.readBoolean()

    }

    override fun handle(socket: LunarAssetWebSocket) {
        socket.processUnknownCosmetic(this)
    }
}