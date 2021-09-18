package dev.isxander.lunarspoof.websocket.asset.packet

import dev.isxander.lunarspoof.utils.createHashBiMap
import dev.isxander.lunarspoof.websocket.asset.LunarAssetWebSocket
import dev.isxander.lunarspoof.websocket.asset.packet.impl.WSPacketClientUnknownCosmetic
import net.minecraft.network.PacketBuffer

abstract class AbstractWebSocketPacket {
    protected val LOGGER = LunarAssetWebSocket.LOGGER

    abstract fun unknown(buf: PacketBuffer)
    abstract fun processBuf(buf: PacketBuffer)
    abstract fun processSocket(socket: LunarAssetWebSocket)

    fun writeKey(buf: PacketBuffer, array: ByteArray) {
        buf.writeShort(array.size)
        buf.writeBytes(array)
    }

    fun readKey(buf: PacketBuffer): ByteArray {
        val key = buf.readShort()
        if (key < 0) {
            LOGGER.error("Invalid key!")
            return ByteArray(0)
        }
        val data = ByteArray(key.toInt())
        buf.readBytes(data)
        return data
    }

    companion object {
        val REGISTRY = createHashBiMap(
            WSPacketClientUnknownCosmetic::class.java to 8
        )
    }
}