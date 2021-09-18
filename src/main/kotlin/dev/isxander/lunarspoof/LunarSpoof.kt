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
import org.lwjgl.Sys
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
        startAssetWebSocket()
    }

    private fun startAuthSocket(consumer: (String?) -> Unit) {
        try {
            LOGGER.info("Starting Authentication WebSocket...")
            var username = "XanderDevs"
            var playerId = "90a8ada2-5422-4c65-93d2-0994ba5bbc8d"
            if (!EssentialAPI.getMinecraftUtil().isDevelopment()) {
                username = mc.session.username
                playerId = mc.session.playerID
            }

            LunarAuthWebSocket(
                mapOf(
                    "username" to username,
                    "playerId" to playerId,
                ),
                consumer
            ).connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun startAssetWebSocket() {
        Multithreading.runAsync {
            try {
                if (assetSocket != null && assetSocket!!.isOpen) {
                    assetSocket!!.closeBlocking()
                }
                startAuthSocket { auth ->
                    LOGGER.info("Starting Asset WebSocket...")
                    LOGGER.debug("Authentication: $auth")
                    val httpHeaders = mapOf(
                        "accountType" to mc.session.sessionType.name,
                        "version" to "1.8.9",
                        "gitCommit" to "6f9eb864d0d24cafb109d638a849d13ad67d5979",
                        "branch" to "master",
                        "os" to System.getProperty("os.name"),
                        "arch" to System.getProperty("os.arch", System.getenv("PROCESSOR_ARCHITECTURE")),
                        "server" to (mc.currentServerData?.serverIP ?: ""),
                        "launcherVersion" to "2.7.5",
                        "username" to mc.session.username,
                        "playerId" to mc.session.playerID,
                        "Authorization" to auth!!,
                        "protocolVersion" to "1"
                    )
                    LOGGER.debug("Headers: ")
                    LOGGER.debug(httpHeaders)
                    try {
                        LunarAssetWebSocket(httpHeaders).also { assetSocket = it }.connect()
                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                    }
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}

val mc: Minecraft
    get() = Minecraft.getMinecraft()