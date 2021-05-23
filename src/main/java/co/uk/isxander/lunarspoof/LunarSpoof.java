package co.uk.isxander.lunarspoof;

import club.sk1er.mods.core.ModCoreInstaller;
import club.sk1er.mods.core.util.MinecraftUtils;
import co.uk.isxander.lunarspoof.websocket.LunarAuthWebSocket;
import co.uk.isxander.xanderlib.XanderLib;
import co.uk.isxander.xanderlib.utils.Constants;
import com.google.common.collect.ImmutableMap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.util.function.Consumer;

@Mod(modid = LunarSpoof.MOD_ID, name = LunarSpoof.MOD_NAME, version = LunarSpoof.MOD_VERSION, clientSideOnly = true)
public class LunarSpoof implements Constants {

    public static final String MOD_ID = "lunarspoof";
    public static final String MOD_NAME = "LunarSpoof";
    public static final String MOD_VERSION = "2.0";

    public static final Logger LOGGER = LogManager.getLogger("LunarSpoof");

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModCoreInstaller.initializeModCore(mc.mcDataDir);
        XanderLib.getInstance().initPhase();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onJoin(EntityJoinWorldEvent event) {
        if (event.entity == mc.thePlayer) {
            try {
                LOGGER.info("Starting Websocket...");
                String username = "XanderDevs";
                String playerId = "90a8ada2-5422-4c65-93d2-0994ba5bbc8d";
                if (!MinecraftUtils.isDevelopment()) {
                    username = mc.getSession().getUsername();
                    playerId = mc.getSession().getPlayerID();
                }

                (new LunarAuthWebSocket(new ImmutableMap.Builder<String, String>().put("username", username).put("playerId", playerId).build(), LOGGER::info)).connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

}
