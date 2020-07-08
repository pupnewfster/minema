package info.ata4.minecraft.minema.client.config;

import java.nio.file.Path;
import info.ata4.minecraft.minema.Minema;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.loading.FMLPaths;

public class MekanismConfigHelper {

    public static final Path CONFIG_DIR;

    static {
        CONFIG_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(Minema.MODID), Minema.MODID);
    }

    /**
     * Creates a mod config so that {@link net.minecraftforge.fml.config.ConfigTracker} will track it and sync server configs from server to client.
     */
    public static void registerConfig(ModContainer modContainer, IMekanismConfig config) {
        ConfigHandler modConfig = new ConfigHandler(modContainer, config);
        if (config.addToContainer()) {
            modContainer.addConfig(modConfig);
        }
    }
}