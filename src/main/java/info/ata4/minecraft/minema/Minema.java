package info.ata4.minecraft.minema;

import info.ata4.minecraft.minema.client.config.ConfigHandler;
import info.ata4.minecraft.minema.client.config.MinemaConfig;
import java.nio.file.Path;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * Most of the files in this repo do have the old copyright notice about Barracuda even though I have touched most of it, in some cases substantially. Few classes do not
 * contain the notice, these are the ones that I have written completely myself or some of the class with substantial changes.
 *
 * @author Gregosteros (minecraftforum) / daipenger (github)
 */
@Mod(Minema.MODID)
public class Minema {

    public static final String MODID = "minema";
    public static final Path CONFIG_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(Minema.MODID), Minema.MODID);

    public static Minema instance;
    private final MinemaConfig config;

    public Minema() {
        instance = this;
        ModContainer modContainer = ModLoadingContext.get().getActiveContainer();
        modContainer.addConfig(new ConfigHandler(modContainer, config = new MinemaConfig()));
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onConfigLoad);
    }

    public MinemaConfig getConfig() {
        return config;
    }

    private void onConfigLoad(ModConfigEvent configEvent) {
        //Note: We listen to both the initial load and the reload, to make sure that we fix any accidentally
        // cached values from calls before the initial loading
        ModConfig config = configEvent.getConfig();
        //Make sure it is for the same modid as us
        if (config.getModId().equals(MODID) && config instanceof ConfigHandler handler) {
            handler.clearCache();
        }
    }
}