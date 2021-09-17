package dev.isxander.lunarspoof.websocket.auth.packet

import dev.isxander.lunarspoof.websocket.auth.LunarAuthWebSocket
import gg.essential.api.utils.JsonHolder

abstract class LunarPacket {
    abstract val name: String
    abstract fun process(ws: LunarAuthWebSocket)

    open fun processJson(json: JsonHolder) {}
}