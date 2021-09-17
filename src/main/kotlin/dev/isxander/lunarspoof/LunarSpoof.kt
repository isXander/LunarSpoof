package dev.isxander.lunarspoof

import com.google.common.collect.ImmutableMap
import dev.isxander.lunarspoof.websocket.asset.LunarAssetWebSocket
import dev.isxander.lunarspoof.websocket.auth.LunarAuthWebSocket
import gg.essential.api.EssentialAPI
import gg.essential.api.utils.Multithreading
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.apache.logging.log4j.LogManager
import java.net.URISyntaxException

@Mod(modid = LunarSpoof.ID, name = LunarSpoof.NAME, version = LunarSpoof.VERSION, clientSideOnly = true, modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter")
object LunarSpoof {
    const val NAME = "__GRADLE_NAME__"
    const val ID = "__GRADLE_ID__"
    const val VERSION= "__GRADLE_VERSION__"

    val LOGGER = LogManager.getLogger("LunarSpoof")

    private var assetSocket: LunarAssetWebSocket? = null

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        Multithreading.runAsync { startAuthSocket { println(it) } }
//        startAssetWebSocket()
    }

    private fun startAuthSocket(consumer: (String?) -> Unit) {
        try {
            LOGGER.info("Starting Authentication WebSocket...")
            var username = "XanderDevs"
            var playerId = "90a8ada2-5422-4c65-93d2-0994ba5bbc8d"
//            if (!EssentialAPI.getMinecraftUtil().isDevelopment()) {
//                username = mc.session.username
//                playerId = mc.session.playerID
//            }

            LunarAuthWebSocket(
                mapOf(
                    "username" to username,
                    "playerId" to playerId,
                ),
                consumer
            ).connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            consumer(null)
        }
    }

//    private fun startAssetWebSocket() {
//        Multithreading.runAsync {
//            try {
//                if (assetSocket != null && assetSocket.isOpen()) {
//                    assetSocket.closeBlocking()
//                }
//                startAuthSocket { auth ->
//                    LOGGER.info("Starting Asset WebSocket...")
//                    if (auth == null) return@startAuthSocket
//                    val httpHeaders = mutableMapOf<String, String>()
//                    httpHeaders["accountType"] = mc.session.sessionType.name()
//                    httpHeaders["version"] = "1.8.9"
//                    httpHeaders["commitId"] = "6f9eb864d0d24cafb109d638a849d13ad67d5979"
//                    httpHeaders["branch"] = "master"
//                    httpHeaders["os"] = System.getProperty("os.name")
//                    httpHeaders["arch"] = System.getProperty("os.arch", System.getenv("PROCESSOR_ARCHITECTURE"))
//                    val server = mc.currentServerData
//                    httpHeaders["server"] = if (server == null) "" else server.serverIP
//                    httpHeaders["launcherVersion"] = "2.7.1"
//                    httpHeaders["username"] = mc.session.username
//                    httpHeaders["playerId"] = mc.session.playerID
//                    httpHeaders["Authorization"] = auth
//                    httpHeaders["protocolVersion"] = "1"
//                    try {
//                        LunarAssetWebSocket(httpHeaders).also { assetSocket = it }.connect()
//                    } catch (e: URISyntaxException) {
//                        e.printStackTrace()
//                    }
//                }
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//        }
//    }
}

val mc: Minecraft
    get() = Minecraft.getMinecraft()